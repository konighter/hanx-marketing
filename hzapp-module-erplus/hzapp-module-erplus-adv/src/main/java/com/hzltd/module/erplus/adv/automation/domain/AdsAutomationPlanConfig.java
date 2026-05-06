package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;
import java.util.List;

/**
 * 运营计划完整配置项
 */
@Data
public class AdsAutomationPlanConfig {

    /**
     * 策略配置
     */
    private AdsStrategyConfig strategy;

    /**
     * 执行规则配置 (多条规则)
     */
    private List<AdsExecutionRuleConfig> rules;

    /**
     * 结构与命名配置
     */
    private AdsStructureConfig structure;

    /**
     * 报警与风控配置
     */
    private AdsAlertConfig alert;

}
