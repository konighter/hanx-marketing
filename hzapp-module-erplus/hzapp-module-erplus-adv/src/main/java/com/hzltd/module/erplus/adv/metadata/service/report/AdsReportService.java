package com.hzltd.module.erplus.adv.metadata.service.report;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsPerformanceRespVO;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportTrendRespVO;
import java.util.List;

/**
 * 广告性能报表 Service
 */
public interface AdsReportService {

    /**
     * 保存小时级原始报表数据
     * @param reports 报表列表
     */
    void saveHourlyReports(List<AdsReportHourlyDO> reports);

    /**
     * 将小时级数据聚合到汇总表 (日/周/月)
     * @param accountId 账号ID
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param reportHour 报表小时
     */
    void aggregateHourlyToSummary(Long accountId, String entityType, Long entityId, java.time.LocalDateTime reportHour);

    /**
     * 获取性能核心指标卡片数据
     * @param reqVO 请求
     * @return 核心指标及环比
     */
    AdsPerformanceRespVO getPerformanceScorecard(AdsPerformanceReqVO reqVO);

    /**
     * 获取趋势图表数据
     * @param reqVO 请求
     * @return 趋势序列
     */
    AdsReportTrendRespVO getPerformanceTrend(AdsPerformanceReqVO reqVO);

    /**
     * 获取下钻层级数据 (Campaign -> AdGroup -> Ad)
     * @param reqVO 请求
     * @return 层级结构数据列表
     */
    List<java.util.Map<String, Object>> getPerformanceDrilldown(AdsPerformanceReqVO reqVO);
}
