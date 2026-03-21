package com.hzltd.module.erplus.adv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.FlowConfigVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.ai.workflow.MasWorkflowManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mas 处理 Skill Agent任务的服务
 */
@Slf4j
@Service
public class MasSkillAgenticService {

    @Resource
    private MasWorkflowManager masWorkflowManager;

    /**
     * 基于 Skill 定义创建 Flowable 工作流实例
     * 将策略指导书中的 PhaseDefinition 列表转换为 Flowable 流程变量
     */
    @Transactional(rollbackFor = Exception.class)
    public String createWorkflowFromSkill(String businessKey, String targetBizId, MasSkillDefDO skill, String inputData) {
        Map<String, Object> params = new HashMap<>();

        // 解析用户输入参数
        if (inputData != null && !inputData.isEmpty()) {
            Map<String, Object> userParams = JsonUtils.parseObject(inputData, new TypeReference<Map<String, Object>>() {});
            if (userParams != null) {
                params.putAll(userParams);
            }
        }

        // 基础流程变量
        String sessionId = businessKey + System.currentTimeMillis();
        params.put("sessionId", sessionId);
        params.put("skillCode", skill.getSkillCode());
        params.put("targetBizId", targetBizId);

        // 解析策略指导书中的 Phase 定义，转为 Flowable 可消费的 JSON
        String phaseConfigs = buildPhaseConfigs(skill);
        params.put("phaseConfigs", phaseConfigs);

        // 遍历 FlowConfig 启动流程实例
        List<FlowConfigVO> flowConfigs = skill.getFlowConfigs();
        List<FlowConfigVO> processInstances = new ArrayList<>();

        for (FlowConfigVO flowConfig : flowConfigs) {
            // 设置监控间隔（从 FlowConfigVO 或默认值）
            String monitorInterval = flowConfig.getMonitorInterval() != null
                    ? flowConfig.getMonitorInterval() : "PT1H";
            params.put("monitorInterval", monitorInterval);

            String instanceId = masWorkflowManager.startFlowProcess(
                    flowConfig.getFlowDefineKey(), businessKey, params);
            flowConfig.setFlowInstanceId(instanceId);
            flowConfig.setSessionId(sessionId); // 保存 sessionId 到 flowConfig
            processInstances.add(flowConfig);

            log.info("[MasSkillAgentic] 启动流程, key={}, instanceId={}, businessKey={}",
                    flowConfig.getFlowDefineKey(), instanceId, businessKey);
        }

        return JsonUtils.toJsonString(processInstances);
    }

    /**
     * 从策略指导书中提取 PhaseDefinition 列表，转为 JSON 供 SkillInitDelegate 消费
     */
    private String buildPhaseConfigs(MasSkillDefDO skill) {
        if (skill.getStrategyInstruction() == null || skill.getStrategyInstruction().isEmpty()) {
            return "[]";
        }

        try {
            StrategyInstructionVO instruction = JsonUtils.parseObject(
                    skill.getStrategyInstruction(), StrategyInstructionVO.class);
            if (instruction == null || instruction.getStrategy() == null) {
                return "[]";
            }

            List<Map<String, Object>> phaseList = new ArrayList<>();
            for (StrategyInstructionVO.PhaseDefinition phase : instruction.getStrategy()) {
                Map<String, Object> phaseMap = new HashMap<>();
                phaseMap.put("name", phase.getName());
                phaseMap.put("instruction", phase.getInstruction());
                phaseMap.put("maxIterations", phase.getMaxIterations() != null ? phase.getMaxIterations() : 5);
                // 将秒数转为 ISO-8601 Duration
                phaseMap.put("interval", phase.getInterval() != null
                        ? "PT" + phase.getInterval() + "S" : "P1D");
                phaseMap.put("tools", phase.getTools() != null
                        ? JsonUtils.toJsonString(phase.getTools()) : "[]");
                phaseMap.put("order", phase.getOrder());
                phaseList.add(phaseMap);
            }

            // 按 order 排序
            phaseList.sort((a, b) -> {
                Integer orderA = (Integer) a.getOrDefault("order", 0);
                Integer orderB = (Integer) b.getOrDefault("order", 0);
                return orderA.compareTo(orderB);
            });

            return JsonUtils.toJsonString(phaseList);
        } catch (Exception e) {
            log.error("[MasSkillAgentic] 解析策略指导书失败: {}", e.getMessage(), e);
            return "[]";
        }
    }
}

