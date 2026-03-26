package com.hzltd.module.amz.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 亚马逊广告优化规则 DO (Bid Rule / Budget Rule)
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_adv_optimization_rule")
@KeySequence("erplus_amz_adv_optimization_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzAdvOptimizationRuleDO extends BaseDO {

    /**
     * 本地ID
     */
    @TableId
    private Long id;

    /**
     * 规则ID (Amazon侧)
     */
    private String ruleId;

    /**
     * 所属的 Campaign ID (Amazon侧)
     */
    private String campaignId;

    /**
     * 所属的 Shop ID
     */
    private String shopId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则大类 (BID, BUDGET)
     */
    private String ruleCategory;

    /**
     * 动作详情配置 (JSON 字符串，对应 actionDetails)
     */
    private String actionDetailsJson;

    /**
     * 执行周期配置 (JSON 字符串，对应 recurrence)
     */
    private String recurrenceJson;

    /**
     * 规则触发条件 (JSON 字符串，对应 conditions)
     */
    private String conditionsJson;

    /**
     * 状态 (e.g. ACTIVE, PAUSED)
     */
    private String status;

}
