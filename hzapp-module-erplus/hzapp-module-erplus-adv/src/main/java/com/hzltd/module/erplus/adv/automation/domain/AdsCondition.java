package com.hzltd.module.erplus.adv.automation.domain;

import com.hzltd.module.erplus.adv.enums.automation.AdsMetricEnum;
import lombok.Data;

/**
 * 判定条件定义
 */
@Data
public class AdsCondition {
    private AdsMetricEnum metric;
    private String operator; // >=, <=, ==, >, <
    private Object value;
}
