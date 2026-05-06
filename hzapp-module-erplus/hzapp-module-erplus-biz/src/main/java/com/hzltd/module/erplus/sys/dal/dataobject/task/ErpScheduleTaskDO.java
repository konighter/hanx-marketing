package com.hzltd.module.erplus.sys.dal.dataobject.task;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统通用任务调度表 DO
 *
 * @author antigravity
 */
@TableName(value = "erplus_schedule_task", autoResultMap = true)
@KeySequence("erplus_schedule_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpScheduleTaskDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 父任务 ID
     */
    private Long parentTaskId;
    /**
     * 平台标识 (如 AMAZON, ERP, SYSTEM)
     */
    private String platform;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务唯一标识（用于幂等）
     */
    private String taskUniqueId;
    /**
     * 任务状态
     */
    private String status;
    /**
     * 平台 Job ID (异步场景)
     */
    private String platformJobId;
    /**
     * 起始日期
     */
    private LocalDateTime dateRangeStart;
    /**
     * 结束日期
     */
    private LocalDateTime dateRangeEnd;
    /**
     * 任务参数上下文 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> context;
    /**
     * 已重试次数
     */
    private Integer retryCount;
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    /**
     * 失败原因
     */
    private String errorMessage;
    /**
     * 预计执行时间 (epoch millis)
     */
    private Long scheduledAt;
    /**
     * 开始执行时间
     */
    private LocalDateTime startedAt;
    /**
     * 完成时间
     */
    private LocalDateTime finishedAt;

}
