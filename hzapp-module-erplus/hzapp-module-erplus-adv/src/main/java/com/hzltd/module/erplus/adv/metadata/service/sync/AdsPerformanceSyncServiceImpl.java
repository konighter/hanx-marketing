package com.hzltd.module.erplus.adv.metadata.service.sync;

import com.hzltd.module.system.enums.AdsPlatformEnum;
import com.hzltd.module.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsSyncTaskMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 广告绩效数据同步 Service 实现类
 *
 * 职责：
 * 1. 创建 REPORT_DAILY 类型的同步任务（PENDING 状态）
 * 2. 调度处理所有 REPORT_DAILY 类型的活跃任务
 * 3. 委托给平台适配器执行具体逻辑
 *
 * 不包含任何平台特定逻辑
 */
@Slf4j
@Service
public class AdsPerformanceSyncServiceImpl implements AdsPerformanceSyncService {

    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsSyncTaskMapper adsSyncTaskMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;

    @Override
    public void createDailySyncTasks() {
        log.info("[createDailySyncTasks] 开始为所有已启用账号创建每日绩效同步任务...");
        List<AdsAccountDO> accounts = adsAccountMapper.selectList();
        for (AdsAccountDO account : accounts) {
            try {
                createDailySyncTask(account.getId());
            } catch (Exception e) {
                log.error("[createDailySyncTasks] 账号 {} 创建任务失败", account.getId(), e);
            }
        }
        log.info("[createDailySyncTasks] 任务创建完成，共处理 {} 个账号", accounts.size());
    }

    @Override
    public void createDailySyncTask(Long accountId) {
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        if (account == null) {
            log.error("[createDailySyncTask] 账号 {} 不存在", accountId);
            return;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 检查是否已存在当天同类型的未完成任务，避免重复创建
        List<AdsSyncTaskDO> existing = adsSyncTaskMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsSyncTaskDO>()
                .eq(AdsSyncTaskDO::getAccountId, accountId)
                .eq(AdsSyncTaskDO::getTaskType, AdsSyncTaskTypeEnum.REPORT_DAILY.getCode())
                .eq(AdsSyncTaskDO::getDateRangeStart, yesterday)
                .in(AdsSyncTaskDO::getStatus, Arrays.asList("PENDING", "RUNNING"))
        );
        if (!existing.isEmpty()) {
            log.info("[createDailySyncTask] 账号 {} 在 {} 已有活跃 REPORT_DAILY 任务，跳过", accountId, yesterday);
            return;
        }

        AdsSyncTaskDO task = AdsSyncTaskDO.builder()
                .accountId(accountId)
                .platform(account.getPlatform())
                .taskType(AdsSyncTaskTypeEnum.REPORT_DAILY.getCode())
                .status("PENDING")
                .dateRangeStart(yesterday)
                .dateRangeEnd(yesterday)
                .scheduledAt(System.currentTimeMillis())
                .startedAt(LocalDateTime.now())
                .retryCount(0)
                .maxRetries(3)
                .build();
        task.setCreator("system");

        adsSyncTaskMapper.insert(task);
        log.info("[createDailySyncTask] 已创建 PENDING 任务: accountId={}, taskId={}, date={}", accountId, task.getId(), yesterday);
    }

    @Override
    public void processActiveReportTasks() {
        // 查询所有 REPORT_DAILY + REPORT_DIMENSION 的活跃任务
        List<AdsSyncTaskDO> activeTasks = adsSyncTaskMapper.selectActiveReportTasks(
            Arrays.asList("PENDING", "RUNNING")
        );
        if (activeTasks.isEmpty()) {
            return;
        }

        log.debug("[processActiveReportTasks] 发现 {} 个活跃报表任务", activeTasks.size());
        for (AdsSyncTaskDO task : activeTasks) {
            try {
                dispatchTask(task);
            } catch (Exception e) {
                log.error("[processActiveReportTasks] 任务 {} 处理异常", task.getId(), e);
                task.setStatus("FAILED");
                task.setErrorMessage("Dispatch error: " + e.getMessage());
                task.setFinishedAt(LocalDateTime.now());
                adsSyncTaskMapper.updateById(task);
            }
        }
    }

    private void dispatchTask(AdsSyncTaskDO task) {
        AdsAccountDO account = adsAccountMapper.selectById(task.getAccountId());
        if (account == null) {
            log.error("[dispatchTask] 任务 {} 对应的账号不存在", task.getId());
            task.setStatus("FAILED");
            task.setErrorMessage("Account not found");
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            return;
        }

        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(AdsPlatformEnum.valueOf(account.getPlatform()));
        if (adapter == null) {
            log.error("[dispatchTask] 找不到平台 {} 的适配器", account.getPlatform());
            task.setStatus("FAILED");
            task.setErrorMessage("No adapter for platform: " + account.getPlatform());
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            return;
        }

        // 委托给适配器执行具体逻辑
        adapter.executePerformanceSyncTask(task);
    }
}
