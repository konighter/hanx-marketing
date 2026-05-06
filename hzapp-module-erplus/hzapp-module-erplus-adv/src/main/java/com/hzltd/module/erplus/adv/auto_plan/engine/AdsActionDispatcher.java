package com.hzltd.module.erplus.adv.auto_plan.engine;

import com.hzltd.module.erplus.adv.auto_plan.domain.AdsAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自动化动作分发器
 */
@Slf4j
@Component
public class AdsActionDispatcher {

    // TODO: Map of adapters for different platforms
    // @Resource
    // private Map<String, AdsPlatformAdapter> adapters;

    /**
     * 分发并执行动作
     *
     * @param platform 平台标识 (AMAZON/META)
     * @param action   动作定义
     * @param context  上下文
     */
    public void dispatch(String platform, AdsAction action, Map<String, Object> context) {
        log.info("Dispatching action: [platform={}, type={}]", platform, action.getType());
        
        // Mock execution for now
        log.info("Executing mock action for platform {}: {}", platform, action);
    }
}
