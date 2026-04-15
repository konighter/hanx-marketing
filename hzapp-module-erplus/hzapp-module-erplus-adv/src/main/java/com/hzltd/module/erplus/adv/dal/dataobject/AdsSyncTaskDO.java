package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 广告数据同步任务 DO
 *
 * 支持父子任务模型：
 * - 父任务 (parentTaskId = null): 代表账号级别的同步任务
 * - 子任务 (parentTaskId != null): 代表具体维度的报表请求
 *
 * @author hzadd
 */
@TableName(value = "ads_sync_task", autoResultMap = true)
@KeySequence("ads_sync_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSyncTaskDO extends BaseDO {

    @TableId
    private Long id;
    /** 店铺ID */
    private Long shopId;
    /** 关联广告账户ID */
    private Long accountId;
    /** 父任务 ID（子任务才有值，父任务为 null） */
    private Long parentTaskId;
    /** 广告平台: AMAZON / FACEBOOK 等 */
    private String platform;
    /** 任务类型: METADATA_FULL / METADATA_INCR / REPORT_DAILY / REPORT_DIMENSION / TOKEN_REFRESH */
    private String taskType;
    /** 任务状态: PENDING / RUNNING / SUCCESS / FAILED / PARTIAL */
    private String status;
    /** 平台报表 Job ID (异步报表场景) */
    private String platformJobId;
    /** 报表起始日期 */
    private LocalDate dateRangeStart;
    /** 报表结束日期 */
    private LocalDate dateRangeEnd;
    /**
     * 平台特定上下文 (JSON)
     * Amazon 示例: {"profileId": "123", "reportType": "spCampaigns", "baseUrl": "...", "region": "US"}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> context;
    /** 已重试次数 */
    private Integer retryCount;
    /** 最大重试次数 */
    private Integer maxRetries;
    /** 失败原因 */
    private String errorMessage;
    /** 预计可执行时间 (epoch millis 时间戳，避免时区转换) */
    private Long scheduledAt;
    /** 开始执行时间 */
    private LocalDateTime startedAt;
    /** 完成时间 */
    private LocalDateTime finishedAt;
}
