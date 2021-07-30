package tech.yiren.ystart.lowcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.yiren.ystart.common.core.util.R;
import tech.yiren.ystart.common.log.annotation.SysLog;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Model;
import tech.yiren.ystart.lowcode.entity.Relative;
import tech.yiren.ystart.lowcode.service.ObjectService;

import java.util.HashMap;
import java.util.List;


/**
 * object 对象
 *
 * @author pig code generator
 * @date 2021-06-10 15:52:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/object")
@Api(value = "object", tags = "object 对象管理")
@JsonSerialize(using = ToStringSerializer.class)
public class ObjectController {

    private final ObjectService objectService;

    /**
     * 获得页面列表
     *
     * @param page   页面
     * @param object 对象
     * @return {@link R}
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
//    @PreAuthorize("@pms.hasPermission('object_object_get')")
    public R getPageList(Page page, FilterDto object) {
        System.out.println(page);
        System.out.println(object);
        PageDto pageDto = new PageDto();
        pageDto.setPageNo(page.getCurrent());
        pageDto.setPageSize((int) page.getSize());
        if(null != object.getFilter()){
            pageDto.setFilter(object.getFilter());
        }
        return R.ok(objectService.listByPage(pageDto));
    }

    /**
     * 得到列表
     *
     * @param filterDto 过滤器dto
     * @return {@link R}
     */
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @GetMapping("/list")
//    @PreAuthorize("@pms.hasPermission('object_object_list')")
    public R getList(FilterDto filterDto) {
        return R.ok(objectService.list(filterDto));
    }


    /**
     * 通过id
     *
     * @param id id
     * @return {@link R}
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/getById/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(objectService.getById(id));
    }

    @ApiOperation(value = "通过code查询", notes = "通过id查询")
    @GetMapping("/getByCode/{code}")
//    @PreAuthorize("@pms.hasPermission('object_object_get')")
    public R getByCode(@PathVariable("code") String code) {
        return R.ok(objectService.getByCode(code));
    }


    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/getObjConfig/{code}")
//    @PreAuthorize("@pms.hasPermission('object_object_get')")
    public R getObjConfig(@PathVariable("code") String code) {
        return R.ok(objectService.getConfigByCode(code));
    }


    /**
     * 保存
     *
     * @param object 对象
     * @return {@link R}
     */
    @ApiOperation(value = "新增object 对象", notes = "新增object 对象")
    @SysLog("新增object 对象")
    @PostMapping("/save")
//    @PreAuthorize("@pms.hasPermission('object_object_add')")
    public R save(@RequestBody Object object) {
        return R.ok(objectService.save(object));
    }


    /**
     * 更新通过id
     *
     * @param id     id
     * @param object 对象
     * @return {@link R}
     */
    @ApiOperation(value = "修改object 对象", notes = "修改object 对象")
    @SysLog("修改object 对象")
    @PutMapping("/updateById/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_edit')")
    public R updateById(@PathVariable String id, @RequestBody Object object) {
        return R.ok(objectService.updateById(id, object));
    }


    /**
     * 删除通过id
     *
     * @param id id
     * @return {@link R}
     */
    @ApiOperation(value = "通过id删除object 对象", notes = "通过id删除object 对象")
    @SysLog("通过id删除object 对象")
    @DeleteMapping("/deleteById/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_del')")
    public R deleteById(@PathVariable String id) {
        return R.ok(objectService.deleteById(id));
    }



    @ApiOperation(value = "列表查询", notes = "列表查询")
    @GetMapping("/getObjectFeilds/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_list')")
    public R getObjectFeilds(@PathVariable String id) {
        return R.ok(objectService.listFeilds(id));
    }


    @ApiOperation(value = "保存 object  对象", notes = "修改object 对象")
    @SysLog("修改object 对象")
    @PutMapping("/saveObjConfig/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_edit')")
    public R saveObjConfig(@PathVariable String id, @RequestBody Object object) {
        return R.ok(objectService.updateById(id, object));
    }


	@ApiOperation(value = "保存 object  对象", notes = "修改object 对象")
	@SysLog("修改object 对象")
	@GetMapping("/genSql/{code}")
//    @PreAuthorize("@pms.hasPermission('object_object_edit')")
	public R genSql(@PathVariable String code) {
    	String strSql ="CREATE TABLE `"+code+"` (\n";

		strSql += "`id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '自增ID',\n";

		HashMap<String, Object> ret = (HashMap<String, Object>) objectService.getConfigByCode(code);
		List<Relative> relatives = (List<Relative>) ret.get("relatives");
		Model model = (Model) ret.get("model");
		for (int i = 0; i < relatives.size(); i++) {
			Relative relative = relatives.get(i);
			if(relative.getFieldType().equals("input")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("input-number-int")) {
				strSql += "`"+relative.getFieldCode()+"` int(11) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("input-number-double")) {
				strSql += "`"+relative.getFieldCode()+"` DECIMAL(20,"+relative.getPrecision()+") COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("boolean")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) default 'true' COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("input-number-money")) {
				strSql += "`"+relative.getFieldCode()+"` DECIMAL(20,"+relative.getPrecision()+") default 0 COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("input-number-percent")) {
				strSql += "`"+relative.getFieldCode()+"` DECIMAL(10,2) default 0 COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("date")) {
				strSql += "`"+relative.getFieldCode()+"` datetime COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("datetime")) {
				strSql += "`"+relative.getFieldCode()+"` datetime COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("select")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("select-multiple")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("tree")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("tree-multiple")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("url")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("email")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("telephone")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("mobile")) {
				strSql += "`"+relative.getFieldCode()+"` varchar(255) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("country-region")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("address")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("tag")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("summary")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("auto-number")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("attachment")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("formula")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("image")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("rich-text")) {
				strSql += "`"+relative.getFieldCode()+"` TEXT COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("map")) {
				strSql += "`"+relative.getFieldCode()+"` json COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("field-reference")) {
				strSql += "`"+relative.getFieldCode()+"` bigint(64) COMMENT '"+relative.getFieldName()+"',\n";
			}else if(relative.getFieldType().equals("master-slave")) {
				strSql += "`"+relative.getFieldCode()+"` bigint(64) COMMENT '"+relative.getFieldName()+"',\n";
			}
		}
		strSql += "PRIMARY KEY (`id`) USING BTREE\n";
		strSql += ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='"+model.getName()+"';\n";
		return R.ok(strSql);
	}
}
