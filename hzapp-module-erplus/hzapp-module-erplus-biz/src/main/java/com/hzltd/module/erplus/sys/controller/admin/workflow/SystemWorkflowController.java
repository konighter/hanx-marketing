package com.hzltd.module.erplus.sys.controller.admin.workflow;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.workflow.vo.WorkflowProcessRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 流程管理")
@RestController
@RequestMapping("/erplus/sys/workflow")
@Validated
@Slf4j
public class SystemWorkflowController {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ManagementService managementService;

    @Resource
    private RuntimeService runtimeService;

    @GetMapping("/page")
    @Operation(summary = "获得流程定义分页")
    public CommonResult<PageResult<WorkflowProcessRespVO>> getProcessPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "key", required = false) String key) {
        
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery().latestVersion();
        
        if (StrUtil.isNotBlank(name)) {
            query.processDefinitionNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKeyLike("%" + key + "%");
        }
        
        long total = query.count();
        List<ProcessDefinition> list = query.listPage((pageNo - 1) * pageSize, pageSize);
        
        List<WorkflowProcessRespVO> resultList = new ArrayList<>();
        for (ProcessDefinition pd : list) {
            WorkflowProcessRespVO vo = new WorkflowProcessRespVO();
            vo.setId(pd.getId());
            vo.setName(pd.getName());
            vo.setKey(pd.getKey());
            vo.setVersion(pd.getVersion());
            vo.setDeploymentId(pd.getDeploymentId());
            vo.setResourceName(pd.getResourceName());
            vo.setSuspensionState(pd.isSuspended() ? 2 : 1);
            resultList.add(vo);
        }
        
        return success(new PageResult<>(resultList, total));
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署流程")
    public CommonResult<Boolean> deploy(@RequestParam("file") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream(filename, file.getInputStream())
                .name(filename)
                .deploy();
        log.info("部署流程成功，部署ID: {}", deployment.getId());
        return success(true);
    }

    @PutMapping("/suspend")
    @Operation(summary = "暂停流程定义")
    public CommonResult<Boolean> suspend(@RequestParam("id") String id) {
        repositoryService.suspendProcessDefinitionById(id, true, null);
        return success(true);
    }

    @PutMapping("/activate")
    @Operation(summary = "恢复流程定义")
    public CommonResult<Boolean> activate(@RequestParam("id") String id) {
        repositoryService.activateProcessDefinitionById(id, true, null);
        return success(true);
    }

    @GetMapping("/load")
    @Operation(summary = "获得流程XML配置内容")
    public CommonResult<String> load(@RequestParam("id") String id) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(id)
                .singleResult();
        if (processDefinition == null) {
            return CommonResult.error(404, "流程定义不存在");
        }
        InputStream inputStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), processDefinition.getResourceName());
        String xml = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        return success(xml);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除部署的流程")
    public CommonResult<Boolean> delete(@RequestParam("deploymentId") String deploymentId) {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();
        
        for (ProcessDefinition pd : processDefinitions) {
            String processDefId = pd.getId();
            
            List<Job> deadLetterJobs = managementService.createDeadLetterJobQuery().processDefinitionId(processDefId).list();
            for (Job job : deadLetterJobs) {
                managementService.deleteDeadLetterJob(job.getId());
            }

            List<Job> timerJobs = managementService.createTimerJobQuery().processDefinitionId(processDefId).list();
            for (Job job : timerJobs) {
                managementService.deleteTimerJob(job.getId());
            }

            List<Job> jobs = managementService.createJobQuery().processDefinitionId(processDefId).list();
            for (Job job : jobs) {
                managementService.deleteJob(job.getId());
            }

            List<Job> suspendedJobs = managementService.createSuspendedJobQuery().processDefinitionId(processDefId).list();
            for (Job job : suspendedJobs) {
                managementService.deleteSuspendedJob(job.getId());
            }
        }

        repositoryService.deleteDeployment(deploymentId, true);
        return success(true);
    }

    // ========== 手动触发 & 运行实例管理 ==========

    @PostMapping("/start")
    @Operation(summary = "手动触发流程执行")
    public CommonResult<Map<String, Object>> startProcess(
            @RequestParam("processKey") String processKey,
            @RequestBody(required = false) Map<String, Object> variables) {

        log.info("[Workflow] 手动触发流程: key={}, variables={}", processKey, variables);

        Map<String, Object> vars = variables != null ? variables : new HashMap<>();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, vars);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("processInstanceId", processInstance.getId());
        result.put("processDefinitionId", processInstance.getProcessDefinitionId());
        result.put("processKey", processKey);
        result.put("businessKey", processInstance.getBusinessKey());
        result.put("ended", processInstance.isEnded());

        log.info("[Workflow] 流程已启动: instanceId={}", processInstance.getId());
        return success(result);
    }

    @GetMapping("/instances")
    @Operation(summary = "查询运行中的流程实例")
    public CommonResult<List<Map<String, Object>>> listRunningInstances(
            @RequestParam(value = "processKey", required = false) String processKey) {

        var query = runtimeService.createProcessInstanceQuery().active();
        if (StrUtil.isNotBlank(processKey)) {
            query.processDefinitionKey(processKey);
        }

        List<ProcessInstance> instances = query.orderByProcessInstanceId().desc().list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProcessInstance pi : instances) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("processInstanceId", pi.getId());
            item.put("processDefinitionId", pi.getProcessDefinitionId());
            item.put("processDefinitionKey", pi.getProcessDefinitionKey());
            item.put("businessKey", pi.getBusinessKey());
            item.put("startTime", pi.getStartTime());
            item.put("suspended", pi.isSuspended());
            result.add(item);
        }

        return success(result);
    }

    @GetMapping("/instance/variables")
    @Operation(summary = "获取流程实例的运行时变量")
    public CommonResult<Map<String, Object>> getInstanceVariables(
            @RequestParam("processInstanceId") String processInstanceId) {
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        return success(variables);
    }

    @DeleteMapping("/instance")
    @Operation(summary = "强制终止流程实例")
    public CommonResult<Boolean> deleteInstance(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam(value = "reason", defaultValue = "手动终止") String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        log.info("[Workflow] 流程实例已终止: instanceId={}, reason={}", processInstanceId, reason);
        return success(true);
    }

    @GetMapping("/instance/timers")
    @Operation(summary = "查询流程实例的等待中定时任务")
    public CommonResult<List<Map<String, Object>>> listTimerJobs(
            @RequestParam("processInstanceId") String processInstanceId) {
        List<Job> timerJobs = managementService.createTimerJobQuery()
                .processInstanceId(processInstanceId)
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Job job : timerJobs) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("jobId", job.getId());
            item.put("dueDate", job.getDuedate());
            item.put("elementId", job.getElementId());
            item.put("elementName", job.getElementName());
            item.put("retries", job.getRetries());
            item.put("jobType", job.getJobType());
            result.add(item);
        }

        return success(result);
    }

    @PostMapping("/instance/trigger-timer")
    @Operation(summary = "手动触发定时任务（跳过等待时间立即执行）")
    public CommonResult<Boolean> triggerTimer(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam(value = "jobId", required = false) String jobId) {

        if (StrUtil.isNotBlank(jobId)) {
            // 指定 jobId: 直接触发
            managementService.moveTimerToExecutableJob(jobId);
            managementService.executeJob(jobId);
            log.info("[Workflow] 手动触发定时任务: jobId={}", jobId);
        } else {
            // 未指定 jobId: 触发该实例的所有等待中的 TimerJob
            List<Job> timerJobs = managementService.createTimerJobQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            if (timerJobs.isEmpty()) {
                return CommonResult.error(404, "该流程实例没有等待中的定时任务");
            }
            for (Job job : timerJobs) {
                managementService.moveTimerToExecutableJob(job.getId());
                managementService.executeJob(job.getId());
                log.info("[Workflow] 手动触发定时任务: jobId={}, elementId={}",
                        job.getId(), job.getElementId());
            }
        }

        return success(true);
    }
}
