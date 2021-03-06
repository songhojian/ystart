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

package tech.yiren.ystart.common.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import tech.yiren.ystart.common.data.config.YstartMybatisProperties;
import tech.yiren.ystart.common.data.datascope.DataScopeHandle;
import tech.yiren.ystart.common.data.datascope.DataScopeInnerInterceptor;
import tech.yiren.ystart.common.data.datascope.DataScopeSqlInjector;
import tech.yiren.ystart.common.data.datascope.YstartDefaultDatascopeHandle;
import tech.yiren.ystart.common.data.resolver.SqlFilterArgumentResolver;
import tech.yiren.ystart.common.data.tenant.YstartTenantHandler;
import tech.yiren.ystart.common.security.service.YstartUser;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author ystart
 * @date 2020-02-08
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(YstartMybatisProperties.class)
public class MybatisPlusConfiguration implements WebMvcConfigurer {

	/**
	 * ?????????????????????????????????????????????????????????SQL ??????
	 * @param resolverList
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolverList) {
		resolverList.add(new SqlFilterArgumentResolver());
	}

	/**
	 * ystart ???????????????????????????
	 * @return YstartDefaultDatascopeHandle
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(YstartUser.class)
	public DataScopeHandle dataScopeHandle() {
		return new YstartDefaultDatascopeHandle();
	}

	/**
	 * mybatis plus ???????????????
	 * @return YstartDefaultDatascopeHandle
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// ???????????????
		TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
		tenantLineInnerInterceptor.setTenantLineHandler(ystartTenantHandler());
		interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
		// ????????????
		DataScopeInnerInterceptor dataScopeInnerInterceptor = new DataScopeInnerInterceptor();
		dataScopeInnerInterceptor.setDataScopeHandle(dataScopeHandle());
		interceptor.addInnerInterceptor(dataScopeInnerInterceptor);
		// ????????????
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return interceptor;
	}

	/**
	 * ?????????????????????????????????
	 * @return ?????????????????????????????????
	 */
	@Bean
	@ConditionalOnMissingBean
	public YstartTenantHandler ystartTenantHandler() {
		return new YstartTenantHandler();
	}

	/**
	 * ?????? mybatis-plus baseMapper ??????????????????
	 * @return
	 */
	@Bean
	@ConditionalOnBean(DataScopeHandle.class)
	public DataScopeSqlInjector dataScopeSqlInjector() {
		return new DataScopeSqlInjector();
	}

	/**
	 * SQL ???????????????
	 * @return DruidSqlLogFilter
	 */
	@Bean
	public DruidSqlLogFilter sqlLogFilter(YstartMybatisProperties properties) {
		return new DruidSqlLogFilter(properties);
	}

}
