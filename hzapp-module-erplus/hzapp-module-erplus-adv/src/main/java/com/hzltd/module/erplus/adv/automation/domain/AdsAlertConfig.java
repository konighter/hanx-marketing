package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;
import java.util.List;

/**
 * 广告报警与风控配置
 */
@Data
public class AdsAlertConfig {

    /**
     * 是否启用报警
     */
    private Boolean enabled = true;

    /**
     * 规则列表
     */
    private List<AlertRule> rules;

    @Data
    public static class AlertRule {
        /**
         * 报警类型: 
         * BUDGET_SPEED (消耗过快), 
         * ACOS_EXCEED (ACOS超标), 
         * ROAS_LOW (ROAS过低),
         * ZERO_CONVERSION (高点击无转化)
         */
        private String type;

        /**
         * 阈值 (如预算消耗百分比, 或 ACOS 具体数值)
         */
        private Double threshold;

        /**
         * 统计时间窗口 (单位: 小时)
         */
        private Integer windowHours;

        /**
         * 执行动作: 
         * NOTIFY (系统通知/邮件), 
         * PAUSE_CAMPAIGN (暂停广告活动), 
         * REDUCE_BUDGET (按比例削减预算),
         * EMERGENCY_STOP (紧急停止计划)
         */
        private List<String> actions;

        /**
         * 动作参数 (如削减预算的比例 0.5)
         */
        private Double actionParam;
    }
}
