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

package tech.yiren.ystart.common.security.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.yiren.ystart.common.security.component.YstartCommenceAuthExceptionEntryPoint;
import tech.yiren.ystart.common.security.service.YstartUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author ystart
 * @date 2018/8/5 手机号登录配置入口
 */
@Getter
@Setter
public class MobileSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AuthenticationSuccessHandler mobileLoginSuccessHandler;

	@Autowired
	private YstartUserDetailsService userDetailsService;

	@Override
	public void configure(HttpSecurity http) {
		MobileAuthenticationFilter mobileAuthenticationFilter = new MobileAuthenticationFilter();
		mobileAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		mobileAuthenticationFilter.setAuthenticationSuccessHandler(mobileLoginSuccessHandler);
		mobileAuthenticationFilter.setAuthenticationEntryPoint(new YstartCommenceAuthExceptionEntryPoint(objectMapper));

		MobileAuthenticationProvider mobileAuthenticationProvider = new MobileAuthenticationProvider();
		mobileAuthenticationProvider.setUserDetailsService(userDetailsService);
		http.authenticationProvider(mobileAuthenticationProvider).addFilterAfter(mobileAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class);
	}

}
