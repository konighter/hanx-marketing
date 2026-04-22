package com.hzltd.module.erplus.adv.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import com.hzltd.module.erplus.adv.model.AdsReportRequest;
import com.hzltd.module.erplus.adv.enums.AdsReportStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * 广告报表 API 接口 (v2: 任务无关)
 *
 * 定义跨平台的离线报表同步能力，由具体的平台（如 Amazon）实现。
 * 任务的状态管理和串联由 adv 模块的同步引擎负责。
 */
public interface AdsReportApi {

    /**
     * 获取指定范围内该平台需要执行的报表同步请求
     * 例如 Amazon 可能需要按 SP_ADS, SP_TARGETING 等维度分别创建请求
     *
     * @param shopId 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 报表请求配置列表
     */
    List<AdsReportRequest> getReportRequests(Integer shopId, LocalDate startDate, LocalDate endDate);

    /**
     * 提交异步报表生成请求
     *
     * @param request 报表请求
     * @return 平台侧的任务唯一标识 (platformJobId)
     */
    String submitAsyncReport(AdsReportRequest request);

    /**
     * 查询异步报表生成状态
     *
     * @param platformJobId 平台侧任务标识
     * @param request 报表请求上下文
     * @return 当前状态
     */
    AdsReportStatus getReportStatus(String platformJobId, AdsReportRequest request);

    /**
     * 下载并解析报表，解析后的结果通过 saver 回调处理（通常是入库 Doris）
     *
     * @param platformJobId 平台侧任务标识
     * @param request 报表请求上下文
     * @param saver 数据保存回调
     */
    void downloadAndProcess(String platformJobId, AdsReportRequest request, Consumer<List<AdsReportBatchDO>> saver);

}
