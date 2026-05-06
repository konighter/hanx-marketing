package com.hzltd.module.erplus.adv.automation.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 广告策略配置
 */
@Data
public class AdsStrategyConfig {

    /**
     * 投放模式: AUTO_TO_MANUAL, BROAD_TO_EXACT
     */
    private String mode;

    /**
     * 策略参数 (保存用户输入的动态参数)
     */
    private Map<String, Object> params = new HashMap<>();

    @JsonAnySetter
    public void addParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * 初始每日预算 (可选，也可放入 params)
     */
    private Double initialDailyBudget;

    /**
     * 预算上限 (可选，也可放入 params)
     */
    private Double maxDailyBudget;

    /**
     * 调价/调预算 滚动周期 (天)
     */
    private Integer rollingDays = 7;

    /**
     * 晋升路径定义 (从 Stage 1 开始，Source 为 Stage 0)
     */
    private List<AdsPromotionStage> promotionPath;

}
