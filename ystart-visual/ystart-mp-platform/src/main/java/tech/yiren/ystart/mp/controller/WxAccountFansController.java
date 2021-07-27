/*
 *    Copyright (c) 2018-2025, ystart All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: ystart
 */
package tech.yiren.ystart.mp.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import tech.yiren.ystart.common.core.util.R;
import tech.yiren.ystart.common.log.annotation.SysLog;
import tech.yiren.ystart.mp.entity.WxAccountFans;
import tech.yiren.ystart.mp.service.WxAccountFansService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 微信公众号粉丝管理
 *
 * @author ystart
 * @date 2019-03-26 22:08:08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/wxaccountfans")
public class WxAccountFansController {

	private final WxAccountFansService wxAccountFansService;

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param wxAccountFans 微信公众号粉丝
	 * @return
	 */
	@GetMapping("/page")
	public R getWxAccountFansPage(Page page, WxAccountFans wxAccountFans) {
		return R.ok(wxAccountFansService.page(page, Wrappers.query(wxAccountFans)));
	}

	/**
	 * 通过id查询微信公众号粉丝
	 * @param id id
	 * @return R
	 */
	@GetMapping("/{id}")
	public R getById(@PathVariable("id") Integer id) {
		return R.ok(wxAccountFansService.getById(id));
	}

	/**
	 * 新增微信公众号粉丝
	 * @param wxAccountFans 微信公众号粉丝
	 * @return R
	 */
	@SysLog("新增微信公众号粉丝")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('mp_wxaccountfans_add')")
	public R save(@RequestBody WxAccountFans wxAccountFans) {
		return R.ok(wxAccountFansService.save(wxAccountFans));
	}

	/**
	 * 修改微信公众号粉丝
	 * @param wxAccountFans 微信公众号粉丝
	 * @return R
	 */
	@SysLog("修改微信公众号粉丝")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('mp_wxaccountfans_edit')")
	public R updateById(@RequestBody WxAccountFans wxAccountFans) {
		return R.ok(wxAccountFansService.updateById(wxAccountFans));
	}

	/**
	 * 通过id删除微信公众号粉丝
	 * @param id id
	 * @return R
	 */
	@SysLog("删除微信公众号粉丝")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('mp_wxaccountfans_del')")
	public R removeById(@PathVariable Integer id) {
		return R.ok(wxAccountFansService.removeById(id));
	}

	/**
	 * 同步粉丝
	 * @param appId
	 * @return
	 */
	@PostMapping("/sync/{appId}")
	@PreAuthorize("@pms.hasPermission('mp_wxaccountfans_sync')")
	public R sync(@PathVariable String appId) {
		return R.ok(wxAccountFansService.syncAccountFans(appId));
	}

}
