package com.hzltd.module.erplus.adv.metadata.service.report;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportTrendRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportQueryReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportDataRespVO;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告性能报表 Service
 */
public interface AdsReportService {

    /**
     * 保存小时性能报表数据
     * @param reports 报表数据列表
     */
    void saveHourlyReports(List<AdsReportHourlyDO> reports);

    /**
     * 聚合小时数据到汇总表
     */
    void aggregateHourlyToSummary(Long shopId, String entityType, Long entityId, LocalDateTime reportHour);

    /**
     * 获得核心指标响应
     */
    AdsPerformanceRespVO getPerformanceScorecard(AdsPerformanceReqVO reqVO);

    /**
     * 获得性能趋势图
     */
    AdsReportTrendRespVO getPerformanceTrend(AdsPerformanceReqVO reqVO);

    /**
     * 获得多层级下钻性能数据
     */
    List<AdsPerformanceRespVO> getPerformanceDrilldown(AdsPerformanceReqVO reqVO);

    /**
     * 【新架构】多维聚合结构化报表查询
     */
    AdsReportDataRespVO queryAdsReport(AdsReportQueryReqVO reqVO);
}
