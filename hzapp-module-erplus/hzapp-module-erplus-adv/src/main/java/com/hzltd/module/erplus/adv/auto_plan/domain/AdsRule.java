package com.hzltd.module.erplus.adv.auto_plan.domain;

import lombok.Data;
import java.util.List;

/**
 * 自动化规则定义
 */
@Data
public class AdsRule {
    private String name;
    private List<AdsCondition> conditions;
    private List<AdsAction> actions;
}
