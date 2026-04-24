package com.hzltd.module.erplus.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * 任务提交请求
 *
 * @author antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpTaskSubmitRequest {

    /** 店铺ID (可选) */
    private Integer shopId;
    
    /** 平台标识 (如 AMAZON, ERP, SYSTEM) */
    private String platform;
    
    /** 任务类型 */
    private String taskType;
    
    /** 任务唯一标识（用于幂等） */
    private String taskUniqueId;
    
    /** 业务数据开始日期 (可选) */
    private LocalDate dateRangeStart;
    
    /** 业务数据结束日期 (可选) */
    private LocalDate dateRangeEnd;
    
    /** 任务上下文参数 (JSON) */
    private Map<String, Object> context;
    
    /** 预定执行时间 (epoch millis) */
    private Long scheduledAt;
    
    /** 最大重试次数 */
    private Integer maxRetries;
    
}
