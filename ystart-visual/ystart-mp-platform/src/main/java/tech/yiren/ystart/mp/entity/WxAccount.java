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
package tech.yiren.ystart.mp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import tech.yiren.ystart.common.core.sensitive.Sensitive;
import tech.yiren.ystart.common.core.sensitive.SensitiveTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公众号账户
 *
 * @author ystart
 * @date 2019-03-26 22:07:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxAccount extends Model<WxAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Integer id;

	/**
	 * 公众号名称
	 */
	private String name;

	/**
	 * 公众号账户
	 */
	private String account;

	/**
	 * 公众号appid
	 */
	private String appid;

	/**
	 * 公众号密钥
	 */
	@Sensitive(type = SensitiveTypeEnum.KEY)
	private String appsecret;

	/**
	 * 公众号url
	 */
	private String url;

	/**
	 * 公众号token
	 */
	private String token;

	/**
	 * 加密密钥
	 */
	private String aeskey;

	/**
	 * 二维码图片URL
	 */
	private String qrUrl;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 是否删除 -1：已删除 0：正常
	 */
	@TableLogic
	private String delFlag;

	/**
	 * 租户
	 */
	private Integer tenantId;

}
