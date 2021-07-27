package tech.yiren.ystart.lowcode.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.service.BaseService;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class BaseServiceImpl implements BaseService {
    /**
     * 设置集合名称
     */
    private static final String COLLECTION_NAME = "users";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public IPage listByPage(PageDto pageDto) {
//        DBObject filterDBObject=new BasicDBObject();
//        filterDBObject.put("_id", 0);
//        filterDBObject.put("cname",1);
//        filterDBObject.put("onumber",1);
//        //排序
//        DBObject sortDBObject=new BasicDBObject();
//        sortDBObject.put("onumber",1);
//        DBObject queryObject = new BasicDBObject("cname","zcy");

        int pageSize = pageDto.getPageSize();
        long offset = (pageDto.getPageNo() - 1) * pageSize;
        // 创建条件对象
//        String sex = "男";
        String sort = "_id";
        Criteria criteria = new Criteria(); // Criteria.where("sex").is(sex);

        Query query = new Query(criteria).with(Sort.by(sort)).skip(offset).limit(pageSize);

        List<Object> records = mongoTemplate.find(query, Object.class, COLLECTION_NAME);

        IPage iPage = new Page();
        iPage.setRecords(records);
        iPage.setCurrent(pageDto.getPageNo());
        long total = mongoTemplate.count(new Query(criteria), Object.class, COLLECTION_NAME);
        iPage.setTotal(total);
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
    public Object getById(String id) {
        return mongoTemplate.findById(id, Object.class, COLLECTION_NAME);
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
            if(next.getKey().equals("_id")) {
                _id = next.getValue();
            } else {
                update.set(next.getKey(),next.getValue());
            }
        }

        if(null == _id) {
            return mongoTemplate.save(object, COLLECTION_NAME);
        } else {
            Criteria criteria = Criteria.where("_id").is(_id);
            Query query = new Query(criteria);
            mongoTemplate.updateFirst(query,update,COLLECTION_NAME);
            return mongoTemplate.findById(_id,Object.class,COLLECTION_NAME);
        }
    }
}
