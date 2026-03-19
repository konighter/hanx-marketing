package com.hzltd.module.erplus.ai.workflow;

import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 广告工作流管理器：负责流程部署与启动
 */
@Slf4j
@Service
public class MasWorkflowManager {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 手动部署流程定义（如果 Spring Boot 自动扫描未覆盖到该路径）
     * @param resourcePath 资源路径，例如 "Agent-loop.bpmn20.xml"
     */
    public void deployProcess(String resourcePath) {
        log.info("正在部署 BPMN 流程: {}", resourcePath);
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resourcePath)
                .name("AdMonitoringDeployment")
                .tenantId(TenantContextHolder.getTenantId().toString())
                .deploy();
        log.info("部署成功，ID: {}, 名称: {}", deployment.getId(), deployment.getName());
    }


    public String startFlowProcess(String processDefineKey, String businessKey, Map<String, Object> params) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKeyAndTenantId(
                processDefineKey, businessKey, params, TenantContextHolder.getTenantId().toString());
        return instance.getProcessInstanceId();
    }

    public Boolean pauseFlowProcess(String processDefineKey, String businessKey) {

       List<ProcessInstance> instances = getFlowProcess(processDefineKey, businessKey);

        instances.forEach(instance -> {
            runtimeService.suspendProcessInstanceById(instance.getProcessInstanceId());
        });
        return true;
    }

    public Boolean activeFlowProcess(String processDefineKey, String businessKey) {
        List<ProcessInstance> instances = getFlowProcess(processDefineKey, businessKey);

        instances.forEach(instance -> {
            runtimeService.activateProcessInstanceById(instance.getProcessInstanceId());
        });
        return true;
    }

    public Boolean deleteFlowProcess(String processDefineKey, String businessKey, String reason) {
        List<ProcessInstance> instances = getFlowProcess(processDefineKey, businessKey);

        runtimeService.bulkDeleteProcessInstances(instances.stream().map(ProcessInstance::getProcessInstanceId).toList(), reason);
        return true;
    }

    public List<ProcessInstance> getFlowProcess(String processDefineKey, String businessKey) {
        return runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefineKey)
                .processInstanceTenantId(TenantContextHolder.getTenantId().toString())
                .processInstanceBusinessKey(businessKey).list();
    }


}
