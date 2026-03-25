package com.hzltd.module.erplus.ai.workflow;
 
/**
 * 广告工作流常量定义：统一管理流程 ID、变量名与信号名
 */
public class AdWorkflowConstants {
 
    /**
     * 流程定义 Key
     */
    public static final String PROCESS_KEY = "adAgentProcess";
 
    /**
     * 变量：广告 ID / ASIN
     */
    public static final String VAR_AD_ID = "adId";
 
    /**
     * 变量：租户 ID
     */
    public static final String VAR_TENANT_ID = "tenantId";
 
    /**
     * 变量：监控时间间隔 (ISO-8601 Duration, e.g., PT10M)
     */
    public static final String VAR_MONITOR_INTERVAL = "monitorInterval";
 
    /**
     * 变量：监控是否检测到异常触发报警
     */
    public static final String VAR_ALARM_TRIGGERED = "alarmTriggered";
 
    /**
     * 变量：报警上下文数据 (Map)
     */
    public static final String VAR_ALARM_DATA = "alarmData";
 
    /**
     * 变量：是否需要人工确认
     */
    public static final String VAR_REQUIRES_CONFIRM = "requiresConfirm";
 
    /**
     * 变量：复盘分析回溯时间
     */
    public static final String VAR_REVIEW_DURATION = "reviewDuration";
 
    /**
     * 信号：内部异常报警信号 (作用域：processInstance)
     */
    public static final String SIGNAL_ALARM = "internalAlarmSignal";
}
