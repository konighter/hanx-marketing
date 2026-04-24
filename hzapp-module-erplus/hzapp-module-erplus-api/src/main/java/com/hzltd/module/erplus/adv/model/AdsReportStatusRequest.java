package com.hzltd.module.erplus.adv.model;

import lombok.Data;

/**
 * 查询报表状态的请求参数
 */
@Data
public class AdsReportStatusRequest {
    /** 平台侧的任务唯一标识 (platformJobId) */
    private String platformJobId;
    /** 报表请求上下文 */
    private AdsReportRequest reportRequest;
}
