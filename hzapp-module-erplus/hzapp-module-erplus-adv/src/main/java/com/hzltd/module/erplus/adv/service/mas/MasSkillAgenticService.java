package com.hzltd.module.erplus.adv.service.mas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.FlowConfigVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;
import com.hzltd.module.erplus.ai.mas.task.MasTaskService;
import com.hzltd.module.erplus.ai.mas.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.task.enums.MasTaskTypeEnum;
import java.time.LocalDateTime;

import com.hzltd.module.erplus.ai.workflow.MasWorkflowManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Mas 处理 Skill Agent任务的服务
 *
 */
@Slf4j
@Service
public class MasSkillAgenticService {

    @Resource
    private MasTaskService taskService;

    @Resource
    private MasMemoryManager memoryManager;

    @Resource
    private MasWorkflowManager masWorkflowManager;





    /**
     * Creates a business task based on a predefined skill definition.
     * Uses deterministic strategy defined in MasSkillDefDO.
     */
    @Transactional(rollbackFor = Exception.class)
    public String createWorkflowFromSkill(String businessKey, MasSkillDefDO skill, String inputData) {
        Map<String, Object> params = JsonUtils.parseObject(inputData, new TypeReference<Map<String, Object>>() {});
        params.put("sessionId", businessKey+System.currentTimeMillis());
        List<FlowConfigVO> processInstances = Lists.newArrayList();
        skill.getFlowConfigs().forEach(flowConfig -> {

            String instanceId = masWorkflowManager.startFlowProcess(flowConfig.getFlowDefineKey(), businessKey, params);
            flowConfig.setFlowInstanceId(instanceId);
            processInstances.add(flowConfig);
        });
        // 流程节点的保存


        return JsonUtils.toJsonString(processInstances);
    }

}
