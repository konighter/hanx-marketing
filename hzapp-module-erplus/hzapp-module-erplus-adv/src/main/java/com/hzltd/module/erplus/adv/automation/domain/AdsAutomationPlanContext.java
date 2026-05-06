package com.hzltd.module.erplus.adv.automation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运营计划 Context 包装类 (用于 JSON 存储)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAutomationPlanContext {

    /**
     * 静态配置
     */
    private AdsAutomationPlanConfig config;

    /**
     * 运行时状态
     */
    private AdsAutomationPlanState state;

}
