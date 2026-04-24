package com.hzltd.module.erplus.adv.dal.mysql;

import java.util.*;

/**
 * 广告报表指标映射配置
 * 将逻辑指标（Logical Metrics）解构为物理指标（Physical Metrics）
 */
public class AdsReportMetricConfig {

    private static final Map<String, List<String>> METRIC_MAPPINGS = new HashMap<>();

    static {
        // ROAS = Sales / Spend
        METRIC_MAPPINGS.put("roas", Arrays.asList("cost AS spend", "sales"));
        // ACOS = Spend / Sales
        METRIC_MAPPINGS.put("acos", Arrays.asList("cost AS spend", "sales"));
        // CPC = Spend / Clicks
        METRIC_MAPPINGS.put("cpc", Arrays.asList("cost AS spend", "clicks"));
        // CTR = Clicks / Impressions
        METRIC_MAPPINGS.put("ctr", Arrays.asList("clicks", "impressions"));
        // Spend 统一映射到数据库的 cost 字段
        METRIC_MAPPINGS.put("spend", Collections.singletonList("cost AS spend"));
    }

    /**
     * 根据请求的逻辑指标列表，获取数据库中需要查询的物理列名（包含别名）
     * @param logicalMetrics 逻辑指标列表 (如 ["roas", "impressions"])
     * @return 物理字段集合 (如 ["cost AS spend", "sales", "impressions"])
     */
    public static Set<String> getPhysicalMetrics(List<String> logicalMetrics) {
        if (logicalMetrics == null || logicalMetrics.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> dbMetricsToFetch = new HashSet<>();
        for (String m : logicalMetrics) {
            if (METRIC_MAPPINGS.containsKey(m)) {
                dbMetricsToFetch.addAll(METRIC_MAPPINGS.get(m));
            } else {
                // 原生指标，直接添加
                dbMetricsToFetch.add(m);
            }
        }
        return dbMetricsToFetch;
    }
}
