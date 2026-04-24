package com.hzltd.module.erplus.adv.model;

import lombok.Data;

/**
 * 下载并解析报表的请求参数
 */
@Data
public class AdsReportProcessRequest {
    /** 平台侧的任务唯一标识 (platformJobId) */
    private String platformJobId;
    /** 报表请求上下文 */
    private AdsReportRequest reportRequest;
}
