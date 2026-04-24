package com.hzltd.module.erplus.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务模型 (DTO)
 * 用于 API 层与业务层交互，隔离底层 DO
 *
 * @author antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpTaskDTO {

    /** 任务ID */
    private Long id;
    
    /** 店铺ID */
    private Integer shopId;
    
    /** 父任务 ID */
    private Long parentTaskId;
    
    /** 平台: AMAZON / FACEBOOK / ERP / SYSTEM 等 */
    private String platform;
    
    /** 任务类型 */
    private String taskType;
    
    /** 任务唯一标识（用于幂等） */
    private String taskUniqueId;
    
    /** 任务状态 */
    private String status;
    
    /** 平台 Job ID (异步场景) */
    private String platformJobId;
    
    /** 开始日期 */
    private LocalDate dateRangeStart;
    
    /** 结束日期 */
    private LocalDate dateRangeEnd;
    
    /** 任务上下文参数 */
    private Map<String, Object> context;
    
    /** 已重试次数 */
    private Integer retryCount;
    
    /** 最大重试次数 */
    private Integer maxRetries;
    
    /** 失败原因 */
    private String errorMessage;
    
    /** 预计可执行时间 (epoch millis) */
    private Long scheduledAt;
    
    /** 开始执行时间 */
    private LocalDateTime startedAt;
    
    /** 完成时间 */
    private LocalDateTime finishedAt;
    
}
