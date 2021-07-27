package tech.yiren.ystart.lowcode.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import lombok.Data;

import java.util.HashMap;

@Data
public class PageDto {
    private Long pageNo;
    private Integer pageSize;
    private String sortField;
    private String sortOrder;
    private HashMap filter;

    public void fillAndQueryAndSort(QueryWrapper wrapper) {

        if (null != this.getFilter()) {
            FilterDto filterDto = new FilterDto();
            filterDto.parseQueryWrapper(this.getFilter(), wrapper);
        }
        if (null != this.getSortField() && null != this.getSortOrder() && !this.getSortField().equals("") && !this.getSortOrder().equals("")) {
            if (this.getSortOrder().equals("ascend")) {
                wrapper.orderByAsc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getSortField()));
            } else {
                wrapper.orderByDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getSortField()));
            }
        } else {
            wrapper.orderByDesc("id");
        }

    }

}
