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

    default List<AdsSyncTaskDO> selectListByStatuses(List<String> statuses) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .in(AdsSyncTaskDO::getStatus, statuses)
                .orderByAsc(AdsSyncTaskDO::getCreateTime));
    }

    default List<AdsSyncTaskDO> selectListByStatusesAndTaskType(List<String> statuses, String taskType) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .in(AdsSyncTaskDO::getStatus, statuses)
                .eq(AdsSyncTaskDO::getTaskType, taskType)
                .orderByAsc(AdsSyncTaskDO::getCreateTime));
    }

    default List<AdsSyncTaskDO> selectListByParentTaskId(Long parentTaskId) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getParentTaskId, parentTaskId)
                .orderByAsc(AdsSyncTaskDO::getId));
    }

    default List<AdsSyncTaskDO> selectActiveReportTasks(List<String> statuses) {
        long now = System.currentTimeMillis();
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .in(AdsSyncTaskDO::getStatus, statuses)
                .in(AdsSyncTaskDO::getTaskType, "REPORT_DAILY", "REPORT_DIMENSION")
                .and(w -> w.le(AdsSyncTaskDO::getScheduledAt, now)
                           .or().isNull(AdsSyncTaskDO::getScheduledAt))
                .orderByAsc(AdsSyncTaskDO::getScheduledAt));
    }

    default List<AdsSyncTaskDO> selectRunningByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getAccountId, accountId)
                .eq(AdsSyncTaskDO::getStatus, "RUNNING"));
    }
}
