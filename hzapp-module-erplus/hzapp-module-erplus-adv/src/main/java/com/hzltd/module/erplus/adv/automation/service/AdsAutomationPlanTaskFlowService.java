package com.hzltd.module.erplus.adv.automation.service;

/**
 * 广告自动化任务流执行服务
 * 负责运营计划的具体执行逻辑：广告结构初始化、报表拉取、分类决策与动作分发
 *
 * @author antigravity
 */
public interface AdsAutomationPlanTaskFlowService {

    /**
     * 初始化广告结构 (onStart)
     * 根据计划配置，创建必要的手动广告活动与关联
     *
     * @param planId 运营计划ID
     */
    void initAdStructure(Long planId);

    /**
     * 执行自动化循环 (onPoll)
     * 拉取报表、评估规则并执行对应动作
     *
     * @param planId 运营计划ID
     */
    void executeAutomationCycle(Long planId);

}
