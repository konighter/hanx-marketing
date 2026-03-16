package com.hzltd.module.erplus.adv.service.mas;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.spi.memory.MasSessionMemory;
import com.hzltd.module.erplus.ai.mas.task.MasTaskService;
import com.hzltd.module.erplus.ai.mas.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.task.enums.MasTaskTypeEnum;
import java.time.LocalDateTime;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to handle the strategic planning of a high-level UserGoal.
 * Moved to adv module to support direct skill definition integration.
 */
@Slf4j
@Service
public class MasStrategicPlannerService {

    @Resource
    private MasTaskService taskService;

    @Resource
    private MasMemoryManager memoryManager;


    /**
     * Creates a business task based on a predefined skill definition.
     * Uses deterministic strategy defined in MasSkillDefDO.
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createTaskFromSkill(String sessionId, MasSkillDefDO skill, String inputData) {
        log.info("[MasStrategicPlanner] Creating task from skill: {} for sessionId: {}", skill.getSkillCode(), sessionId);

        // 1. Construct the root task with the structured strategy instruction from the skill
        // Initialize root task as PENDING with a random execution time within 1 hour
        LocalDateTime nextExecuteAt = LocalDateTime.now().plusMinutes(new java.util.Random().nextInt(60));
        
        MasTaskDO rootTask = MasTaskDO.builder()
                .sessionId(sessionId)
                .name(skill.getName())
                .taskType(MasTaskTypeEnum.SEQUENTIAL.getType()) 
                .status(MasTaskStatusEnum.PENDING.getStatus())
                .nextExecuteTime(nextExecuteAt)
                .inputData(inputData)
                .strategyInstruction(skill.getStrategyInstruction())
                .build();

        taskService.saveTask(rootTask);

        // 2. Build sub-tasks (phases) from the structured strategy
        try {
            StrategyInstructionVO strategy = JsonUtils.parseObject(skill.getStrategyInstruction(), StrategyInstructionVO.class);
            if (strategy != null && strategy.getStrategy() != null) {
                List<StrategyInstructionVO.PhaseDefinition> phases = strategy.getStrategy();
                for (int i = 0; i < phases.size(); i++) {
                    StrategyInstructionVO.PhaseDefinition phase = phases.get(i);
                    // All children start as WAITING. Root task Job will activate the first one.
                    String initialStatus = MasTaskStatusEnum.WAITING.getStatus();

                    MasTaskDO phaseTask = MasTaskDO.builder()
                            .sessionId(sessionId)
                            .parentId(rootTask.getId())
                            .name(phase.getName())
                            .taskType(MasTaskTypeEnum.LEAF.getType()) // Unit of execution
                            .status(initialStatus)
                            .inputData(inputData) // Initial input propagation
                            .executionOrder(phase.getOrder() != null ? phase.getOrder() : i + 1)
                            .strategyInstruction(JsonUtils.toJsonString(phase))
                            .build();
                    taskService.saveTask(phaseTask);
                }
            }
        } catch (Exception e) {
            log.error("[MasStrategicPlanner] Failed to parse strategy for skill: {}", skill.getSkillCode(), e);
        }

        // 3. Initialize session memory with the strategy context
        MasSessionMemory sessionMemory = memoryManager.getSessionMemory(sessionId);
        sessionMemory.put("SKILL_CODE", skill.getSkillCode());
        sessionMemory.put("STRATEGY_INSTRUCTION", skill.getStrategyInstruction());
        sessionMemory.put("CURRENT_PHASE_INDEX", 0);
        memoryManager.saveToDb(sessionId);

        log.info("[MasStrategicPlanner] Task {} created with phases for skill {}", rootTask.getId(), skill.getSkillCode());
            return rootTask.getId();
    }
}
