package tech.yiren.ystart.lowcode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Model;
import tech.yiren.ystart.lowcode.entity.Relative;
import tech.yiren.ystart.lowcode.service.RelativeService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;


@Service
public class RelativeServiceImpl implements RelativeService {

    private static final String COLLECTION_NAME = "relative";
    private static final String COLLECTION_MODEL_NAME = "model";
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public IPage listByPage( PageDto pageDto) {
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
        long total = mongoTemplate.count(query, Relative.class, COLLECTION_NAME);
        iPage.setTotal(total);
        List<Relative> records = mongoTemplate.find(query.with(Sort.by(sort)).skip(offset).limit(pageSize), Relative.class, COLLECTION_NAME);
        iPage.setRecords(records);
        iPage.setCurrent(pageDto.getPageNo());

        iPage.setSize(pageSize);
        iPage.setPages(total / pageSize + (total % pageSize > 0 ? 1 : 0));
        return iPage;
    }

    @Override
    public List list( FilterDto filterDto) {
        Query query = new Query();
        if (null != filterDto.getFilter() && filterDto.getFilter().size() > 0) {
            filterDto.getFilter().forEach((k, v) -> {
                query.addCriteria(Criteria.where((String) k).is(v));
            });
        }
        return mongoTemplate.find(query, Relative.class, COLLECTION_NAME);
    }

    @Override
    public Relative getById( String id) {
        return mongoTemplate.findById(id, Relative.class, COLLECTION_NAME);
    }

    @Override
    public Object getByCode(String code) {
        Criteria criteria = Criteria.where("fieldCode").is(code);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        Relative object = mongoTemplate.findOne(query, Relative.class, COLLECTION_NAME);
        return  object;
    }

    @Override
    public Relative insert( Relative relative) {
        Assert.isTrue(ObjectUtil.isNotNull(relative.getModelId()), "缺少模型参数");
        Model model = mongoTemplate.findById(relative.getModelId(), Model.class, COLLECTION_MODEL_NAME);
        Assert.isTrue(ObjectUtil.isNotNull(model), "模型不存在");
        relative.setModelCode(model.getCode());
        relative.setCreatetime(LocalDate.now());
        return mongoTemplate.insert(relative, COLLECTION_NAME);
    }

    @Override
    public Relative deleteById( String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAndRemove(query, Relative.class, COLLECTION_NAME);
    }

    @Override
    public List<Relative> delete( Relative relative) {
        Criteria criteria = new Criteria();
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findAllAndRemove(query, Relative.class, COLLECTION_NAME);
    }

    @Override
    public Relative update( Relative relative) {
        Assert.isTrue(ObjectUtil.isNotNull(relative.get_id()), "缺少主键");
        Criteria criteria = Criteria.where("_id").is(relative.get_id());
        Query query = new Query(criteria);
        Update update = new Update();
        mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return mongoTemplate.findById(relative.get_id(), Relative.class, COLLECTION_NAME);
    }

    @Override
    public Relative updateById( String id, Relative relative) {
        Assert.isTrue(ObjectUtil.isNotNull(relative.get_id()), "缺少主键");
        relative.set_id(id);
        relative.setUpdatetime(LocalDate.now());

       return mongoTemplate.save(relative, COLLECTION_NAME);
    }

    @Override
    public Relative save( Relative relative) {
        return mongoTemplate.save(relative, COLLECTION_NAME);
    }
}
