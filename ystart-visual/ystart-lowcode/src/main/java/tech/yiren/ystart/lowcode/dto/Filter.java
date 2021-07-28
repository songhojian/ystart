package tech.yiren.ystart.lowcode.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import lombok.Data;
import tech.yiren.ystart.lowcode.exception.bizException.BizException;
import tech.yiren.ystart.lowcode.exception.bizException.BizExceptionCodeEnum;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 过滤器dto
 *
 * @author Excel
 * @date 2021/06/18
 */
@Data
public class Filter {
    private HashMap filter;
    private String sortField;
    private String sortOrder;
    private Long limit;

    public void parseListLimitQueryWapper(QueryWrapper queryWrapper) {
        if (null == this.getLimit()) {
            if (null == this.getLimit()) {
                queryWrapper.last("limit 200");
            } else if (this.getLimit() > 200) {
                throw new BizException(BizExceptionCodeEnum.LIMIT_PARAM_ERROR);
            } else if (this.getLimit() > 0) {
                queryWrapper.last("limit " + this.getLimit());
            }
        } else if (this.getLimit() > 200) {
            throw new BizException(BizExceptionCodeEnum.LIMIT_PARAM_ERROR);
        } else if (this.getLimit() > 0) {
            queryWrapper.last("limit " + this.getLimit());
        }
    }

    public QueryWrapper parseQueryWrapper(HashMap<String, Object> hashMap, QueryWrapper queryWrapper) {
        if (hashMap.size() > 0) {
            hashMap.forEach((k, v) -> {
                if (k.startsWith("$attributes")) {
                    List<String> selects = (List<String>) v;
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < selects.size(); i++) {
                        String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, selects.get(i));
                        list.add(col);
                    }
                    queryWrapper.select(String.join(",", list));
                } else if (k.startsWith("$filter")) {
                    queryWrapper.apply(v.toString());
                } else if (k.startsWith("$last")) {
                    queryWrapper.last(v.toString());
                } else if (k.startsWith("$in:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.inSql(col, v.toString());
                } else if (k.startsWith("$like:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(6));
                    queryWrapper.like(col, v);
                } else if (k.startsWith("$notIn:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(7));
                    queryWrapper.notInSql(col, v.toString());
                } else if (k.startsWith("$ne:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.ne(col, v);
                } else if (k.startsWith("$like:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(6));
                    queryWrapper.like(col, v);
                } else if (k.startsWith("$gt:DATE:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(9));
                    queryWrapper.gt(col, Timestamp.valueOf(v.toString()));
                } else if (k.startsWith("$ge:DATE:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(9));
                    queryWrapper.ge(col, Timestamp.valueOf(v.toString()));
                } else if (k.startsWith("$lt:DATE:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(9));
                    queryWrapper.lt(col, Timestamp.valueOf(v.toString()));
                } else if (k.startsWith("$le:DATE:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(9));
                    queryWrapper.le(col, Timestamp.valueOf(v.toString()));
                } else if (k.startsWith("$gt:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.gt(col, v);
                } else if (k.startsWith("$ge:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.ge(col, v);
                } else if (k.startsWith("$lt:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.lt(col, v);
                } else if (k.startsWith("$le:")) {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k.substring(4));
                    queryWrapper.le(col, v.toString());
                } else {
                    String col = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k);
                    queryWrapper.eq(col, v);
                }
            });
        }
        if (null != this.getSortField() && null != this.getSortOrder() && !this.getSortField().equals("") && !this.getSortOrder().equals("")) {
            if (this.getSortOrder().equals("ascend")) {
                queryWrapper.orderByAsc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getSortField()));
            } else {
                queryWrapper.orderByDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getSortField()));
            }
        } else {
            queryWrapper.orderByDesc("id");
        }
        return queryWrapper;
    }
}
