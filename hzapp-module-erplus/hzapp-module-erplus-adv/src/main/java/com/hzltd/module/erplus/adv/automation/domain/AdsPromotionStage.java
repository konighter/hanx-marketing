package com.hzltd.module.erplus.adv.automation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告晋升阶段定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdsPromotionStage {

    /**
     * 阶段顺序 (1, 2, 3...)
     */
    private Integer sequence;

    /**
     * 目标匹配方式: AUTO, BROAD, PHRASE, EXACT, TARGETING
     */
    private String matchType;

    /**
     * 对应阶段的命名后缀 (可选，覆盖全局配置)
     */
    private String namingSuffix;

    /**
     * 出价系数 (相对于目标 CPC)
     */
    private Double bidFactor = 1.0;

}
