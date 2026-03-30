package com.hzltd.module.erplus.adv.metadata.service.report;

import cn.hutool.core.date.DateUtil;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportDailyDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportSummaryDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportDailyMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportHourlyMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportSummaryMapper;
import com.hzltd.module.erplus.adv.metadata.vo.report.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;


/**
 * 广告性能报表 Service 实现类
 */
@Service
@Slf4j
public class AdsReportServiceImpl implements AdsReportService {

    @Resource
    private AdsReportHourlyMapper adsReportHourlyMapper;

    @Resource
    private AdsReportDailyMapper adsReportDailyMapper;

    @Resource
    private AdsReportSummaryMapper adsReportSummaryMapper;

    // ==================== 小时数据写入与聚合 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHourlyReports(List<AdsReportHourlyDO> reports) {
        if (reports == null || reports.isEmpty()) return;

        // Doris Aggregate Model: 直接 INSERT，同 Key 行会自动 SUM 聚合
        for (AdsReportHourlyDO report : reports) {
            adsReportHourlyMapper.insert(report);
        }
    }

    @Override
    public void aggregateHourlyToSummary(Long shopId, Long accountId, String entityType, Long entityId, LocalDateTime reportHour) {
        String day = DateUtil.format(DateUtil.date(reportHour), "yyyy-MM-dd");
        String week = DateUtil.format(DateUtil.date(reportHour), "yyyy-ww");
        String month = DateUtil.format(DateUtil.date(reportHour), "yyyy-MM");

        updateSummaryFromHourly(shopId, accountId, entityType, entityId, "DAY", day, reportHour);
        updateSummaryFromHourly(shopId, accountId, entityType, entityId, "WEEK", week, reportHour);
        updateSummaryFromHourly(shopId, accountId, entityType, entityId, "MONTH", month, reportHour);
        
        if (!AdsEntityTypeEnum.ACCOUNT.name().equals(entityType)) {
            updateSummaryFromHourly(shopId, accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "DAY", day, reportHour);
            updateSummaryFromHourly(shopId, accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "WEEK", week, reportHour);
            updateSummaryFromHourly(shopId, accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "MONTH", month, reportHour);
        }
    }

    // ==================== 查询接口 ====================

    @Override
    public AdsPerformanceRespVO getPerformanceScorecard(AdsPerformanceReqVO reqVO) {
        String entityType = reqVO.getEntityType() != null ? reqVO.getEntityType().name() : AdsEntityTypeEnum.ACCOUNT.name();
        Long entityId = reqVO.getEntityId() != null ? reqVO.getEntityId() : reqVO.getAccountId();

        List<AdsReportSummaryDO> list = adsReportSummaryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(reqVO.getShopId() != null, AdsReportSummaryDO::getShopId, reqVO.getShopId())
                .eq(reqVO.getAccountId() != null, AdsReportSummaryDO::getAccountId, reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getEntityType, entityType)
                .eq(AdsReportSummaryDO::getEntityId, entityId)
                .eq(AdsReportSummaryDO::getPeriodType, "DAY")
                .between(AdsReportSummaryDO::getPeriodValue, reqVO.getStartDate().toString(), reqVO.getEndDate().toString())
        );

        AdsPerformanceRespVO resp = new AdsPerformanceRespVO();
        Map<String, Object> totalMetrics = new HashMap<>();
        
        for (AdsReportSummaryDO summary : list) {
            mergeMetrics(totalMetrics, summary.getMetrics());
        }

        // Issue 3 fix: 包含计算型指标 (ROAS, CPC, CTR)
        resp.setMetrics(buildMetricsList(totalMetrics));
        // Issue 6 fix: 填充维度信息
        resp.setDimensions(Arrays.asList(
            new AdsDimensionVO("entityType", "实体类型", entityType),
            new AdsDimensionVO("entityId", "实体ID", entityId),
            new AdsDimensionVO("dateRange", "日期范围", reqVO.getStartDate() + " ~ " + reqVO.getEndDate())
        ));
        
        return resp;
    }

    @Override
    public AdsReportTrendRespVO getPerformanceTrend(AdsPerformanceReqVO reqVO) {
        String entityType = reqVO.getEntityType() != null ? reqVO.getEntityType().name() : AdsEntityTypeEnum.ACCOUNT.name();
        Long entityId = reqVO.getEntityId() != null ? reqVO.getEntityId() : reqVO.getAccountId();

        List<AdsReportSummaryDO> list = adsReportSummaryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(reqVO.getShopId() != null, AdsReportSummaryDO::getShopId, reqVO.getShopId())
                .eq(reqVO.getAccountId() != null, AdsReportSummaryDO::getAccountId, reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getEntityType, entityType)
                .eq(AdsReportSummaryDO::getEntityId, entityId)
                .eq(AdsReportSummaryDO::getPeriodType, reqVO.getTimeUnit().toUpperCase())
                .between(AdsReportSummaryDO::getPeriodValue, reqVO.getStartDate().toString(), reqVO.getEndDate().toString())
                .orderByAsc(AdsReportSummaryDO::getPeriodValue)
        );

        AdsReportTrendRespVO resp = new AdsReportTrendRespVO();
        List<String> timeList = new ArrayList<>();
        Map<String, List<Object>> seriesData = new HashMap<>();

        for (AdsReportSummaryDO summary : list) {
            timeList.add(summary.getPeriodValue());
            Map<String, Object> metricsWithComputed = addComputedMetrics(summary.getMetrics());
            metricsWithComputed.forEach((key, value) -> {
                seriesData.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            });
        }

        resp.setTimeList(timeList);
        resp.setSeriesData(new HashMap<>(seriesData));
        
        return resp;
    }

    @Override
    public List<AdsPerformanceRespVO> getPerformanceDrilldown(AdsPerformanceReqVO reqVO) {
        List<AdsPerformanceRespVO> result = new ArrayList<>();
        
        // Mock Data
        AdsPerformanceRespVO campaign = new AdsPerformanceRespVO();
        campaign.setDimensions(Arrays.asList(
            new AdsDimensionVO("id", "ID", "campaign-" + reqVO.getEntityId()),
            new AdsDimensionVO("name", "名称", "搜索广告系列-基础款 (Mock)"),
            new AdsDimensionVO("level", "层级", "campaign"),
            new AdsDimensionVO("status", "状态", "ENABLED")
        ));
        campaign.setMetrics(Arrays.asList(
            new AdsMetricVO("spend", "花费", 5280.0, BigDecimal.valueOf(5.2), "$"),
            new AdsMetricVO("impressions", "展现", 154200L, BigDecimal.valueOf(-2.1), null),
            new AdsMetricVO("clicks", "点击", 8560L, BigDecimal.valueOf(1.5), null),
            new AdsMetricVO("sales", "销售额", 25600.0, BigDecimal.valueOf(8.4), "$"),
            new AdsMetricVO("orders", "订单", 425, BigDecimal.valueOf(10.2), null),
            new AdsMetricVO("roas", "ROAS", 4.85, null, null),
            new AdsMetricVO("cpc", "CPC", 0.62, null, "$"),
            new AdsMetricVO("ctr", "CTR", 0.0555, null, "%")
        ));

        List<AdsPerformanceRespVO> adGroups = new ArrayList<>();
        
        AdsPerformanceRespVO group1 = new AdsPerformanceRespVO();
        group1.setDimensions(Arrays.asList(
            new AdsDimensionVO("id", "ID", "adgroup-1"),
            new AdsDimensionVO("name", "名称", "精准匹配-核心词 (Mock)"),
            new AdsDimensionVO("level", "层级", "adGroup"),
            new AdsDimensionVO("status", "状态", "ENABLED")
        ));
        group1.setMetrics(Arrays.asList(
            new AdsMetricVO("spend", "花费", 3800.0, BigDecimal.valueOf(3.2), "$"),
            new AdsMetricVO("impressions", "展现", 104000L, null, null),
            new AdsMetricVO("clicks", "点击", 6500L, null, null),
            new AdsMetricVO("sales", "销售额", 22000.0, BigDecimal.valueOf(6.4), "$"),
            new AdsMetricVO("orders", "订单", 340, null, null),
            new AdsMetricVO("roas", "ROAS", 5.79, null, null),
            new AdsMetricVO("cpc", "CPC", 0.58, null, "$"),
            new AdsMetricVO("ctr", "CTR", 0.0625, null, "%")
        ));

        // Ads under Group 1
        List<AdsPerformanceRespVO> ads1 = new ArrayList<>();
        AdsPerformanceRespVO ad1 = new AdsPerformanceRespVO();
        ad1.setDimensions(Arrays.asList(
            new AdsDimensionVO("id", "ID", "ad-1-1"),
            new AdsDimensionVO("name", "名称", "主图A (场景化) (Mock)"),
            new AdsDimensionVO("level", "层级", "ad"),
            new AdsDimensionVO("status", "状态", "ENABLED")
        ));
        ad1.setMetrics(Arrays.asList(
            new AdsMetricVO("spend", "花费", 2500.0, null, "$"),
            new AdsMetricVO("impressions", "展现", 80000L, null, null),
            new AdsMetricVO("clicks", "点击", 5200L, null, null),
            new AdsMetricVO("sales", "销售额", 18500.0, null, "$"),
            new AdsMetricVO("orders", "订单", 280, null, null),
            new AdsMetricVO("roas", "ROAS", 7.4, null, null),
            new AdsMetricVO("cpc", "CPC", 0.48, null, "$"),
            new AdsMetricVO("ctr", "CTR", 0.065, null, "%")
        ));
        ads1.add(ad1);
        group1.setChildren(ads1);
        
        adGroups.add(group1);

        AdsPerformanceRespVO group2 = new AdsPerformanceRespVO();
        group2.setDimensions(Arrays.asList(
            new AdsDimensionVO("id", "ID", "adgroup-2"),
            new AdsDimensionVO("name", "名称", "广泛匹配-探索 (Mock)"),
            new AdsDimensionVO("level", "层级", "adGroup"),
            new AdsDimensionVO("status", "状态", "ENABLED")
        ));
        group2.setMetrics(Arrays.asList(
            new AdsMetricVO("spend", "花费", 1480.0, null, "$"),
            new AdsMetricVO("impressions", "展现", 50200L, null, null),
            new AdsMetricVO("clicks", "点击", 2060L, null, null),
            new AdsMetricVO("sales", "销售额", 3600.0, null, "$"),
            new AdsMetricVO("orders", "订单", 85, null, null),
            new AdsMetricVO("roas", "ROAS", 2.43, null, null),
            new AdsMetricVO("cpc", "CPC", 0.72, null, "$"),
            new AdsMetricVO("ctr", "CTR", 0.041, null, "%")
        ));
        adGroups.add(group2);
        
        campaign.setChildren(adGroups);
        result.add(campaign);
        
        return result;
    }

    // ==================== 指标构建 ====================

    /**
     * 将原始 Map 指标构建为动态列表，包含计算型指标
     * Issue 3 fix: 在输出时自动派生 ROAS / CPC / CTR
     */
    private List<AdsMetricVO> buildMetricsList(Map<String, Object> rawMetrics) {
        Map<String, Object> withComputed = addComputedMetrics(rawMetrics);
        List<AdsMetricVO> list = new ArrayList<>();
        // 按固定顺序输出，保证前端一致性
        String[] orderedKeys = {"spend", "sales", "impressions", "clicks", "orders", "roas", "cpc", "ctr", "acos"};
        for (String key : orderedKeys) {
            Object value = withComputed.get(key);
            if (value != null) {
                list.add(AdsMetricVO.builder()
                    .key(key)
                    .name(getMetricName(key))
                    .value(value)
                    .unit(getMetricUnit(key))
                    .build());
            }
        }
        // 追加未在 orderedKeys 中的其他指标
        withComputed.forEach((key, value) -> {
            if (!Arrays.asList(orderedKeys).contains(key)) {
                list.add(AdsMetricVO.builder()
                    .key(key)
                    .name(getMetricName(key))
                    .value(value)
                    .unit(getMetricUnit(key))
                    .build());
            }
        });
        return list;
    }

    /**
     * 在原始指标基础上计算派生型指标
     */
    private Map<String, Object> addComputedMetrics(Map<String, Object> rawMetrics) {
        if (rawMetrics == null) return new HashMap<>();
        Map<String, Object> result = new HashMap<>(rawMetrics);
        
        BigDecimal spend = toBigDecimal(rawMetrics.get("spend"));
        BigDecimal sales = toBigDecimal(rawMetrics.get("sales"));
        long impressions = toLong(rawMetrics.get("impressions"));
        long clicks = toLong(rawMetrics.get("clicks"));

        // ROAS = sales / spend
        if (spend.compareTo(BigDecimal.ZERO) > 0) {
            result.put("roas", sales.divide(spend, 2, RoundingMode.HALF_UP));
            result.put("cpc", spend.divide(BigDecimal.valueOf(clicks > 0 ? clicks : 1), 2, RoundingMode.HALF_UP));
            result.put("acos", spend.divide(sales.compareTo(BigDecimal.ZERO) > 0 ? sales : BigDecimal.ONE, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        // CTR = clicks / impressions
        if (impressions > 0) {
            result.put("ctr", BigDecimal.valueOf(clicks).divide(BigDecimal.valueOf(impressions), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }

        return result;
    }

    // ==================== 聚合逻辑 ====================

    /**
     * Issue 2 fix: 聚合时按时间范围过滤小时数据
     */
    private void updateSummaryFromHourly(Long shopId, Long accountId, String entityType, Long entityId,
                                          String periodType, String periodValue, LocalDateTime reportHour) {
        AdsReportSummaryDO summary = adsReportSummaryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(AdsReportSummaryDO::getShopId, shopId)
                .eq(AdsReportSummaryDO::getAccountId, accountId)
                .eq(AdsReportSummaryDO::getEntityType, entityType)
                .eq(AdsReportSummaryDO::getEntityId, entityId)
                .eq(AdsReportSummaryDO::getPeriodType, periodType)
                .eq(AdsReportSummaryDO::getPeriodValue, periodValue)
        );

        if (summary == null) {
            summary = new AdsReportSummaryDO();
            summary.setShopId(shopId);
            summary.setAccountId(accountId);
            summary.setEntityType(entityType);
            summary.setEntityId(entityId);
            summary.setPeriodType(periodType);
            summary.setPeriodValue(periodValue);
            summary.setMetrics(new HashMap<>());
            adsReportSummaryMapper.insert(summary);
        }

        // 计算该时间周期的起止范围
        LocalDateTime rangeStart = calculatePeriodStart(periodType, reportHour);
        LocalDateTime rangeEnd = calculatePeriodEnd(periodType, reportHour);

        Map<String, Object> aggregatedMetrics = new HashMap<>();
        List<AdsReportHourlyDO> hourlies = adsReportHourlyMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportHourlyDO>()
                .eq(AdsReportHourlyDO::getShopId, shopId)
                .eq(AdsReportHourlyDO::getAccountId, accountId)
                .eq(AdsReportHourlyDO::getGroupColumn, entityType)
                .between(AdsReportHourlyDO::getReportHour, rangeStart, rangeEnd)
        );

        for (AdsReportHourlyDO hour : hourlies) {
            Map<String, Object> hourMetrics = hourlyToMetricsMap(hour);
            mergeMetrics(aggregatedMetrics, hourMetrics);
        }

        summary.setMetrics(aggregatedMetrics);
        adsReportSummaryMapper.updateById(summary);
    }

    /**
     * 计算周期起始时间
     */
    private LocalDateTime calculatePeriodStart(String periodType, LocalDateTime reportHour) {
        LocalDate date = reportHour.toLocalDate();
        switch (periodType) {
            case "DAY":
                return date.atStartOfDay();
            case "WEEK":
                return date.with(WeekFields.ISO.dayOfWeek(), 1).atStartOfDay();
            case "MONTH":
                return date.withDayOfMonth(1).atStartOfDay();
            default:
                return date.atStartOfDay();
        }
    }

    /**
     * 计算周期结束时间
     */
    private LocalDateTime calculatePeriodEnd(String periodType, LocalDateTime reportHour) {
        LocalDate date = reportHour.toLocalDate();
        switch (periodType) {
            case "DAY":
                return date.atTime(LocalTime.MAX);
            case "WEEK":
                return date.with(WeekFields.ISO.dayOfWeek(), 7).atTime(LocalTime.MAX);
            case "MONTH":
                return date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
            default:
                return date.atTime(LocalTime.MAX);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 将 AdsReportHourlyDO 的展平指标列转换为 Map 格式
     */
    private Map<String, Object> hourlyToMetricsMap(AdsReportHourlyDO hour) {
        Map<String, Object> map = new HashMap<>();
        if (hour.getImpressions() != null) map.put("impressions", hour.getImpressions());
        if (hour.getClicks() != null) map.put("clicks", hour.getClicks());
        if (hour.getCost() != null) map.put("spend", hour.getCost());
        if (hour.getSales7d() != null) map.put("sales", hour.getSales7d());
        if (hour.getOrders7d() != null) map.put("orders", hour.getOrders7d());
        return map;
    }

    private void mergeMetrics(Map<String, Object> target, Map<String, Object> source) {
        if (source == null) return;
        source.forEach((key, value) -> {
            if (value instanceof Number) {
                BigDecimal targetVal = new BigDecimal(target.getOrDefault(key, 0).toString());
                target.put(key, targetVal.add(new BigDecimal(value.toString())));
            } else {
                target.put(key, value);
            }
        });
    }

    private String getMetricName(String key) {
        switch (key) {
            case "spend": return "花费";
            case "sales": return "销售额";
            case "impressions": return "展现量";
            case "clicks": return "点击量";
            case "orders": return "订单量";
            case "roas": return "ROAS";
            case "cpc": return "CPC";
            case "ctr": return "CTR";
            case "acos": return "ACOS";
            default: return key;
        }
    }

    private String getMetricUnit(String key) {
        switch (key) {
            case "spend": case "sales": case "cpc": return "$";
            case "ctr": case "acos": return "%";
            default: return null;
        }
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        return new BigDecimal(obj.toString());
    }

    private long toLong(Object obj) {
        if (obj == null) return 0L;
        return Long.parseLong(obj.toString().split("\\.")[0]);
    }
}
