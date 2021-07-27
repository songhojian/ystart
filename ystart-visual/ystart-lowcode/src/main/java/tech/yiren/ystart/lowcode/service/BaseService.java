package tech.yiren.ystart.lowcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;

import java.util.List;


public interface BaseService<T> {

    /** 设置集合名称 */
    static final String COLLECTION_NAME = "test";

    /**
     * 分页查询T
     *
     * @param pageDto 分页对象
     * @return 返回mybatis-plus的IPage对象,其中records字段为符合条件的查询结果
     * @author excel.lin
     * @since 2021-05-07
     */
    IPage<T> listByPage(PageDto pageDto);


    /**
     * 查询T列表
     *
     * @param filterDto 分页对象
     * @return 返回mybatis-plus的IPage对象,其中records字段为符合条件的查询结果
     * @author excel.lin
     * @since 2021-05-07
     */
    List<T> list(FilterDto filterDto);


    /**
     * 根据id查询T
     *
     * @param id 需要查询的T的id
     * @return 返回对应id的T对象
     * @author excel.lin
     * @since 2021-05-07
     */
    T getById(String id);

    /**
     * 插入T
     *
     * @param object 需要插入的T对象
     * @return 返回插入成功之后T对象的id
     * @author excel.lin
     * @since 2021-05-07
     */
     T insert(T object);

    /**
     * 根据id删除T
     *
     * @param id 需要删除的T对象的id
     * @return 返回被删除的T对象的id
     * @author excel.lin
     * @since 2021-05-07
     */
    T deleteById(String id);

    List<T> delete(T object);

    /**
     * 根据id更新T
     *
     * @param object 需要更新的T对象
     * @return 返回被更新的T对象的id
     * @author excel.lin
     * @since 2021-05-07
     */
     T update(T object);

    /**
     * 保存T，若无ID，则新增，有ID则更新，
     *
     * @param object 需要更新的T对象
     * @return 返回被更新的T对象的id
     * @author excel.lin
     * @since 2021-05-07
     */
    T save(T object);
}

