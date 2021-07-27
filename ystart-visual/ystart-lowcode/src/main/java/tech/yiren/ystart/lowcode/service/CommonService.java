package tech.yiren.ystart.lowcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;

import java.util.List;


public interface CommonService<T> {

    IPage<T> listByPage(String collectName, PageDto pageDto);


    List<T> list(String collectName, FilterDto filterDto);


    T getById(String collectName, String id);

    T insert(String collectName, T object);


    T deleteById(String collectName, String id);

    List<T> delete(String collectName, T object);

    T update(String collectName, T object);

    T updateById(String collectName, String id, T object);

    T save(String collectName, T object);
}

