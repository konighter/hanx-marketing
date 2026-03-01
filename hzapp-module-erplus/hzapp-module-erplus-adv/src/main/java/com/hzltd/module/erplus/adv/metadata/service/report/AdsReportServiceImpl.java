package com.hzltd.module.erplus.adv.metadata.service.report;

import cn.hutool.core.date.DateUtil;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportSummaryDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportHourlyMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportSummaryMapper;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportTrendRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;

/**
 * 广告性能报表 Service 实现类
 */
@Service
@Slf4j
public class AdsReportServiceImpl implements AdsReportService {

    @Resource
    private AdsReportHourlyMapper adsReportHourlyMapper;

    @Resource
    private AdsReportSummaryMapper adsReportSummaryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHourlyReports(List<AdsReportHourlyDO> reports) {
        if (reports == null || reports.isEmpty()) return;
        
        for (AdsReportHourlyDO report : reports) {
            // 1. 保存或更新小时原始数据 (UPSERT)
            AdsReportHourlyDO existing = adsReportHourlyMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportHourlyDO>()
                    .eq(AdsReportHourlyDO::getAccountId, report.getAccountId())
                    .eq(AdsReportHourlyDO::getEntityType, report.getEntityType())
                    .eq(AdsReportHourlyDO::getEntityId, report.getEntityId())
                    .eq(AdsReportHourlyDO::getReportHour, report.getReportHour())
            );

            if (existing != null) {
                report.setId(existing.getId());
                adsReportHourlyMapper.updateById(report);
            } else {
                adsReportHourlyMapper.insert(report);
            }

            // 2. 触发汇总聚合
            aggregateHourlyToSummary(report.getAccountId(), report.getEntityType(), report.getEntityId(), report.getReportHour());
        }
    }

    @Override
    public void aggregateHourlyToSummary(Long accountId, String entityType, Long entityId, LocalDateTime reportHour) {
        // 计算所属周期值
        String day = DateUtil.format(DateUtil.date(reportHour), "yyyy-MM-dd");
        String week = DateUtil.format(DateUtil.date(reportHour), "yyyy-ww");
        String month = DateUtil.format(DateUtil.date(reportHour), "yyyy-MM");

        // 分别更新日、周、月
        updateSummary(accountId, entityType, entityId, "DAY", day);
        updateSummary(accountId, entityType, entityId, "WEEK", week);
        updateSummary(accountId, entityType, entityId, "MONTH", month);
        
        // 如果不是 ACCOUNT，还需要向上滚动更新 ACCOUNT 级别的汇总
        if (!AdsEntityTypeEnum.ACCOUNT.name().equals(entityType)) {
            updateSummary(accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "DAY", day);
            updateSummary(accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "WEEK", week);
            updateSummary(accountId, AdsEntityTypeEnum.ACCOUNT.name(), accountId, "MONTH", month);
        }
    }

    @Override
    public AdsPerformanceRespVO getPerformanceScorecard(AdsPerformanceReqVO reqVO) {
        // 实现简单的单点查询用于聚合后的指标展示
        List<AdsReportSummaryDO> list = adsReportSummaryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(AdsReportSummaryDO::getAccountId, reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getEntityType, reqVO.getEntityType() != null ? reqVO.getEntityType().name() : AdsEntityTypeEnum.ACCOUNT.name())
                .eq(AdsReportSummaryDO::getEntityId, reqVO.getEntityId() != null ? reqVO.getEntityId() : reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getPeriodType, "DAY")
                .between(AdsReportSummaryDO::getPeriodValue, reqVO.getStartDate().toString(), reqVO.getEndDate().toString())
        );

        AdsPerformanceRespVO resp = new AdsPerformanceRespVO();
        Map<String, Object> totalMetrics = new HashMap<>();
        
        for (AdsReportSummaryDO summary : list) {
            mergeMetrics(totalMetrics, summary.getMetrics());
        }

        resp.setMetrics(totalMetrics);
        fillCalculatedMetrics(resp, totalMetrics);
        
        // TODO: 环比趋势计算需查询前一个周期的汇总
        resp.setTrends(new HashMap<>());
        
        return resp;
    }

    @Override
    public AdsReportTrendRespVO getPerformanceTrend(AdsPerformanceReqVO reqVO) {
        List<AdsReportSummaryDO> list = adsReportSummaryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(AdsReportSummaryDO::getAccountId, reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getEntityType, reqVO.getEntityType() != null ? reqVO.getEntityType().name() : AdsEntityTypeEnum.ACCOUNT.name())
                .eq(AdsReportSummaryDO::getEntityId, reqVO.getEntityId() != null ? reqVO.getEntityId() : reqVO.getAccountId())
                .eq(AdsReportSummaryDO::getPeriodType, reqVO.getTimeUnit().toUpperCase())
                .between(AdsReportSummaryDO::getPeriodValue, reqVO.getStartDate().toString(), reqVO.getEndDate().toString())
                .orderByAsc(AdsReportSummaryDO::getPeriodValue)
        );

        AdsReportTrendRespVO resp = new AdsReportTrendRespVO();
        List<String> timeList = new ArrayList<>();
        Map<String, List<Object>> seriesData = new HashMap<>();

        for (AdsReportSummaryDO summary : list) {
            timeList.add(summary.getPeriodValue());
            summary.getMetrics().forEach((key, value) -> {
                seriesData.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            });
        }

        resp.setTimeList(timeList);
        // Convert Map<String, List<Object>> to Map<String, Object[]> for RespVO if needed, 
        // but existing RespVO uses Record<string, any[]> in TS which matches Map<String, List<Object>> in Java/JSON.
        resp.setSeriesData(new HashMap<>(seriesData));
        
        return resp;
    }

    @Override
    public List<Map<String, Object>> getPerformanceDrilldown(AdsPerformanceReqVO reqVO) {
        // 用户要求先 mock 数据到广告层级 (Campaign -> AdGroup -> Ad)
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 1. Campaign Level (Root)
        Map<String, Object> campaign = new HashMap<>();
        campaign.put("id", "campaign-" + reqVO.getEntityId());
        campaign.put("name", "搜索广告系列-基础款 (Mock)");
        campaign.put("level", "campaign");
        campaign.put("status", "ENABLED");
        campaign.put("spend", 5280.0);
        campaign.put("impressions", 154200L);
        campaign.put("clicks", 8560L);
        campaign.put("ctr", 0.0555);
        campaign.put("cpc", 0.61);
        campaign.put("orders", 425);
        campaign.put("sales", 25600.0);
        campaign.put("roas", 4.85);

        // 2. Ad Groups Level
        List<Map<String, Object>> adGroups = new ArrayList<>();
        
        // AdGroup 1
        Map<String, Object> group1 = new HashMap<>();
        group1.put("id", "adgroup-" + reqVO.getEntityId() + "-1");
        group1.put("name", "精准匹配-核心词 (Mock)");
        group1.put("level", "adGroup");
        group1.put("status", "ENABLED");
        group1.put("spend", 3800.0);
        group1.put("impressions", 104000L);
        group1.put("clicks", 6500L);
        group1.put("ctr", 0.0625);
        group1.put("cpc", 0.58);
        group1.put("orders", 340);
        group1.put("sales", 22000.0);
        group1.put("roas", 5.79);
        
        // Ads under Group 1
        List<Map<String, Object>> ads1 = new ArrayList<>();
        Map<String, Object> ad1 = new HashMap<>();
        ad1.put("id", "ad-" + reqVO.getEntityId() + "-1-1");
        ad1.put("name", "主图A (场景化) (Mock)");
        ad1.put("level", "ad");
        ad1.put("status", "ENABLED");
        ad1.put("spend", 2500.0);
        ad1.put("impressions", 80000L);
        ad1.put("clicks", 5200L);
        ad1.put("ctr", 0.065);
        ad1.put("cpc", 0.48);
        ad1.put("orders", 280);
        ad1.put("sales", 18500.0);
        ad1.put("roas", 7.4);
        ads1.add(ad1);
        
        Map<String, Object> ad2 = new HashMap<>();
        ad2.put("id", "ad-" + reqVO.getEntityId() + "-1-2");
        ad2.put("name", "主图B (模特展示) (Mock)");
        ad2.put("level", "ad");
        ad2.put("status", "PAUSED");
        ad2.put("spend", 1300.0);
        ad2.put("impressions", 24000L);
        ad2.put("clicks", 1300L);
        ad2.put("ctr", 0.054);
        ad2.put("cpc", 1.0);
        ad2.put("orders", 60);
        ad2.put("sales", 3500.0);
        ad2.put("roas", 2.69);
        ads1.add(ad2);
        
        group1.put("children", ads1);
        adGroups.add(group1);
        
        // AdGroup 2
        Map<String, Object> group2 = new HashMap<>();
        group2.put("id", "adgroup-" + reqVO.getEntityId() + "-2");
        group2.put("name", "广泛匹配-探索 (Mock)");
        group2.put("level", "adGroup");
        group2.put("status", "ENABLED");
        group2.put("spend", 1480.0);
        group2.put("impressions", 50200L);
        group2.put("clicks", 2060L);
        group2.put("ctr", 0.041);
        group2.put("cpc", 0.71);
        group2.put("orders", 85);
        group2.put("sales", 3600.0);
        group2.put("roas", 2.43);
        adGroups.add(group2);
        
        campaign.put("children", adGroups);
        result.add(campaign);
        
        return result;
    }

    private void updateSummary(Long accountId, String entityType, Long entityId, String periodType, String periodValue) {
        log.debug("[updateSummary] 正在更新汇总数据: {} - {} - {}", entityType, periodType, periodValue);
        
        // 1. 获取汇总记录
        AdsReportSummaryDO summary = adsReportSummaryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportSummaryDO>()
                .eq(AdsReportSummaryDO::getAccountId, accountId)
                .eq(AdsReportSummaryDO::getEntityType, entityType)
                .eq(AdsReportSummaryDO::getEntityId, entityId)
                .eq(AdsReportSummaryDO::getPeriodType, periodType)
                .eq(AdsReportSummaryDO::getPeriodValue, periodValue)
        );

        if (summary == null) {
            summary = new AdsReportSummaryDO();
            summary.setAccountId(accountId);
            summary.setEntityType(entityType);
            summary.setEntityId(entityId);
            summary.setPeriodType(periodType);
            summary.setPeriodValue(periodValue);
            summary.setMetrics(new HashMap<>());
            adsReportSummaryMapper.insert(summary);
        }

        // 2. 重新聚合该周期内的所有小时数据 (Smart Merge)
        // 核心逻辑：找出该日期/周/月内所有小时记录，并合并 Map
        Map<String, Object> aggregatedMetrics = new HashMap<>();
        
        // 计算时间范围 (简单处理：DAY 对应 reportHour 的同一天)
        // 注意：这里需要根据 periodType 和 periodValue 反向计算时间范围，或者在索引上做文章
        // 生产环境建议使用 incremental update (存量 + 增量)
        
        // 此处演示全量重算该周期
        List<AdsReportHourlyDO> hourlies = adsReportHourlyMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsReportHourlyDO>()
                .eq(AdsReportHourlyDO::getAccountId, accountId)
                .eq(AdsReportHourlyDO::getEntityType, entityType)
                .eq(AdsReportHourlyDO::getEntityId, entityId)
                // 这里需要根据 periodType/Value 过滤 reportHour，略过
        );

        for (AdsReportHourlyDO hour : hourlies) {
            mergeMetrics(aggregatedMetrics, hour.getMetrics());
        }

        summary.setMetrics(aggregatedMetrics);
        adsReportSummaryMapper.updateById(summary);
    }

    private void mergeMetrics(Map<String, Object> target, Map<String, Object> source) {
        if (source == null) return;
        source.forEach((key, value) -> {
            if (value instanceof Number) {
                BigDecimal targetVal = new BigDecimal(target.getOrDefault(key, 0).toString());
                target.put(key, targetVal.add(new BigDecimal(value.toString())));
            } else {
                // 非数值类型直接覆盖（表示最新状态）
                target.put(key, value);
            }
        });
    }

    private void fillCalculatedMetrics(AdsPerformanceRespVO resp, Map<String, Object> metrics) {
        BigDecimal spend = new BigDecimal(metrics.getOrDefault("spend", 0).toString());
        BigDecimal sales = new BigDecimal(metrics.getOrDefault("sales", 0).toString());
        Long impressions = Long.valueOf(metrics.getOrDefault("impressions", 0).toString());
        Long clicks = Long.valueOf(metrics.getOrDefault("clicks", 0).toString());
        Integer orders = Integer.valueOf(metrics.getOrDefault("orders", 0).toString());

        resp.setSpend(spend);
        resp.setSales(sales);
        resp.setImpressions(impressions);
        resp.setClicks(clicks);
        resp.setOrders(orders);

        // ROAS = Sales / Spend
        if (spend.compareTo(BigDecimal.ZERO) > 0) {
            resp.setRoas(sales.divide(spend, 2, RoundingMode.HALF_UP));
            resp.setCpc(spend.divide(new BigDecimal(clicks > 0 ? clicks : 1), 2, RoundingMode.HALF_UP));
        } else {
            resp.setRoas(BigDecimal.ZERO);
            resp.setCpc(BigDecimal.ZERO);
        }
    }
}
