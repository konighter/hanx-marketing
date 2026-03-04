package com.hzltd.module.erplus.adv.adapter.amazon.v3.model;

import lombok.Data;

/**
 * Amazon Ads Reporting API V3.0 Report Response
 */
@Data
public class AmzReportResponse {
    private String reportId;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String url;
    private Integer fileSize;
    private String errorDetails;
}
