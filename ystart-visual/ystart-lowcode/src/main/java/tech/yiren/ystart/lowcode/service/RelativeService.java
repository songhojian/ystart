package tech.yiren.ystart.lowcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Relative;

import java.util.List;


public interface RelativeService<T> {

    IPage<T> listByPage(PageDto pageDto);


    List<T> list(FilterDto filterDto);


    T getById(String id);

    T getByCode(String code,String relative);

    T insert(Relative object);

    T deleteById(String id);

    List<T> delete(Relative object);

    T update(Relative object);

    T updateById(String id, Relative object);

    T save(Relative object);
}

