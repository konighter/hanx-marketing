package com.hzltd.module.erplus.service.notify.sender;

import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.system.enums.NotifyChannelTypeEnum;
import com.hzltd.module.spapi.model.system.NotifyMessage;

/**
 * 通知渠道发送策略接口
 * 
 * 每种渠道实现此接口，由 Spring 自动注入到 NotifySendServiceImpl。
 * 新增渠道只需: 实现此接口 + @Component
 */
public interface NotifyChannelSender {

    /**
     * 获取此 Sender 支持的渠道类型
     */
    NotifyChannelTypeEnum getChannelType();

    /**
     * 发送通知
     *
     * @param channel 渠道配置 (含 webhook URL 等)
     * @param message 通知消息
     */
    void send(NotifyChannelDO channel, NotifyMessage message);
}
