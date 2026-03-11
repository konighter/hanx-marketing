package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import java.time.LocalDateTime;

/**
 * MAS 业务层任务 DO
 * 支持并行/串行/叶子任务及其状态流转
 */
@TableName("ai_mas_task")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasTaskDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 所属会话 ID
     */
    private String sessionId;

    /**
     * 父任务 ID (支持嵌套)
     */
    private Long parentId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型
     * {@link com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum}
     */
    private String taskType;

    /**
     * 任务状态
     * {@link com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum}
     */
    private String status;

    /**
     * 输入参数 (JSON)
     */
    private String inputData;

    /**
     * 输出结果 (JSON)
     */
    private String outputData;

    /**
     * 执行顺序 (串行任务用)
     */
    private Integer executionOrder;

    /**
     * 重试上下文/审核意见/错误详情
     */
    private String retryData;

    /**
     * 下次执行时间 (用于长周期/定时恢复任务)
     */
    private LocalDateTime nextExecuteTime;

}
