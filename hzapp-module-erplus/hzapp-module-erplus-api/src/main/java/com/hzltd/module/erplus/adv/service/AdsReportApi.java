package com.hzltd.module.erplus.adv.service;

import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.adv.enums.AdsReportStatus;

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
     * @param request 包含 shopId, startDate, endDate 等
     * @return 报表请求配置列表
     */
    AdsResponse<List<AdsReportRequest>> getReportRequests(AdsRequest<AdsReportGetRequest> request);

    /**
     * 提交异步报表生成请求
     *
     * @param request 包含报表请求配置
     * @return 平台侧的任务唯一标识 (platformJobId)
     */
    AdsResponse<String> submitAsyncReport(AdsRequest<AdsReportRequest> request);

    /**
     * 查询异步报表生成状态
     *
     * @param request 包含 platformJobId 和报表请求上下文
     * @return 当前状态
     */
    AdsResponse<AdsReportStatus> getReportStatus(AdsRequest<AdsReportStatusRequest> request);

    /**
     * 下载并解析报表，解析后的结果通过 saver 回调处理（通常是入库 Doris）
     *
     * @param request 包含 platformJobId 和报表请求上下文
     * @param saver 数据保存回调
     */
    AdsResponse<Void> downloadAndProcess(AdsRequest<AdsReportProcessRequest> request, Consumer<List<?>> saver);

}
