package com.hzltd.module.erplus.adv.enums.automation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告自动化指标枚举 (平台无关)
 */
@Getter
@AllArgsConstructor
public enum AdsMetricEnum {

    IMPRESSIONS("impressions", "展示量"),
    CLICKS("clicks", "点击量"),
    SPEND("spend", "花费"),
    ORDERS("orders", "订单量"),
    SALES("sales", "销售额"),
    CPA("cpa", "每订单成本"),
    ROAS("roas", "广告支出回报率"),
    CTR("ctr", "点击率"),
    CVR("cvr", "转化率");

    private final String code;
    private final String name;

}
