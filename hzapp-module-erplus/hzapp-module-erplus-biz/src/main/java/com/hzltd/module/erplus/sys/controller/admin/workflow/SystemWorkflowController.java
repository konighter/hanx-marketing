package com.hzltd.module.erplus.sys.controller.admin.workflow;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.workflow.vo.WorkflowProcessRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.job.api.Job;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        // 先获取该部署关联的所有流程定义
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .list();
        
        // 针对每个流程定义清理与之绑定的特定任务（如 TimerStartEvent 产生的各类任务），避免外键约束失败
        for (ProcessDefinition pd : processDefinitions) {
            String processDefId = pd.getId();
            
            // 清理对应的死信队列任务 (DeadLetterJob)
            List<Job> deadLetterJobs = managementService.createDeadLetterJobQuery().processDefinitionId(processDefId).list();
            for (Job job : deadLetterJobs) {
                managementService.deleteDeadLetterJob(job.getId());
            }

            // 清理对应的定时任务 (TimerJob)
            List<Job> timerJobs = managementService.createTimerJobQuery().processDefinitionId(processDefId).list();
            for (Job job : timerJobs) {
                managementService.deleteTimerJob(job.getId());
            }

            // 清理对应的普通任务 (Job)
            List<Job> jobs = managementService.createJobQuery().processDefinitionId(processDefId).list();
            for (Job job : jobs) {
                managementService.deleteJob(job.getId());
            }

            // 清理对应的挂起任务 (SuspendedJob)
            List<Job> suspendedJobs = managementService.createSuspendedJobQuery().processDefinitionId(processDefId).list();
            for (Job job : suspendedJobs) {
                managementService.deleteSuspendedJob(job.getId());
            }
        }

        // 级联删除流程部署，包括实例和历史记录
        repositoryService.deleteDeployment(deploymentId, true);
        return success(true);
    }


}
