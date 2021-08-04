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
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Trigger;
import tech.yiren.ystart.lowcode.service.TriggerService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
public class TriggerServiceImpl implements TriggerService {
	private static final String COLLECTION_TRIGGER_NAME = "trigger";
	private static final String COLLECTION_MODEL_NAME = "model";
	@Resource
	private MongoTemplate mongoTemplate;
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
			return mongoTemplate.save(object, COLLECTION_TRIGGER_NAME);
		} else {
			Criteria criteria = Criteria.where("_id").is(_id);
			Query query = new Query(criteria);
			mongoTemplate.updateFirst(query, update, COLLECTION_TRIGGER_NAME);
			return mongoTemplate.findById(_id, Object.class, COLLECTION_TRIGGER_NAME);
		}
	}

	@Override
	public Object getRelativeApproveLow(String _id) {

		return null;
	}

	@Override
	public Object listByPage(PageDto pageDto) {


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
			long total = mongoTemplate.count(query, Object.class, COLLECTION_TRIGGER_NAME);
			iPage.setTotal(total);
			List<Object> records = mongoTemplate.find(query.with(Sort.by(sort)).skip(offset).limit(pageSize), Object.class, COLLECTION_TRIGGER_NAME);
			iPage.setRecords(records);
			iPage.setCurrent(pageDto.getPageNo());
			iPage.setSize(pageSize);
			iPage.setPages(total / pageSize + (total % pageSize > 0 ? 1 : 0));
			return iPage;
	}

	@Override
	public Object deleteById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		// 创建查询对象，然后将条件对象添加到其中
		Query query = new Query(criteria);
		return mongoTemplate.findAndRemove(query, Object.class, COLLECTION_TRIGGER_NAME);
	}

	@Override
	public Object updateById(String id, Trigger trigger) {
		System.out.println(id				+trigger);
		Assert.isTrue(ObjectUtil.isNotNull(trigger.get_id()), "缺少主键");
		trigger.set_id(id);

		return mongoTemplate.save(trigger, COLLECTION_TRIGGER_NAME);
	}
}
