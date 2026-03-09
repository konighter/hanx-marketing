package com.hzltd.module.erplus.dal.mysql.notify;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.notify.vo.NotifyChannelPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 通知渠道 Mapper
 */
@Mapper
public interface NotifyChannelMapper extends BaseMapperX<NotifyChannelDO> {

    default PageResult<NotifyChannelDO> selectPage(NotifyChannelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NotifyChannelDO>()
                .likeIfPresent(NotifyChannelDO::getName, reqVO.getName())
                .eqIfPresent(NotifyChannelDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(NotifyChannelDO::getStatus, reqVO.getStatus())
                .orderByDesc(NotifyChannelDO::getId));
    }

    default List<NotifyChannelDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<NotifyChannelDO>()
                .eq(NotifyChannelDO::getStatus, status));
    }

    default List<NotifyChannelDO> selectListByStatusAndType(Integer status, Integer channelType) {
        return selectList(new LambdaQueryWrapperX<NotifyChannelDO>()
                .eq(NotifyChannelDO::getStatus, status)
                .eq(NotifyChannelDO::getChannelType, channelType));
    }
}
