package com.hzltd.module.erplus.adv.metadata.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 广告优化规则保存 Request VO")
@Data
public class AdsOptimizationRuleSaveReqVO {

    @Schema(description = "店铺ID")
    private Long shopId;

    @Schema(description = "关联广告账户ID (已弃用，请使用 shopId)")
    @Deprecated
    private Long accountId;

    @Schema(description = "关联 profileId (亚马逊站点维度)")
    private String profileId;

    @Schema(description = "优化规则列表")
    private OptimizationRule optimizationRule;

    @Data
    public static class OptimizationRule {
        @Schema(description = "执行动作")
        private Action action;
        @Schema(description = "周期性设置")
        private Recurrence recurrence;
        @Schema(description = "规则分类", example = "BID")
        private String ruleCategory;
        @Schema(description = "规则名称", example = "increase_bids_by_15%_on_mornings")
        private String ruleName;
        @Schema(description = "规则子分类", example = "SCHEDULE")
        private String ruleSubCategory;
        @Schema(description = "状态", example = "ENABLED")
        private String status;
    }

    @Data
    public static class Action {
        @Schema(description = "动作详情")
        private ActionDetails actionDetails;
        @Schema(description = "动作类型", example = "ADOPT")
        private String actionType;
    }

    @Data
    public static class ActionDetails {
        @Schema(description = "动作操作符", example = "INCREMENT")
        private String actionOperator;
        @Schema(description = "动作单位", example = "PERCENT")
        private String actionUnit;
        @Schema(description = "数值", example = "15")
        private String value;
    }

    @Data
    public static class Recurrence {
        @Schema(description = "时长设置")
        private Duration duration;
    }

    @Data
    public static class Duration {
        @Schema(description = "开始时间", example = "2022-11-01T00:00:00Z")
        private String startTime;
    }

    @Data
    public static class TimeOfDay {
        @Schema(description = "开始时间", example = "05:00")
        private String startTime;
        @Schema(description = "结束时间", example = "07:00")
        private String endTime;
    }
}
