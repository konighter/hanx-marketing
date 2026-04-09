package com.hzltd.module.erplus.job.productpub;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import com.hzltd.module.erplus.service.productpub.ErpProductPublishTaskService;
import com.hzltd.module.erplus.service.productpub.ProductPublishService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刊登发布任务调度 Job
 * 负责扫描并提交达到预约时间的发布任务
 *
 * @author Antigravity
 */
@Slf4j
@Component
public class ProductPublishJob implements JobHandler {

    @Resource
    private ProductPublishService productPublishService;

    @Resource
    private ErpProductPublishTaskService productPublishTaskService;

    @Override
    public String execute(String param) throws Exception {
        log.info("[ProductPublishJob] 开始扫描预约刊登任务...");
        
        // 查找所有状态为 INIT (1) 且预约时间小于等于当前时间的任务
        List<ErpProductPublishTaskDO> tasks = productPublishTaskService.getPendingPublishTasks();
        
        if (tasks.isEmpty()) {
            return "No pending tasks found";
        }
        
        int successCount = 0;
        for (ErpProductPublishTaskDO task : tasks) {
            try {
                log.info("[ProductPublishJob] 提交任务: taskId={}", task.getId());
                boolean submitted = productPublishTaskService.submitProductPublishTask(task.getId());
                if (submitted) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("[ProductPublishJob] 提交任务失败: taskId={}", task.getId(), e);
            }
        }
        
        String result = String.format("Successfully submitted %d/%d tasks", successCount, tasks.size());
        log.info("[ProductPublishJob] 任务调度结束: {}", result);
        return result;
    }
}
