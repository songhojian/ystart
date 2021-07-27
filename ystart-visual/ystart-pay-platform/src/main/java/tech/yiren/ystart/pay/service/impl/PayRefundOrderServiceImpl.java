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
package tech.yiren.ystart.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import tech.yiren.ystart.pay.entity.PayRefundOrder;
import tech.yiren.ystart.pay.mapper.PayRefundOrderMapper;
import tech.yiren.ystart.pay.service.PayRefundOrderService;
import org.springframework.stereotype.Service;

/**
 * 退款
 *
 * @author ystart
 * @date 2019-05-28 23:58:11
 */
@Service
public class PayRefundOrderServiceImpl extends ServiceImpl<PayRefundOrderMapper, PayRefundOrder>
		implements PayRefundOrderService {

}
