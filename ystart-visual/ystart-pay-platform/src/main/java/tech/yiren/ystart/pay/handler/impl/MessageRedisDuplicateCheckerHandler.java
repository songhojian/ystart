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

package tech.yiren.ystart.pay.handler.impl;

import tech.yiren.ystart.pay.handler.MessageDuplicateCheckerHandler;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author ystart
 * @date 2019-06-14
 * <p>
 * 消息去重
 */
@Service
@AllArgsConstructor
public class MessageRedisDuplicateCheckerHandler implements MessageDuplicateCheckerHandler {

	private final RedisTemplate redisTemplate;

	/**
	 * 判断回调消息是否重复.
	 * @param messageId messageId需要根据上面讲的方式构造
	 * @return 如果是重复消息true，否则返回false
	 */
	@Override
	public boolean isDuplicate(String messageId) {
		return !redisTemplate.opsForValue().setIfAbsent(messageId, messageId, Duration.ofSeconds(10L));
	}

}
