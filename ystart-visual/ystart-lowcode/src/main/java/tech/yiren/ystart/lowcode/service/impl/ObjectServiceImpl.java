package tech.yiren.ystart.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.yiren.ystart.common.core.constant.CacheConstants;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.MainData;
import tech.yiren.ystart.lowcode.entity.Model;
import tech.yiren.ystart.lowcode.entity.Relative;
import tech.yiren.ystart.lowcode.service.ObjectService;

import javax.annotation.Resource;
import java.util.*;


@Service
public class ObjectServiceImpl implements ObjectService {

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 设置集合名称
     */
    private static final String COLLECTION_NAME = "model";
    private static final String COLLECTION_RELATIVE_NAME = "relative";
    private static final String COLLECTION_MAINDATA_NAME = "mainData";

    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public IPage listByPage(PageDto pageDto) {

        int pageSize = pageDto.getPageSize();
        long offset = (pageDto.getPageNo() - 1) * pageSize;
        // 创建条件对象
        String sort = "_id";
        Query query = new Query();
        if (null != pageDto.getFilter() && pageDto.getFilter().size() > 0) {
            pageDto.getFilter().forEach((k, v) -> {
                query.addCriteria(Criteria.where((String) k).is(v));
            });
        }
        IPage iPage = new Page();
        long total = mongoTemplate.count(query, Object.class, COLLECTION_NAME);
        iPage.setTotal(total);
        List<Object> records = mongoTemplate.find(query.with(Sort.by(sort)).skip(offset).limit(pageSize), Object.class, COLLECTION_NAME);
        iPage.setRecords(records);
        iPage.setCurrent(pageDto.getPageNo());
        iPage.setSize(pageSize);
        iPage.setPages(total / pageSize + (total % pageSize > 0 ? 1 : 0));
        return iPage;
    }

    @Override
    public List list(FilterDto filterDto) {

        Criteria criteria = new Criteria();
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Object.class, COLLECTION_NAME);
    }

    @Override
    public List listFeilds(String _id) {
        Criteria criteria = Criteria.where("modelId").is(_id);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Relative.class, COLLECTION_RELATIVE_NAME);
    }

    @Override
    public Object getById(String id) {
        return mongoTemplate.findById(id, Object.class, COLLECTION_NAME);
    }

    @Override
    public Object getByCode(String code) {
        Criteria criteria = Criteria.where("code").is(code);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        Model object = mongoTemplate.findOne(query, Model.class, COLLECTION_NAME);
        return  object;
    }


    @Override
    public Object getConfigByCode(String code) {
        Criteria criteria = Criteria.where("code").is(code);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);

        Model object = mongoTemplate.findOne(query, Model.class, COLLECTION_NAME);

        Query queryRelative = new Query();
        queryRelative.addCriteria(Criteria.where("modelCode").is(code));
        List<Relative> relatives = mongoTemplate.find(queryRelative, Relative.class, COLLECTION_RELATIVE_NAME);

        HashMap<String, Object> ret = new HashMap<>();
        ret.put("model", object);
        ret.put("relatives", relatives);
        List<HashMap> relativColumns = new ArrayList<>();
        List<HashMap> relativSlots = new ArrayList<>();
        List<HashMap> relativesRefers = new ArrayList<>();
        String[] slotsRelativeTypes = new String[]{"attachment", "rich-text", "field-reference", "master-slave"};
        for (int i = 0; i < relatives.size(); i++) {
            Relative relative = relatives.get(i);
            HashMap hashMap = this.conventViewJson(relative);
            relativColumns.add(hashMap);
            if (Arrays.stream(slotsRelativeTypes).anyMatch(relative.getFieldType()::equals)) {
                relativSlots.add(hashMap);
            }
            if(relative.getFieldType().equals("field-reference")) {
                relativesRefers.add(hashMap);
            }
        }
        // 查询从主关联的对象
        List<Relative> relativesMasters = this.getMasterSlaveObject(code);
        for (int i = 0; i < relativesMasters.size(); i++) {
            Relative relative = relativesMasters.get(i);
            Query queryRelative1 = new Query();
            queryRelative1.addCriteria(Criteria.where("modelId").is(relative.getModelId()));
            List<Relative> relativeMasters = mongoTemplate.find(queryRelative1, Relative.class, COLLECTION_RELATIVE_NAME);
            List<HashMap> relativColumnMasters = new ArrayList<>();
            for (int j = 0; j < relativeMasters.size(); j++) {
                Relative relativeTmp = relativeMasters.get(j);

                HashMap hashMap = this.conventViewJson(relativeTmp);
                hashMap.remove("search");
                if (relative.getFieldCode().equals(relativeTmp.getFieldCode())) {
                    hashMap.put("hide", true); // 隐藏列
                }
                relativColumnMasters.add(hashMap);
            }

            HashMap<String, Object> relativeChild = new HashMap<>();
            relativeChild.put("column", relativColumnMasters);
            relativeChild.put("rowAdd", "eval( (done) => {done()})");
            relativeChild.put("rowDel", "eval( (row, done) => {done()})");


            HashMap<String, Object> relativeColumn = new HashMap<>();
            relativeColumn.put("children", relativeChild);
            relativeColumn.put("label", relative.getFieldName());
            relativeColumn.put("prop", relative.getModelCode() + "____" +relative.getFieldCode());
            relativeColumn.put("type", "dynamic");
            relativeColumn.put("relativeObj", relative.getModelCode());
            relativeColumn.put("span", 24);
            relativColumns.add(relativeColumn);
        }

        ret.put("relativesMasters", relativesMasters);
        ret.put("relativesRefers", relativesRefers);
        ret.put("column", relativColumns);
        ret.put("slots", relativSlots);
        redisTemplate.opsForValue().set(CacheConstants.DEFAULT_CODE_KEY+"_R_"+code, JSON.toJSON(ret));
        return ret;
    }

    private HashMap conventViewJson(Relative relative) {
        HashMap<String, Object> relativeField = new HashMap<>();
        List<HashMap<String, Object>> rules = new ArrayList<>();

        relativeField.put("label", relative.getFieldName());
        relativeField.put("prop", relative.getFieldCode());
        relativeField.put("hide", false);
        relativeField.put("order", 0);

        if (null != relative.getHelpTip()) {
            relativeField.put("tip", relative.getHelpTip());
        }
        if (relative.getIsNull() != null && relative.getIsNull().toString().equals("1")) {
            // 0 允许为空，1不允许为空
            HashMap<String, Object> rule = new HashMap<>();
            rule.put("required", true);
            rule.put("message", relative.getFieldName() + "不允许为空");
            rules.add(rule);
        }
        switch (relative.getFieldType()) {
            case "input":
                relativeField.put("type", "input");
                relativeField.put("search", true);
                relativeField.put("showWordLimit", true);
                if (null != relative.getMaxLength()) {
                    relativeField.put("maxlength", relative.getMaxLength());
                }
                if (null != relative.getMinLength()) {
                    relativeField.put("minlength", relative.getMinLength());
                }
                break;
            case "boolean":
                relativeField.put("type", "radio");
                relativeField.put("search", true);
                List<HashMap> booleanDict = new ArrayList<>();

                HashMap<String, Object> yes = new HashMap<>();
                yes.put("label", (null != relative.getBooleanYes() && relative.getBooleanYes().isEmpty()) ? "是" : relative.getBooleanYes());
                yes.put("value", 1);
                booleanDict.add(yes);

                HashMap<String, Object> no = new HashMap<>();
                no.put("label", (null != relative.getBooleanNo() && relative.getBooleanNo().isEmpty()) ? "否" : relative.getBooleanNo());
                no.put("value", 0);
                booleanDict.add(no);

                relativeField.put("dicData", booleanDict);
                if (null != relative.getDefalutValBoolean()) {
                    relativeField.put("value", relative.getDefalutValBoolean());
                }
                break;
            case "textarea":
                relativeField.put("search", true);
                relativeField.put("span", 24);
                relativeField.put("showWordLimit", true);
                relativeField.put("type", "textarea");
                break;
            case "rich-text":
//                relativeField.put("search", true);
                relativeField.put("span", 24);
                relativeField.put("component", "avueUeditor");

                HashMap<String, Object> avueUeditorOption = new HashMap<>();
                avueUeditorOption.put("action", "/lowcode/common/upload");
                HashMap<String, Object> avueUeditorHeaders = new HashMap<>();
                avueUeditorHeaders.put("Authorization", "Bearer 9aedda97-589e-47fb-a7a0-f2ff64cc518d");
                avueUeditorOption.put("headers", avueUeditorHeaders);
                HashMap<String, Object> avueUeditorProps = new HashMap<>();
                avueUeditorProps.put("res", "data");
                avueUeditorProps.put("url", "url");
                avueUeditorOption.put("props", avueUeditorProps);


                HashMap<String, Object> avueUeditorParam = new HashMap<>();
                avueUeditorParam.put("options", avueUeditorOption);
                relativeField.put("params", avueUeditorParam);
                break;
            case "input-number-int":
                relativeField.put("type", "number");
                relativeField.put("className", "viewError");
                relativeField.put("step", 1);
                break;
            case "input-number-double":
                relativeField.put("type", "number");
//                relativeField.put("step",1);
                if (null != relative.getMaxValue()) {
                    relativeField.put("max-rows", relative.getMaxValue());
                }
                if (null != relative.getMinValue()) {
                    relativeField.put("min-rows", relative.getMinValue());
                }
                if (null != relative.getPrecision()) {
                    relativeField.put("precision", relative.getPrecision());
                }
            case "switch":
                relativeField.put("search", true);
                relativeField.put("type", "switch");

            case "input-number-money":
                relativeField.put("type", "number");
                relativeField.put("precision", 2);
                relativeField.put("step", 0.01);
                break;
            case "input-number-percent":
                relativeField.put("type", "number");
                relativeField.put("step", 1);
                relativeField.put("max-rows", 100);
                relativeField.put("min-rows", 0);
                relativeField.put("append", "%");
                break;
            case "date":
                relativeField.put("type", "date");
                relativeField.put("format", "yyyy-MM-dd");
                relativeField.put("valueFormat", "yyyy-MM-dd");
                if (null != relative.getDefalutValDate() && relative.getDefalutValDate().equals("fixed") && !relative.getDateFxied().isEmpty()) {
                    relativeField.put("value", relative.getDateFxied());
                } else if (null != relative.getDefalutValDate() && relative.getDefalutValDate().equals("offset")) {
                    // todo date offset
                }
                break;
            case "datetime":
                relativeField.put("type", "datetime");
                relativeField.put("format", "yyyy-MM-dd hh:mm:ss");
                relativeField.put("valueFormat", "yyyy-MM-dd hh:mm:ss");
                if (null != relative.getDefalutValDate() && relative.getDefalutValDate().equals("fixed") && !relative.getDateFxied().isEmpty()) {
                    relativeField.put("value", relative.getDateFxied());
                } else if (null != relative.getDefalutValDate() && relative.getDefalutValDate().equals("offset")) {
                    // todo date offset
                }
                break;
            case "select":
            case "select-multiple":
                HashMap<String, Object> dictProps = new HashMap<>();
                if (relative.getDataSource().equals("dict") && null != relative.getRelMainData()) {
                    Query queryMain = new Query();
                    queryMain.addCriteria(Criteria.where("_id").is(relative.getRelMainData()));
                    MainData mainData = mongoTemplate.findOne(queryMain, MainData.class, COLLECTION_MAINDATA_NAME);
                    if (null != mainData) {
                        relativeField.put("dicData", mainData.getOptions());
                        dictProps.put("label", "mainName");
                        dictProps.put("value", "mainCode");
                        relativeField.put("props", dictProps);
                    }
                } else if (relative.getDataSource().equals("obj") && null != relative.getRelObj() && null != relative.getRelObjField()) {
                    Query queryObj = new Query();
                    queryObj.fields().include("_id").include(relative.getRelObjField());
                    List<Object> objects = mongoTemplate.find(queryObj, Object.class, relative.getRelObj().toString());
                    relativeField.put("dicData", objects);
                    dictProps.put("label", relative.getRelObjField());
                    dictProps.put("value", "_id");
                    relativeField.put("props", dictProps);
                }
                relativeField.put("search", true);
                if (null != relative.getSelectStyle()) {
                    relativeField.put("type", relative.getSelectStyle());
                } else {
                    relativeField.put("type", "select");
                }
                if (relative.getFieldType().equals("select-multiple")) {
                    if (null != relative.getSelectStyle() && relative.getSelectMultipleStyle().equals("checkbox")) {
                        relativeField.put("type", "checkbox");
                    } else {
                        relativeField.put("type", "select");
                        relativeField.put("multiple", true);
                    }
                }
//                relativeField.put("dataType", "string");
                break;
            case "email":
                relativeField.put("type", "input");
                HashMap<String, Object> ruleEmail = new HashMap<>();
                ruleEmail.put("type", "email");
                ruleEmail.put("message", "邮箱格式不对");
                rules.add(ruleEmail);
                break;
            case "mobile":
                relativeField.put("type", "input");
                HashMap<String, Object> ruleMobile = new HashMap<>();
                ruleMobile.put("pattern", "eval(\"new RegExp('^1[0-9]{10}$|^[569][0-9]{7}$')\")");
                ruleMobile.put("message", "手机格式不对");
                rules.add(ruleMobile);
                break;
            case "telephone":
                relativeField.put("type", "input");
                HashMap<String, Object> ruleTel = new HashMap<>();
                ruleTel.put("pattern", "eval('/^(\\\\(\\\\d{3,4}\\\\)|\\\\d{3,4}-|\\\\s)?\\\\d{7,14}$/')");
                ruleTel.put("message", "固定电话格式不对");
                rules.add(ruleTel);
                break;
            case "url":
                HashMap<String, Object> ruleUrl = new HashMap<>();
                ruleUrl.put("type", "url");
                ruleUrl.put("message", "链接格式不对，应包含http://或者https://");
                rules.add(ruleUrl);
                break;
            case "image":
                relativeField.put("type", "upload");
                relativeField.put("listType", "picture-img");
                relativeField.put("loadText", "附件上传中，请稍等");
                relativeField.put("tip", "只能上传jpg/png");
                relativeField.put("accept", "image/*");
                relativeField.put("action", "/common/upload");
                relativeField.put("headers", JSON.parse("{\"Authorization\":\"Bearer\"}"));
                HashMap<String, Object> propsHttp = new HashMap<>();
                propsHttp.put("res", "data");
                relativeField.put("propsHttp", propsHttp);

                HashMap<String, Object> canvasOption = new HashMap<>();
                canvasOption.put("text", "水印");
                canvasOption.put("ratio", 0.1);
                relativeField.put("canvasOption", canvasOption); // 水印
                break;
            case "attachment":
                relativeField.put("type", "upload");
                relativeField.put("loadText", "附件上传中，请稍等");
//                relativeField.put("tip", "只能上传jpg/png");
//                relativeField.put("accept", "image/*");
//                relativeField.put("limit", 1);
                relativeField.put("action", "/common/upload");
                relativeField.put("headers", JSON.parse("{\"Authorization\":\"Bearer\"}"));
                HashMap<String, Object> propsHttpA = new HashMap<>();
                propsHttpA.put("res", "data");
                relativeField.put("propsHttp", propsHttpA);
                relativeField.put("span", 24);
                break;
            case "field-reference": // 字段引用
                relativeField.put("type", "table");
                relativeField.put("relObj", relative.getRelObj());
//                relativeField.put("children", );
//                relativeField.put("props", JSON.parse("{\"label\":\"name\",\"value\":\"id\"}"));
//                relativeField.put("onLoad", "eval(\"({ page, value, data }, callback) => {if(value){callback({id: '0', name: '张三', sex: '男'});return;} callback({total: 2,data: [{id: '0', name: '张三', sex: '男'}, {id: '1',name: '李四',sex: '女'}]})}\")");
//                relativeField.put("formatter", "eval(\"(row) => {if (!row.name){return ''}return row.name + '-' + row.sex}\")");
                break;
            case "master-slave":
                relativeField.put("slot", true);
                break;
        }
        relativeField.put("slot", true);
        relativeField.put("formslot", true);
        relativeField.put("rules", rules);
        return relativeField;
    }

    private List<Relative> getMasterSlaveObject(String objectName) {
        Query queryRel = new Query();
        queryRel.addCriteria(Criteria.where("fieldType").is("master-slave").and("relObj").is(objectName));
        List<Relative> relatives = mongoTemplate.find(queryRel, Relative.class, COLLECTION_RELATIVE_NAME);
        return relatives;
    }

    @Override
    public Object insert(Object object) {
        return mongoTemplate.insert(object, COLLECTION_NAME);
    }

    @Override
    public Object deleteById(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAndRemove(query, Object.class, COLLECTION_NAME);
    }

    @Override
    public List<Object> delete(Object object) {
        Criteria criteria = new Criteria();
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAllAndRemove(query, Object.class, COLLECTION_NAME);
    }

    @Override
    public Object update(Object object) {
        Criteria criteria = new Criteria();
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        // 创建更新对象,并设置更新的内容
//        Update update = new Update().set("age", 33).set("name", "zhangsansan");
        // 执行更新，如果没有找到匹配查询的文档，则创建并插入一个新文档
        // 输出结果信息
        return mongoTemplate.upsert(query, new Update(), Object.class, COLLECTION_NAME);
    }

    @Override
    public Object save(Object object) {
        Object _id = null;
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            System.out.println("key=" + next.getKey() + " value=" + next.getValue());
            if (next.getKey().equals("_id")) {
                _id = next.getValue();
            } else {
                update.set(next.getKey(), next.getValue());
            }
        }

        if (null == _id) {
            return mongoTemplate.save(object, COLLECTION_NAME);
        } else {
            Criteria criteria = Criteria.where("_id").is(_id);
            Query query = new Query(criteria);
            mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
            return mongoTemplate.findById(_id, Object.class, COLLECTION_NAME);
        }
    }

    @Override
    public Object updateById(String id, Object object) {
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entryIterator = ((LinkedHashMap) object).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (!key.equals("_id") && !StrUtil.startWith(key, "$")) {
                update.set(key, value);
            }
        }
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return mongoTemplate.findById(id, Object.class, COLLECTION_NAME);
    }
}
