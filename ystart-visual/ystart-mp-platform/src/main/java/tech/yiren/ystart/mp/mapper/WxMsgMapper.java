package tech.yiren.ystart.mp.mapper;

import tech.yiren.ystart.common.data.datascope.YstartBaseMapper;
import tech.yiren.ystart.mp.entity.WxMsg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信消息
 *
 * @author JL
 * @date 2019-05-28 16:12:10
 */
@Mapper
public interface WxMsgMapper extends YstartBaseMapper<WxMsg> {

}
