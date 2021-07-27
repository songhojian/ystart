package tech.yiren.ystart.pay.handler.impl;

import java.util.Map;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import tech.yiren.ystart.common.data.tenant.TenantContextHolder;
import tech.yiren.ystart.pay.entity.PayGoodsOrder;
import tech.yiren.ystart.pay.entity.PayNotifyRecord;
import tech.yiren.ystart.pay.entity.PayTradeOrder;
import tech.yiren.ystart.pay.handler.MessageDuplicateCheckerHandler;
import tech.yiren.ystart.pay.service.PayGoodsOrderService;
import tech.yiren.ystart.pay.service.PayNotifyRecordService;
import tech.yiren.ystart.pay.service.PayTradeOrderService;
import tech.yiren.ystart.pay.utils.OrderStatusEnum;
import tech.yiren.ystart.pay.utils.PayConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author ystart
 * @date 2021/1/4
 * <p>
 * 聚合支付回调处理
 */
@Slf4j
@AllArgsConstructor
@Service("mergePayCallback")
public class YungouosMergePayNotifyCallbakHandler extends AbstractPayNotifyCallbakHandler {

	private final MessageDuplicateCheckerHandler duplicateCheckerHandler;

	private final PayNotifyRecordService recordService;

	private final PayTradeOrderService tradeOrderService;

	private final PayGoodsOrderService goodsOrderService;

	/**
	 * 初始化执行
	 * @param params
	 */
	@Override
	public void before(Map<String, String> params) {
		Integer tenant = MapUtil.getInt(params, "attach");
		TenantContextHolder.setTenantId(tenant);
	}

	/**
	 * 去重处理
	 * @param params 回调报文
	 * @return
	 */
	@Override
	public Boolean duplicateChecker(Map<String, String> params) {
		// 判断10秒内是否已经回调处理
		if (duplicateCheckerHandler.isDuplicate(params.get(PayConstants.MERGE_OUT_TRADE_NO))) {
			log.info("聚合支付订单重复回调 {} 不做处理", params);
			this.saveNotifyRecord(params, "重复回调");
			return true;
		}
		return false;
	}

	/**
	 * 验签逻辑
	 * @param params 回调报文
	 * @return
	 */
	@Override
	public Boolean verifyNotify(Map<String, String> params) {
		return Boolean.TRUE;
	}

	/**
	 * 解析报文
	 * @param params
	 * @return
	 */
	@Override
	public String parse(Map<String, String> params) {
		String mergeCode = params.get(PayConstants.MERGE_CODE);
		String orderNo = params.get(PayConstants.MERGE_OUT_TRADE_NO);
		PayGoodsOrder goodsOrder = goodsOrderService
				.getOne(Wrappers.<PayGoodsOrder>lambdaQuery().eq(PayGoodsOrder::getPayOrderId, orderNo));
		PayTradeOrder tradeOrder = tradeOrderService
				.getOne(Wrappers.<PayTradeOrder>lambdaQuery().eq(PayTradeOrder::getOrderId, orderNo));

		if (OrderStatusEnum.SUCCESS.getStatus().equals(mergeCode)) {
			goodsOrder.setStatus(OrderStatusEnum.SUCCESS.getStatus());
			tradeOrder.setStatus(OrderStatusEnum.SUCCESS.getStatus());
		}
		else {
			goodsOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
			tradeOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
		}

		goodsOrderService.updateById(goodsOrder);

		String succTime = MapUtil.getStr(params, "time");
		tradeOrder.setPaySuccTime(DateUtil.parse(succTime, DatePattern.NORM_DATETIME_FORMAT).getTime());
		tradeOrder.setChannelOrderNo(params.get("orderNo"));
		tradeOrderService.updateById(tradeOrder);
		return "SUCCESS";
	}

	/**
	 * 保存回调记录
	 * @param params 回调报文
	 * @param result 处理结果
	 */
	@Override
	public void saveNotifyRecord(Map<String, String> params, String result) {
		PayNotifyRecord record = new PayNotifyRecord();
		String notifyId = RandomUtil.randomNumbers(12);
		MapUtil.renameKey(params, PayConstants.MERGE_OUT_TRADE_NO, PayConstants.OUT_TRADE_NO);
		saveRecord(params, result, record, notifyId, recordService);
	}

}
