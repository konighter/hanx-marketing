package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 任务执行历史日志 DO
 */
@TableName("ai_mas_task_history")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasTaskHistoryDO extends BaseDO {

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
     * 任务唯一标识
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 执行角色
     */
    private String role;
    /**
     * 执行提示词
     */
    private String prompt;
    /**
     * 执行结果内容
     */
    private String result;
    /**
     * 执行状态 (SUCCESS/FAILED/INTERRUPTED)
     */
    private String status;
    /**
     * 是否为内部编排任务
     */
    private Boolean isInternal;
    /**
     * 执行耗时 (ms)
     */
    private Long executionTime;

}
