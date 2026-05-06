package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;
import java.util.List;

/**
 * 自动化策略蓝图定义
 */
@Data
public class AdsStrategyBlueprint {
    private List<AdsRule> rules;
}
