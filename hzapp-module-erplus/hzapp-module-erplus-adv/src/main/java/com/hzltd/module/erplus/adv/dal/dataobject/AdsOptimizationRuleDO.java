package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告优化规则 DO
 */
@TableName(value = "ads_optimization_rule", autoResultMap = true)
@KeySequence("ads_optimization_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsOptimizationRuleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 关联广告账户ID
     */
    private Long accountId;

    /**
     * 平台原始规则ID
     */
    private String ruleId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则分类 (BID)
     */
    private String category;

    /**
     * 规则子分类 (SCHEDULE)
     */
    private String subCategory;

    /**
     * 状态 (ENABLED / DISABLED)
     */
    private String status;

    /**
     * 周期性设置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object recurrence;

    /**
     * 执行动作
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object action;

    /**
     * 其他扩展信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extData;

}
