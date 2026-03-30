package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告优化规则关系 DO
 */
@TableName(value = "ads_optimization_rule_relation", autoResultMap = true)
@KeySequence("ads_optimization_rule_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsOptimizationRuleRelationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /** 店铺ID */
    private Long shopId;
    /** 广告账户ID */
    private Long accountId;

    /**
     * 内部规则ID
     */
    private Long ruleId;

    /**
     * 关联实体类型 (CAMPAIGN)
     */
    private String entityType;

    /**
     * 关联实体平台原始ID (如 Amazon CampaignId)
     */
    private String entityExternalId;

    /**
     * 关联实体内部ID
     */
    private Long entityId;

}
