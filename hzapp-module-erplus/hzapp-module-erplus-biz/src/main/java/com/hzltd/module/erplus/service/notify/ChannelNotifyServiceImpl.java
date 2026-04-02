package com.hzltd.module.erplus.service.notify;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.erplus.dal.mysql.notify.NotifyChannelMapper;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelSaveReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 通知渠道 Service 实现
 */
@Service
@Validated
public class ChannelNotifyServiceImpl implements ChannelNotifyService {

    @Resource
    private NotifyChannelMapper notifyChannelMapper;

    @Override
    public Long createChannel(NotifyChannelSaveReqVO reqVO) {
        NotifyChannelDO channelDO = BeanUtils.toBean(reqVO, NotifyChannelDO.class);
        notifyChannelMapper.insert(channelDO);
        return channelDO.getId();
    }

    @Override
    public void updateChannel(NotifyChannelSaveReqVO reqVO) {
        NotifyChannelDO updateObj = BeanUtils.toBean(reqVO, NotifyChannelDO.class);
        notifyChannelMapper.updateById(updateObj);
    }

    @Override
    public void deleteChannel(Long id) {
        notifyChannelMapper.deleteById(id);
    }

    @Override
    public NotifyChannelDO getChannel(Long id) {
        return notifyChannelMapper.selectById(id);
    }

    @Override
    public PageResult<NotifyChannelDO> getChannelPage(NotifyChannelPageReqVO reqVO) {
        return notifyChannelMapper.selectPage(reqVO);
    }

    @Override
    public List<NotifyChannelDO> getEnabledChannels() {
        return notifyChannelMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public List<NotifyChannelDO> getEnabledChannelsByType(Integer channelType) {
        return notifyChannelMapper.selectListByStatusAndType(CommonStatusEnum.ENABLE.getStatus(), channelType);
    }
}
