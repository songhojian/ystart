package tech.yiren.ystart.lowcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.yiren.ystart.common.core.util.R;
import tech.yiren.ystart.common.log.annotation.SysLog;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.entity.Relative;
import tech.yiren.ystart.lowcode.service.RelativeService;


/**
 * 字段关系
 *
 * @author pig code generator
 * @date 2021-06-10 15:52:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/relative")
@Api(value = "relative", tags = "字段关系管理")
public class RelativeController {

    private final RelativeService relativeService;

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
//    @PreAuthorize("@pms.hasPermission('common_common_get')" )
    public R getDemoPage(Page page, FilterDto common) {
        System.out.println(page);
        PageDto pageDto = new PageDto();
        pageDto.setPageNo(page.getCurrent());
        pageDto.setPageSize((int) page.getSize());
        if(null != common){
            pageDto.setFilter(common.getFilter());
        }
        pageDto.setPageSize((int) page.getSize());
        return R.ok(relativeService.listByPage(pageDto));
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
        return R.ok(relativeService.list(filterDto));
    }


    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/getById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_get')" )
    public R getById(@PathVariable("id") String id) {
        return R.ok(relativeService.getById(id));
    }

    @ApiOperation(value = "通过code查询", notes = "通过code查询")
    @GetMapping("/getByCode/{code}")
    public R getByCode(@PathVariable("code") String code) {
        return R.ok(relativeService.getByCode(code));
    }


    @ApiOperation(value = "新增字段关系", notes = "新增字段关系")
    @SysLog("新增字段关系")
    @PostMapping("/insert")
//    @PreAuthorize("@pms.hasPermission('common_common_add')" )
    public R insert(@RequestBody Relative object) {
        return R.ok(relativeService.insert(object));
    }

    @ApiOperation(value = "修改字段关系", notes = "修改字段关系")
    @SysLog("修改字段关系")
    @PutMapping("/update")
//    @PreAuthorize("@pms.hasPermission('common_common_add')" )
    public R update(@RequestBody Relative object) {
        return R.ok(relativeService.update(object));
    }

    @ApiOperation(value = "修改字段关系", notes = "修改字段关系")
    @SysLog("修改字段关系")
    @PutMapping("/updateById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_edit')" )
    public R updateById(@PathVariable String id, @RequestBody Relative object) {
        return R.ok(relativeService.updateById(id, object));
    }

    @ApiOperation(value = "通过id删除字段关系", notes = "通过id删除字段关系")
    @SysLog("通过id删除字段关系")
    @DeleteMapping("/deleteById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_del')" )
    public R deleteById(@PathVariable String id) {
        return R.ok(relativeService.deleteById(id));
    }

}
