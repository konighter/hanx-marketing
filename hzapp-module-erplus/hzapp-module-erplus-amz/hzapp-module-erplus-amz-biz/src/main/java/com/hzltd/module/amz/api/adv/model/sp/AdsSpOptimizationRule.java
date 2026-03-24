package com.hzltd.module.amz.api.adv.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结构体表示：/sp/campaigns/{campaignId}/optimizationRules 的优化规则返回信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpOptimizationRule {

    private String ruleId;
    private String campaignId;
    private RuleAction action;
    private RuleRecurrence recurrence;
    private String ruleCategory;
    private String ruleName;
    private String ruleSubCategory;
    private String status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleAction {
        private ActionDetails actionDetails;
        private String actionType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionDetails {
        private String actionOperator;
        private String actionUnit;
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleRecurrence {
        private Duration duration;
        private java.util.List<TimeOfDay> timesOfDay;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Duration {
        private String startTime;
        private String endTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeOfDay {
        private String startTime;
        private String endTime;
    }
}
