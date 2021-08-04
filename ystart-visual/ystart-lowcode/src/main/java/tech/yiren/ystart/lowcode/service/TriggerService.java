package tech.yiren.ystart.lowcode.service;

import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Trigger;

public interface TriggerService<T> {

	T save(T object);
	T getRelativeApproveLow(String object);

	T listByPage(PageDto pageDto);

	T deleteById(String id);

	T updateById(String id, Trigger object);
}
