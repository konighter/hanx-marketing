package com.hzltd.module.erplus.adv.metadata.service.sync;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.adapter.service.AdsReportApiFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportBatchMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsSyncTaskMapper;
import com.hzltd.module.erplus.adv.enums.AdsReportStatus;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.adv.model.AdsReportRequest;
import com.hzltd.module.erplus.adv.service.AdsReportApi;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 广告同步任务核心服务实现类
 */
@Slf4j
@Service
public class AdsSyncTaskServiceImpl implements AdsSyncTaskService {

    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsSyncTaskMapper adsSyncTaskMapper;
    @Resource
    private AdsReportBatchMapper adsReportBatchMapper;
    @Resource
    private AdsReportApiFactory adsReportApiFactory;
    @Resource
    private SystemShopService shopService;

    /** 报表结算延时缓冲（小时） */
    private static final int REPORT_SETTLEMENT_BUFFER_HOURS = 4;

    @Override
    public void createDailyTasks() {
        log.info("[createDailyTasks] 开始扫描店铺创建每日同步任务...");
        // 查找所有启用的广告账号
       List<ShopModel> shopModels = shopService.getAvailableShops();



        for (ShopModel shop : shopModels) {
            try {
                processAccountCreation(shop);
            } catch (Exception e) {
                log.error("[createDailyTasks] 店铺 {} 创建任务失败", shop.getName(), e);
            }
        }
    }

    private void processAccountCreation(ShopModel shop) {
        String timezone = shop.getTimezone();
        if (timezone == null || timezone.isEmpty()) {
            timezone = "UTC";
        }

        // 计算账号所在时区的“昨日”
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime nowInZone = ZonedDateTime.now(zoneId);
        LocalDate yesterday = nowInZone.minusDays(1).toLocalDate();

        // 检查缓冲期：只有当昨日已结束且超过 BUFFER_HOURS 时才创建
        // 例如凌晨 4 点之后开始同步昨日数据
        if (nowInZone.getHour() < REPORT_SETTLEMENT_BUFFER_HOURS) {
            return;
        }

        // 检查任务是否已存在 (REPORT_DAILY 为父任务)
        AdsSyncTaskDO existing = adsSyncTaskMapper.selectOne(
                new LambdaQueryWrapperX<AdsSyncTaskDO>()
                        .eq(AdsSyncTaskDO::getShopId, shop.getId())
                        .eq(AdsSyncTaskDO::getTaskType, AdsSyncTaskTypeEnum.REPORT_DAILY.getCode())
                        .eq(AdsSyncTaskDO::getDateRangeStart, yesterday)
                        .last("LIMIT 1")
        );

        if (existing != null) {
            return;
        }

        // 创建昨日任务
        AdsSyncTaskDO task = AdsSyncTaskDO.builder()
                .shopId(shop.getId())
                .accountId(shop.getAccountId())
                .platform(shop.getPlatformCode())
                .taskType(AdsSyncTaskTypeEnum.REPORT_DAILY.getCode())
                .status("PENDING")
                .dateRangeStart(yesterday)
                .dateRangeEnd(yesterday)
                .retryCount(0)
                .maxRetries(3)
                .scheduledAt(System.currentTimeMillis())
                .build();
        task.setCreator("system");
        adsSyncTaskMapper.insert(task);
        
        log.info("[createDailyTasks] 为店铺 {} 创建昨日同步任务: date={}", shop.getName(), yesterday);
    }

    @Override
    public void processActiveTasks() {
        // 1. 处理 PENDING 的父任务 -> 拆分子任务
        processPendingParentTasks();

        // 2. 处理 PENDING 的子任务 -> 提交异步请求
        processPendingChildTasks();

        // 3. 处理 SUBMITTED 状态的任务 -> 轮询并下载
        processSubmittedTasks();
    }

    private void processPendingParentTasks() {
        List<AdsSyncTaskDO> pendingParents = adsSyncTaskMapper.selectListByStatusesAndTaskType(
                Collections.singletonList("PENDING"),
                AdsSyncTaskTypeEnum.REPORT_DAILY.getCode()
        );

        for (AdsSyncTaskDO parentTask : pendingParents) {
            try {
                splitParentTask(parentTask);
            } catch (Exception e) {
                log.error("[splitParentTask] 任务 {} 拆分失败", parentTask.getId(), e);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected void splitParentTask(AdsSyncTaskDO parentTask) {
        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(parentTask.getPlatform()));
        if (api == null) {
            return;
        }

        // 获取该平台需要生成的请求列表
        List<AdsReportRequest> requests = api.getReportRequests(
                parentTask.getShopId(),
                parentTask.getDateRangeStart(),
                parentTask.getDateRangeEnd()
        );

        if (requests == null || requests.isEmpty()) {
            parentTask.setStatus("SUCCESS");
            parentTask.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(parentTask);
            return;
        }

        // 创建子任务 (REPORT_DIMENSION)
        for (AdsReportRequest req : requests) {
            AdsSyncTaskDO childTask = AdsSyncTaskDO.builder()
                    .shopId(parentTask.getShopId())
                    .accountId(parentTask.getAccountId())
                    .parentTaskId(parentTask.getId())
                    .platform(parentTask.getPlatform())
                    .taskType(AdsSyncTaskTypeEnum.REPORT_DIMENSION.getCode())
                    .status("PENDING")
                    .dateRangeStart(req.getStartDate()) // 注意：此处可能包含 T-7 到 T-1 的范围
                    .dateRangeEnd(req.getEndDate())
                    .context(req.getContext())
                    .retryCount(0)
                    .maxRetries(3)
                    .build();
            childTask.setCreator("system");
            adsSyncTaskMapper.insert(childTask);
        }

        // 更新父任务状态为 RUNNING
        parentTask.setStatus("RUNNING");
        parentTask.setStartedAt(LocalDateTime.now());
        adsSyncTaskMapper.updateById(parentTask);
    }

    private void processPendingChildTasks() {
        List<AdsSyncTaskDO> pendingChildren = adsSyncTaskMapper.selectListByStatusesAndTaskType(
                Collections.singletonList("PENDING"),
                AdsSyncTaskTypeEnum.REPORT_DIMENSION.getCode()
        );

        for (AdsSyncTaskDO task : pendingChildren) {
            try {
                submitTask(task);
            } catch (Exception e) {
                handleTaskError(task, e);
            }
        }
    }

    private void submitTask(AdsSyncTaskDO task) {
        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(task.getPlatform()));
        AdsReportRequest req = buildRequest(task);

        String platformJobId = api.submitAsyncReport(req);
        if (platformJobId != null) {
            task.setPlatformJobId(platformJobId);
            task.setStatus("SUBMITTED");
            adsSyncTaskMapper.updateById(task);
            log.info("[submitTask] 任务 {} 已提交, platformJobId={}", task.getId(), platformJobId);
        }
    }

    private void processSubmittedTasks() {
        List<AdsSyncTaskDO> submittedTasks = adsSyncTaskMapper.selectListByStatus("SUBMITTED");

        for (AdsSyncTaskDO task : submittedTasks) {
            try {
                pollTask(task);
            } catch (Exception e) {
                handleTaskError(task, e);
            }
        }
    }

    private void pollTask(AdsSyncTaskDO task) {
        AdsReportApi api = adsReportApiFactory.getAdsApiService(AdsPlatformEnum.valueOf(task.getPlatform()));
        AdsReportRequest req = buildRequest(task);

        AdsReportStatus status = api.getReportStatus(task.getPlatformJobId(), req);
        if (status == AdsReportStatus.COMPLETED) {
            log.info("[pollTask] 任务 {} 已在平台生成完成，开始下载...", task.getId());
            api.downloadAndProcess(task.getPlatformJobId(), req, data -> {
                // 填充通用字段并入库 Doris
//                saveToDoris(task, data);
            });
            
            task.setStatus("SUCCESS");
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            
            checkParentCompletion(task.getParentTaskId());
        } else if (status == AdsReportStatus.FAILED) {
            throw new RuntimeException("Platform report generation failed");
        }
    }

    private AdsReportRequest buildRequest(AdsSyncTaskDO task) {
        return AdsReportRequest.builder()
                .shopId(task.getShopId())
                .platform(task.getPlatform())
                .startDate(task.getDateRangeStart())
                .endDate(task.getDateRangeEnd())
                .context(task.getContext())
                .build();
    }

    private void saveToDoris(AdsSyncTaskDO task, List<AdsReportBatchDO> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        for (AdsReportBatchDO item : dataList) {
            item.setShopId(task.getShopId());
            item.setPlatform(task.getPlatform());
            // reportDate 由解析端由于可能跨 7 天，理论上由 JSON 数据自带。
            // 但如果解析端没带，可以根据任务兜底，不过 7 天任务建议解析端带。
            adsReportBatchMapper.insert(item);
        }
    }

    private void handleTaskError(AdsSyncTaskDO task, Exception e) {
        log.error("[handleTaskError] 任务 {} 执行异常", task.getId(), e);
        task.setRetryCount(task.getRetryCount() + 1);
        if (task.getRetryCount() >= task.getMaxRetries()) {
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
        } else {
            // 稍后重试
            task.setScheduledAt(System.currentTimeMillis() + 60000); // 1分钟后
        }
        adsSyncTaskMapper.updateById(task);
    }

    private void checkParentCompletion(Long parentId) {
        if (parentId == null) return;
        
        Long count = adsSyncTaskMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsSyncTaskDO>()
                        .eq(AdsSyncTaskDO::getParentTaskId, parentId)
                        .ne(AdsSyncTaskDO::getStatus, "SUCCESS")
        );
        
        if (count == 0) {
            AdsSyncTaskDO parent = adsSyncTaskMapper.selectById(parentId);
            if (parent != null) {
                parent.setStatus("SUCCESS");
                parent.setFinishedAt(LocalDateTime.now());
                adsSyncTaskMapper.updateById(parent);
            }
        }
    }
}
