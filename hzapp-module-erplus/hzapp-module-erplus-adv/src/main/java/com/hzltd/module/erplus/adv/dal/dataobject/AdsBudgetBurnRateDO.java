package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告预算消耗进度记录 DO
 */
@TableName(value = "ads_budget_burn_rate", autoResultMap = true)
@KeySequence("ads_budget_burn_rate_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsBudgetBurnRateDO extends BaseDO {

    @TableId
    private Long id;

    private String advertiserId;

    private String marketplaceId;

    private String datasetId;

    private String budgetScopeId;

    private String budgetScopeType;

    private String advertisingProductType;

    private Double budget;

    private Double budgetUsagePercentage;

    private LocalDateTime usageUpdatedTimestamp;

}
