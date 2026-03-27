package com.hzltd.module.erplus.service.notify;

import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.system.enums.NotifyChannelTypeEnum;
import com.hzltd.module.erplus.service.notify.sender.NotifyChannelSender;
import com.hzltd.module.system.service.ChannelNotifySendService;
import com.hzltd.module.system.model.NotifyMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知发送 Service 实现
 * 
 * 自动收集所有 NotifyChannelSender 实现，按渠道类型分发。
 */
@Slf4j
@Service
public class ChannelNotifySendServiceImpl implements ChannelNotifySendService {

    @Resource
    private ChannelNotifyService channelNotifyService;

    @Resource
    private List<NotifyChannelSender> senderList;

    /**
     * 渠道类型 -> Sender 映射
     */
    private Map<NotifyChannelTypeEnum, NotifyChannelSender> senderMap;

    @PostConstruct
    public void init() {
        senderMap = new HashMap<>();
        for (NotifyChannelSender sender : senderList) {
            senderMap.put(sender.getChannelType(), sender);
            log.info("[NotifySendService] 注册 Sender: {}", sender.getChannelType().getName());
        }
    }

    @Override
    public void send(NotifyMessage message) {
        List<NotifyChannelDO> channels = channelNotifyService.getEnabledChannels();
        if (channels.isEmpty()) {
            log.warn("[NotifySendService] 没有启用的通知渠道, 跳过发送");
            return;
        }
        doSend(channels, message);
    }

    @Override
    public void send(NotifyMessage message, NotifyChannelTypeEnum channelType) {
        List<NotifyChannelDO> channels = channelNotifyService.getEnabledChannelsByType(channelType.getCode());
        if (channels.isEmpty()) {
            log.warn("[NotifySendService] 没有启用的 {} 渠道, 跳过发送", channelType.getName());
            return;
        }
        doSend(channels, message);
    }

    private void doSend(List<NotifyChannelDO> channels, NotifyMessage message) {
        for (NotifyChannelDO channel : channels) {
            NotifyChannelTypeEnum type = NotifyChannelTypeEnum.of(channel.getChannelType());
            if (type == null) {
                log.warn("[NotifySendService] 未知的渠道类型: {}", channel.getChannelType());
                continue;
            }
            NotifyChannelSender sender = senderMap.get(type);
            if (sender == null) {
                log.warn("[NotifySendService] 渠道 {} 没有对应的 Sender 实现", type.getName());
                continue;
            }
            try {
                sender.send(channel, message);
            } catch (Exception e) {
                log.error("[NotifySendService] 发送通知失败, channel={}, type={}", 
                        channel.getName(), type.getName(), e);
            }
        }
    }
}
