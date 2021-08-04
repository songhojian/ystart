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
import tech.yiren.ystart.lowcode.entity.Trigger;
import tech.yiren.ystart.lowcode.service.TriggerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trigger")
@Api(value = "trigger", tags = "触发器管理")
public class TriggerController {
	private final TriggerService triggerService;
	@ApiOperation(value = "新增trigger 对象", notes = "新增trigger 对象")
	@SysLog("新增trigger 对象")
	@PostMapping("/save")
//    @PreAuthorize("@pms.hasPermission('object_object_add')")
	public R save(@RequestBody Object object) {
		return R.ok(triggerService.save(object));
	}

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
		return R.ok(triggerService.listByPage(pageDto));
	}
	/**
	 * 删除通过id
	 *
	 * @param id id
	 * @return {@link R}
	 */
	@ApiOperation(value = "通过id删除trigger 对象", notes = "通过id删除trigger 对象")
	@SysLog("通过id删除trigger 对象")
	@DeleteMapping("/deleteById/{id}")
//    @PreAuthorize("@pms.hasPermission('object_object_del')")
	public R deleteById(@PathVariable String id) {
		return R.ok(triggerService.deleteById(id));
	} /**
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
	public R updateById(@PathVariable String id, @RequestBody Trigger object) {
		return R.ok(triggerService.updateById(id, object));
	}


}
