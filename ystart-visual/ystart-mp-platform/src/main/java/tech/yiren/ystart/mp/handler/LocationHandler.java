package tech.yiren.ystart.mp.handler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import tech.yiren.ystart.mp.config.WxMpContextHolder;
import tech.yiren.ystart.mp.constant.ReplyTypeEnum;
import tech.yiren.ystart.mp.entity.WxAccount;
import tech.yiren.ystart.mp.entity.WxAccountFans;
import tech.yiren.ystart.mp.entity.WxAutoReply;
import tech.yiren.ystart.mp.mapper.WxAccountFansMapper;
import tech.yiren.ystart.mp.mapper.WxAccountMapper;
import tech.yiren.ystart.mp.mapper.WxMsgMapper;
import tech.yiren.ystart.mp.service.WxAutoReplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
@Component
@AllArgsConstructor
public class LocationHandler extends AbstractHandler {

	private final WxAutoReplyService wxAutoReplyService;

	private final WxAccountFansMapper wxAccountFansMapper;

	private final WxAccountMapper wxAccountMapper;

	private final WxMsgMapper msgMapper;

	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) {
		// 上报地理位置事件
		log.info("上报地理位置，纬度 : {}，经度 : {}，精度 : {}", wxMessage.getLatitude(), wxMessage.getLongitude(),
				wxMessage.getPrecision());

		// 发送关注消息
		List<WxAutoReply> listWxAutoReply = wxAutoReplyService
				.list(Wrappers.<WxAutoReply>query().lambda().eq(WxAutoReply::getType, ReplyTypeEnum.MSG.getType())
						.eq(WxAutoReply::getAppId, WxMpContextHolder.getAppId())
						.eq(WxAutoReply::getReqType, wxMessage.getMsgType()));
		// 查询公众号 基本信息
		WxAccount wxAccount = wxAccountMapper
				.selectOne(Wrappers.<WxAccount>lambdaQuery().eq(WxAccount::getAccount, wxMessage.getToUser()));

		// 查询粉丝基本信息
		WxAccountFans fans = wxAccountFansMapper
				.selectOne(Wrappers.<WxAccountFans>lambdaQuery().eq(WxAccountFans::getOpenid, wxMessage.getFromUser()));

		return MsgHandler.getWxMpXmlOutMessage(wxMessage, listWxAutoReply, fans, msgMapper, wxAccount);
	}

}
