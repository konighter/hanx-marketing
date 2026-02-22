package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsEntityChangeLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告实体变更日志 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsEntityChangeLogMapper extends BaseMapperX<AdsEntityChangeLogDO> {

    default List<AdsEntityChangeLogDO> selectListByEntity(String entityType, Long entityId) {
        return selectList(new LambdaQueryWrapperX<AdsEntityChangeLogDO>()
                .eq(AdsEntityChangeLogDO::getEntityType, entityType)
                .eq(AdsEntityChangeLogDO::getEntityId, entityId)
                .orderByDesc(AdsEntityChangeLogDO::getCreateTime));
    }

    default List<AdsEntityChangeLogDO> selectListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsEntityChangeLogDO>()
                .eq(AdsEntityChangeLogDO::getAccountId, accountId)
                .orderByDesc(AdsEntityChangeLogDO::getCreateTime));
    }
}
