package com.hzltd.module.erplus.system.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.system.dal.dataobject.ErpScheduleTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 系统通用任务调度表 Mapper
 *
 * @author antigravity
 */
@Mapper
@TenantIgnore
public interface ErpScheduleTaskMapper extends BaseMapperX<ErpScheduleTaskDO> {

    /**
     * 根据任务唯一标识和类型查询任务
     *
     * @param taskUniqueId 任务唯一标识
     * @param taskType     任务类型
     * @return 任务对象
     */
    default ErpScheduleTaskDO selectByUniqueId(String taskUniqueId, String taskType) {
        return selectOne(ErpScheduleTaskDO::getTaskUniqueId, taskUniqueId,
                ErpScheduleTaskDO::getTaskType, taskType);
    }

    /**
     * 根据状态查询待调度的任务
     *
     * @param statuses 状态列表
     * @param limit    限制数量
     * @return 任务列表
     */
    default List<ErpScheduleTaskDO> selectListByStatuses(Collection<String> statuses, int limit) {
        return selectList(new LambdaQueryWrapperX<ErpScheduleTaskDO>()
                .in(ErpScheduleTaskDO::getStatus, statuses)
                .le(ErpScheduleTaskDO::getScheduledAt, System.currentTimeMillis())
                .orderByAsc(ErpScheduleTaskDO::getScheduledAt)
                .last("LIMIT " + limit));
    }
    
}
