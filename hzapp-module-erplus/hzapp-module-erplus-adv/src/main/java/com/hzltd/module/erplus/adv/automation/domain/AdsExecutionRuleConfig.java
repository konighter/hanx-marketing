package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;

import java.util.Map;

/**
 * 广告执行规则配置 (动态引擎版)
 * 对应模版中的 rules 数组项
 */
@Data
public class AdsExecutionRuleConfig {

    /**
     * 规则名称 (用于日志记录)
     */
    private String name;

    /**
     * 执行条件 (表达式，如: orders >= {minOrders} && roas >= {targetRoas})
     * 支持 SpEL 或自定义解析器，{} 中的变量将从 strategy.params 中替换
     */
    private String condition;

    /**
     * 触发动作 (如: PROMOTE_TO_EXACT, CREATE_ISOLATED_CAMPAIGN, ADJUST_BID)
     */
    private String action;

    /**
     * 规则品类,  campaign, adGroup, ad, keyword 等
     */
    private String category;

    /**
     * 动作参数 (可选，用于辅助 action 执行)
     */
    private Map<String, Object> actionParams;

}
