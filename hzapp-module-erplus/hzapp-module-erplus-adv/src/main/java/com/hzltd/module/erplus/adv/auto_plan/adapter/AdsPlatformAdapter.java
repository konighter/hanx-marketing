package com.hzltd.module.erplus.adv.auto_plan.adapter;

import com.hzltd.module.erplus.adv.auto_plan.domain.AdsAction;

import java.util.Map;

/**
 * 广告平台自动化适配器接口
 */
public interface AdsPlatformAdapter {

    /**
     * 执行自动化动作
     *
     * @param action  抽象动作定义
     * @param context 运行时上下文
     * @return 执行结果描述
     */
    String execute(AdsAction action, Map<String, Object> context);

}
