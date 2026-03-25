package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告计划分时调度跟踪 DO
 * 用于记录下一次状态切换时间，优化调度效率
 */
@TableName(value = "ads_campaign_schedule")
@KeySequence("ads_campaign_schedule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignScheduleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 广告计划 ID
     */
    private Long campaignId;

    /**
     * 广告账户 ID (冗余，方便按账户批量查询)
     */
    private Long accountId;

    /**
     * 当前该调度逻辑认为的状态: ENABLED / PAUSED
     */
    private String currentStatus;

    /**
     * 下一次状态变迁时间 (兼容旧版本)
     */
    private LocalDateTime nextTransitionTime;
    
    /**
     * 下一次状态变迁时间 (毫秒时间戳，推荐使用)
     */
    private Long nextTransitionTimestamp;

}
