package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告数据同步任务 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsSyncTaskMapper extends BaseMapperX<AdsSyncTaskDO> {

    default List<AdsSyncTaskDO> selectListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getAccountId, accountId)
                .orderByDesc(AdsSyncTaskDO::getId));
    }

    default List<AdsSyncTaskDO> selectListByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getStatus, status)
                .orderByAsc(AdsSyncTaskDO::getCreateTime));
    }

    default List<AdsSyncTaskDO> selectRunningByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getAccountId, accountId)
                .eq(AdsSyncTaskDO::getStatus, "RUNNING"));
    }
}
