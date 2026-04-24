package com.hzltd.module.erplus.adv.enums;

/**
 * 广告报表平台侧生成状态
 */
public enum AdsReportStatus {
    
    /** 待处理/处理中 */
    PROCESSING,
    
    /** 已完成 */
    COMPLETED,
    
    /** 失败 */
    FAILED,
    
    /** 未发现（可能 Job ID 已过期或不存在） */
    NOT_FOUND
    
}
