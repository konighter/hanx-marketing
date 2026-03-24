package com.hzltd.module.erplus.ai.workflow;
 
import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.HashMap;
import java.util.Map;
 
/**
 * 广告工作流管理器：负责统一 Agent 流程的部署与启动
 * 采用并行双环架构：一个流程实例同时包含监控与策略逻辑
 */
@Slf4j
@Service
public class AdWorkflowManager {
 
    @Autowired
    private RuntimeService runtimeService;
 
    @Autowired
    private RepositoryService repositoryService;
 
    /**
     * 启动广告 AI Agent (包含监控与策略双循环)
     * @param adId 广告 ID / ASIN
     * @param tenantId 租户 ID
     * @param monitorInterval 监控频率，例如 "PT10M" (10分钟)
     * @return 流程实例 ID
     */
    public String startAdAgent(String adId, String tenantId, String monitorInterval) {
        log.info("激活广告 AI Agent... ASIN: {}, 租户: {}, 监控间隔: {}", adId, tenantId, monitorInterval);
 
        Map<String, Object> variables = new HashMap<>();
        variables.put(AdWorkflowConstants.VAR_AD_ID, adId);
        variables.put(AdWorkflowConstants.VAR_TENANT_ID, tenantId);
        variables.put(AdWorkflowConstants.VAR_MONITOR_INTERVAL, monitorInterval);
        
        // 默认复盘间隔（如果策略中需要）
        variables.put(AdWorkflowConstants.VAR_REVIEW_DURATION, "P1D"); 
 
        // 启动统一的 Agent 流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                AdWorkflowConstants.PROCESS_KEY, 
                "AGENT_" + adId + "_" + System.currentTimeMillis(),
                variables
        );
 
        log.info("Agent 流程已启动，实例ID: {}, 业务Key: {}", processInstance.getId(), processInstance.getBusinessKey());
        return processInstance.getId();
    }
 
    public void deployProcess(String resourcePath) {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resourcePath)
                .name("AdAgentDeployment")
                .tenantId(TenantContextHolder.getTenantId().toString())
                .deploy();
        log.info("部署成功，ID: {}", deployment.getId());
    }
}
