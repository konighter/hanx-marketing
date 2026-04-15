package com.hzltd.module.erplus.service.notify;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelSaveReqVO;

import java.util.List;

/**
 * 通知渠道 Service
 */
public interface ChannelNotifyService {

    Long createChannel(NotifyChannelSaveReqVO reqVO);

    void updateChannel(NotifyChannelSaveReqVO reqVO);

    void deleteChannel(Long id);

    NotifyChannelDO getChannel(Long id);

    PageResult<NotifyChannelDO> getChannelPage(NotifyChannelPageReqVO reqVO);

    /**
     * 获取所有启用的渠道
     */
    List<NotifyChannelDO> getEnabledChannels();

    /**
     * 获取指定类型的启用渠道
     */
    List<NotifyChannelDO> getEnabledChannelsByType(Integer channelType);
}
