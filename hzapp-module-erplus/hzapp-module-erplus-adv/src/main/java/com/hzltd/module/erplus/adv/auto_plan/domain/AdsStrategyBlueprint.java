package com.hzltd.module.erplus.adv.auto_plan.domain;

import lombok.Data;
import java.util.List;

/**
 * 自动化策略蓝图定义
 */
@Data
public class AdsStrategyBlueprint {
    private List<AdsRule> rules;
}
