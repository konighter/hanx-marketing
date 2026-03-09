package com.hzltd.module.erplus.sys;

import com.hzltd.module.erplus.enums.notify.NotifyChannelTypeEnum;
import com.hzltd.module.erplus.sys.model.NotifyMessage;

/**
 * 通知发送服务
 * 
 * 各模块通过此接口发送通知，与具体渠道实现解耦。
 * 实现类在 biz 模块中。
 */
public interface ChannelNotifySendService {

    /**
     * 向所有启用的渠道发送通知
     *
     * @param message 通知消息
     */
    void send(NotifyMessage message);

    /**
     * 向指定类型的渠道发送通知
     *
     * @param message     通知消息
     * @param channelType 渠道类型
     */
    void send(NotifyMessage message, NotifyChannelTypeEnum channelType);
}
