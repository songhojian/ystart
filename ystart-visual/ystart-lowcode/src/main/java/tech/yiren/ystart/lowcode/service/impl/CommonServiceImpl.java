package tech.yiren.ystart.lowcode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.yiren.ystart.common.core.constant.CacheConstants;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.service.CommonService;

import javax.annotation.Resource;
import java.util.*;


@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public IPage listByPage(String collectName, PageDto pageDto) {
        int pageSize = pageDto.getPageSize();
        long offset = (pageDto.getPageNo() - 1) * pageSize;
        // 创建条件对象
        String sort = "_id";
        Query query = new Query();
        if (null != pageDto.getFilter() && pageDto.getFilter().size() > 0) {
            pageDto.getFilter().forEach((k, v) -> {
            	String condition = (String) k;
            	if(condition.startsWith("$=:")){
					condition = condition.substring(3);
					query.addCriteria(Criteria.where(condition).is(v));// bug to fix
				}else if(condition.startsWith("$≠:")){
					condition = condition.substring(3);
					query.addCriteria(Criteria.where(condition).ne(v));
				}else if(condition.startsWith("$>:")){
					condition = condition.substring(3);
					query.addCriteria(Criteria.where(condition).gt(v));
				}else if(condition.startsWith("$<:")){
					condition = condition.substring(3);
					query.addCriteria(Criteria.where(condition).lt(v));
				}else if(condition.startsWith("$>=:")){
					condition = condition.substring(4);
					query.addCriteria(Criteria.where(condition).gte(v));
				}else if(condition.startsWith("$<=:")){
					condition = condition.substring(4);
					query.addCriteria(Criteria.where(condition).lte(v));
				}else if(condition.startsWith("$like:")){
					condition = condition.substring(6);
					query.addCriteria(Criteria.where(condition).regex("^.*"+v+".*$"));
				}else if(condition.startsWith("$∈:")){
					condition = condition.substring(3);
					query.addCriteria(Criteria.where(condition).in(v));
				}else{
					query.addCriteria(Criteria.where(condition).is(v));
				}
            });
        }
        IPage iPage = new Page();
        long total = mongoTemplate.count(query, Object.class, collectName);
        iPage.setTotal(total);
        List<HashMap> records = mongoTemplate.find(query.with(Sort.by(sort)).skip(offset).limit(pageSize), HashMap.class, collectName);

        // relativesMasters / relativesRefers
        String cacheKey = CacheConstants.DEFAULT_CODE_KEY + "_R_" + collectName;

        if (!redisTemplate.hasKey(cacheKey)) {
        } else {
            JSONObject modelRelative = (JSONObject) redisTemplate.opsForValue().get(cacheKey);
            List<JSONObject> relativesMasters = (List<JSONObject>) modelRelative.get("relativesMasters");
            List<JSONObject> relativesRefers = (List<JSONObject>) modelRelative.get("relativesRefers");
            for (int i = 0; i < records.size(); i++) {
                HashMap hashMapRecord = (HashMap) records.get(i);
                for (int j = 0; j < relativesMasters.size(); j++) {
                    JSONObject masterHasMap = relativesMasters.get(j);
                    Query queryMaster = new Query();
                    queryMaster.addCriteria(Criteria.where(masterHasMap.get("fieldCode").toString()).is(hashMapRecord.get("_id").toString()));
                    List<HashMap> masters = mongoTemplate.find(queryMaster, HashMap.class, masterHasMap.get("modelCode").toString());
                    if (null != masters && masters.size() > 0) {
                        hashMapRecord.put(masterHasMap.get("modelCode").toString() + "____" + masterHasMap.get("fieldCode").toString(), masters);
                    } else {
                        hashMapRecord.put(masterHasMap.get("modelCode").toString() + "____" + masterHasMap.get("fieldCode").toString(), new ArrayList<>());
                    }
                }
                for (int k = 0; k < relativesRefers.size(); k++) {
                    JSONObject objMapRef = relativesRefers.get(k);
                    if (null != hashMapRecord.get(objMapRef.get("prop").toString())) {
                        Query queryMaster = new Query();
                        String objId = hashMapRecord.get(objMapRef.get("prop").toString()).toString();
                        queryMaster.addCriteria(Criteria.where("_id").is(objId));
                        HashMap refObj = mongoTemplate.findOne(queryMaster, HashMap.class, objMapRef.get("prop").toString());
                        hashMapRecord.put(objMapRef.get("prop").toString() + "__REFOBJ", refObj);
                    } else {
                        hashMapRecord.put(objMapRef.get("prop").toString() + "__REFOBJ", new HashMap<>());
                    }
                }
            }
        }

        iPage.setRecords(records);
        iPage.setCurrent(pageDto.getPageNo());

        iPage.setSize(pageSize);
        iPage.setPages(total / pageSize + (total % pageSize > 0 ? 1 : 0));
        return iPage;
    }

    @Override
    public List list(String collectName, FilterDto filterDto) {
        Query query = new Query();
        if (null != filterDto.getFilter() && filterDto.getFilter().size() > 0) {
            filterDto.getFilter().forEach((k, v) -> {
                query.addCriteria(Criteria.where((String) k).is(v));
            });
        }
        return mongoTemplate.find(query, Object.class, collectName);
    }

    @Override
    public Object getById(String collectName, String id) {
        return mongoTemplate.findById(id, Object.class, collectName);
    }

    @Override
    public Object insert(String collectName, Object object) {
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        HashMap<String, Object> insertMap = new HashMap();
        List<HashMap<String, Object>> relObjs = new ArrayList<>();

        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (key.indexOf("____") != -1) { //"edu_exp____student"
                // 关联对象信息
                HashMap<String, Object> rel = new HashMap<>();
                String[] temp = key.split("____");
                rel.put("field", temp[1]); // student
                rel.put("model", temp[0]); // edu_exp
                rel.put("data", value);
                relObjs.add(rel);
            }
            if (!key.equals("_id") && !StrUtil.startWith(key, "$")) {
                insertMap.put(key, value);
            }

        }

        insertMap.put("createtime", new Date()); // 新建时间
        HashMap objResult = (HashMap) mongoTemplate.insert(insertMap, collectName);
        // 处理 Relative 对象
        this.saveRelativeObj(relObjs, objResult.get("_id").toString());
        return objResult;
    }

    @Override
    public Object deleteById(String collectName, String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAndRemove(query, Object.class, collectName);
    }

    @Override
    public List<Object> delete(String collectName, Object object) {
        Criteria criteria = new Criteria();
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAllAndRemove(query, Object.class, collectName);
    }

    @Override
    public Object update(String collectName, Object object) {
        Object _id = null;
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (key.equals("_id") && ObjectUtil.isNotEmpty(value)) {
                _id = value;
            } else if (!StrUtil.startWith(key, "$")) {
                update.set(key, value);
            }
        }
        Assert.isTrue(ObjectUtil.isNotNull(_id), "缺少主键");
        Criteria criteria = Criteria.where("_id").is(_id);
        Query query = new Query(criteria);
        mongoTemplate.updateFirst(query, update, collectName);
        return mongoTemplate.findById(_id, Object.class, collectName);
    }

    private boolean saveRelativeObj(List<HashMap<String, Object>> relObjs, String relativeObjId) {
        try {
            List<ObjectId> ids = new ArrayList<>();
            if (relObjs.size() > 0) {
                for (int i = 0; i < relObjs.size(); i++) {
                    HashMap hashMap = relObjs.get(i);
                    List<LinkedHashMap> relData = (List<LinkedHashMap>) hashMap.get("data");
                    for (int j = 0; j < relData.size(); j++) {
                        LinkedHashMap hashMap1 = relData.get(j);
                        hashMap1.put(hashMap.get("field").toString(), relativeObjId);
//                        if(null != hashMap1.get("_id")) {
//                            HashMap exist = mongoTemplate.findById(hashMap1.get("_id"),HashMap.class, hashMap.get("model").toString());
//                            if(null != exist) {
//                                mongoTemplate.insert(relData, hashMap.get("model").toString());
//                            } else {
//                                mongoTemplate.update(relData, hashMap.get("model").toString());
//                            }
//                        } else {
//                            mongoTemplate.insert(relData, hashMap.get("model").toString());
//                        }
                        if (null != hashMap1.get("_id")) {
                            hashMap1.put("_id", new ObjectId(hashMap1.get("_id").toString()));
                        }
                        LinkedHashMap retHasmap = mongoTemplate.save(hashMap1, hashMap.get("model").toString());
                        ids.add((ObjectId) retHasmap.get("_id"));
                    }
                    Query query = new Query().addCriteria(Criteria.where(hashMap.get("field").toString()).is(relativeObjId).and("_id").nin(ids));
                    mongoTemplate.remove(query, hashMap.get("model").toString());
                }

            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

    }

    @Override
    public Object updateById(String collectName, String id, Object object) {
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        List<HashMap<String, Object>> relObjs = new ArrayList<>();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (key.indexOf("____") != -1) { //"edu_exp____student"
                // 关联对象信息
                HashMap<String, Object> rel = new HashMap<>();
                String[] temp = key.split("____");
                rel.put("field", temp[1]); // student
                rel.put("model", temp[0]); // edu_exp
                rel.put("data", value);
                relObjs.add(rel);
            }
            if (!key.equals("_id") && !StrUtil.startWith(key, "$")) {
                update.set(key, value);
            }
        }

        update.set("updatetime", new Date()); // 新增加修改时间

        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.updateFirst(query, update, collectName);
        HashMap ret = mongoTemplate.findById(id, HashMap.class, collectName);

        this.saveRelativeObj(relObjs, ret.get("_id").toString());
        return ret;
    }

    @Override
    public Object save(String collectName, Object object) {
        Object _id = null;
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            System.out.println("key=" + next.getKey() + " value=" + next.getValue());
            if (next.getKey().equals("_id")) {
                if (next.getValue().toString() != "") {
                    _id = next.getValue();
                }
            } else {
                update.set(next.getKey(), next.getValue());
            }
        }

        if (null == _id) {
            return mongoTemplate.save(object, collectName);
        } else {
            Criteria criteria = Criteria.where("_id").is(_id);
            Query query = new Query(criteria);
            mongoTemplate.updateFirst(query, update, collectName);
            return mongoTemplate.findById(_id, Object.class, collectName);
        }
    }
}
