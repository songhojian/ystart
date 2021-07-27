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

package tech.yiren.ystart.codegen;

import tech.yiren.ystart.common.datasource.annotation.EnableDynamicDataSource;
import tech.yiren.ystart.common.feign.annotation.EnableYstartFeignClients;
import tech.yiren.ystart.common.security.annotation.EnableYstartResourceServer;
import tech.yiren.ystart.common.swagger.annotation.EnableYstartSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ystart
 * @date 2018/07/29 代码生成模块
 */
@EnableDynamicDataSource
@EnableYstartSwagger2
@EnableYstartFeignClients
@EnableDiscoveryClient
@EnableYstartResourceServer
@SpringBootApplication
public class YstartCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(YstartCodeGenApplication.class, args);
	}

}
