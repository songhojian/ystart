package tech.yiren.ystart.pay.utils;

import tech.yiren.ystart.pay.entity.PayChannel;
import lombok.experimental.UtilityClass;

/**
 * @author ystart
 * @date 2021/2/2
 *
 * 聚合支付配置管理
 */
@UtilityClass
public class ChannelPayApiConfigKit {

	private static final ThreadLocal<PayChannel> TL = new ThreadLocal();

	public PayChannel get() {
		return TL.get();
	}

	public void put(PayChannel channel) {
		TL.set(channel);
	}

	public void remove() {
		TL.remove();
	}

}
