package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 广告数据同步任务 DO
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
    /** 关联广告账户ID */
    private Long accountId;
    /** 任务类型: METADATA_FULL / METADATA_INCR / REPORT_DAILY / TOKEN_REFRESH */
    private String taskType;
    /** 任务状态: PENDING / RUNNING / SUCCESS / FAILED / PARTIAL */
    private String status;
    /** 平台报表 Job ID (异步报表场景) */
    private String platformJobId;
    /** 报表起始日期 */
    private LocalDate dateRangeStart;
    /** 报表结束日期 */
    private LocalDate dateRangeEnd;
    /** 已重试次数 */
    private Integer retryCount;
    /** 最大重试次数 */
    private Integer maxRetries;
    /** 失败原因 */
    private String errorMessage;
    /** 开始执行时间 */
    private LocalDateTime startedAt;
    /** 完成时间 */
    private LocalDateTime finishedAt;
}
