package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 运营计划运行时状态
 */
@Data
public class AdsAutomationPlanState {

    /**
     * 上次成功拉取报表的时间
     */
    private LocalDateTime lastDataSyncAt;

    /**
     * 上次执行动作的数量
     */
    private Integer lastActionCount;

    /**
     * 累计转移关键词数量
     */
    private Integer totalPromotedKeywords = 0;

    /**
     * 临时存储的中间变量 (例如: 统计信息)
     */
    private Map<String, Object> metrics;

}
