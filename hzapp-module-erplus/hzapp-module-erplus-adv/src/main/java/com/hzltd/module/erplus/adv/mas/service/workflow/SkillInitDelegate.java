package com.hzltd.module.erplus.adv.mas.service.workflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Skill 流程初始化委托
 * 在 skill-seq-tasks 主流程启动时，将 phaseConfigs JSON 展开为独立的流程变量
 * 供后续 CallActivity (skill-task-loop) 通过 flowable:in 映射读取
 */
@Slf4j
@Service("skillInitDelegate")
public class SkillInitDelegate {

    /**
     * 初始化流程变量
     * 将 phaseConfigs JSON 数组展开为 phase_{N}_name, phase_{N}_instruction 等变量
     *
     * @param execution Flowable 执行上下文
     */
    public void init(DelegateExecution execution) {

        // 恢复租户上下文 (Timer/Async Job 回调时线程没有租户信息)
        Object tenantIdObj = execution.getVariable("tenantId");
        if (tenantIdObj != null) {
            TenantContextHolder.setTenantId(Long.valueOf(tenantIdObj.toString()));
        }

        String skillCode = (String) execution.getVariable("skillCode");
        String targetBizId = (String) execution.getVariable("targetBizId");
        String phaseConfigsJson = (String) execution.getVariable("phaseConfigs");

        // 生成共享 Session ID: tenantId:skillCode:targetBizId
        String sessionId = String.format("%s:%s:%s",
                tenantIdObj != null ? tenantIdObj : "0", skillCode, targetBizId);
        execution.setVariable("sessionId", sessionId);

        log.info("[SkillInit] 初始化 Skill 流程变量, skillCode={}, targetBizId={}", skillCode, targetBizId);

        if (phaseConfigsJson == null || phaseConfigsJson.isEmpty()) {
            log.warn("[SkillInit] phaseConfigs 为空, 设置 totalPhases=0");
            execution.setVariable("totalPhases", 0);
            return;
        }

        List<Map<String, Object>> phases = JsonUtils.parseObject(
                phaseConfigsJson, new TypeReference<List<Map<String, Object>>>() {});

        if (phases == null || phases.isEmpty()) {
            execution.setVariable("totalPhases", 0);
            return;
        }

        int total = Math.min(phases.size(), 5); // 最多支持 5 个 Phase
        execution.setVariable("totalPhases", total);

        for (int i = 0; i < total; i++) {
            Map<String, Object> phase = phases.get(i);
            int idx = i + 1; // Phase 编号从 1 开始

            execution.setVariable("phase_" + idx + "_name",
                    getOrDefault(phase, "name", "Phase-" + idx));
            execution.setVariable("phase_" + idx + "_instruction",
                    getOrDefault(phase, "instruction", ""));
            execution.setVariable("phase_" + idx + "_maxIterations",
                    getIntOrDefault(phase, "maxIterations", 5));
            execution.setVariable("phase_" + idx + "_interval",
                    getOrDefault(phase, "interval", "P1D"));
            execution.setVariable("phase_" + idx + "_tools",
                    getOrDefault(phase, "tools", "[]"));
            execution.setVariable("phase_" + idx + "_index", idx);

            log.info("[SkillInit] Phase-{}: name={}, maxIterations={}, interval={}",
                    idx,
                    phase.get("name"),
                    getIntOrDefault(phase, "maxIterations", 5),
                    getOrDefault(phase, "interval", "P1D"));
        }

        // 初始化报警相关变量
        execution.setVariable("alarmTriggered", false);
        execution.setVariable("alarmAttribution", null);
        execution.setVariable("suggestedAction", null);
    }

    private String getOrDefault(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private int getIntOrDefault(Map<String, Object> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
