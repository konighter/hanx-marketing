# Erplus 通用任务调度引擎设计文档 (Erplus Schedule Task Engine)

## 1. 背景与目标
为了支持系统整体（不仅限于广告，还包括库存、订单、系统维护等）的统一任务调度，构建一个通用的、基于状态机流转的任务引擎。该引擎将作为 Erplus 系统的底层基础设施。

### 核心目标：
- **全系统通用**：支持不同模块注册任务处理器。
- **解耦**：业务模块仅负责业务逻辑，引擎负责生命周期管理。
- **状态机驱动**：支持异步任务的“提交-轮询-执行-完成”全流程。
- **统一入口**：所有任务的提交和回写必须通过引擎 API。
- **健壮性**：支持超时检测、重试退避机制。

## 2. 核心状态机设计
任务引擎通过 `status` 字段驱动任务流转：

| 状态 | 说明 | 下一步动作 |
| :--- | :--- | :--- |
| **PENDING** | 初始状态或排期中 | 引擎调用 `onStart()`，通常进行任务初始化或提交远程请求。 |
| **SUBMITTED**| 已提交远程平台 | 引擎定期调用 `onPoll()`，检查远程任务是否就绪。 |
| **PROCESSING**| 本地重活处理中 | 引擎停止主动调度，等待子模块异步回调 `completeTask()`。 |
| **SUCCESS** | 终端状态：成功 | 引擎触发父任务完成检查，记录 `finishedAt`。 |
| **FAILED** | 终端状态：失败 | 记录错误信息，触发重试或停止。 |

## 3. 关键接口定义

### 3.1 引擎服务接口 (ErpTaskEngine)
由核心引擎模块实现，供各业务子模块调用。
```java
public interface ErpTaskEngine {
    /**
     * 提交新任务（业务模块调用）
     */
    Long submitTask(ErpTaskSubmitRequest request);

    /**
     * 批量提交子任务
     */
    void submitChildTasks(Long parentId, List<ErpTaskSubmitRequest> requests);

    /**
     * 异步任务回写结果（业务模块异步回调）
     * 负责将状态由 PROCESSING/SUBMITTED 转为 SUCCESS/FAILED
     */
    void completeTask(Long taskId, ErpTaskResult result);
}
```

### 3.2 任务提交请求 (ErpTaskSubmitRequest)
```java
public class ErpTaskSubmitRequest {
    private Integer shopId;        // 店铺ID (可选)
    private String platform;      // 平台标识 (如 AMAZON, ERP, SYSTEM)
    private String taskType;      // 任务类型 (如 AD_REPORT, INV_SYNC)
    private String taskUniqueId;  // 任务唯一标识（用于幂等）
    
    private LocalDate dateRangeStart; // 业务数据开始日期 (可选)
    private LocalDate dateRangeEnd;   // 业务数据结束日期 (可选)
    
    private Map<String, Object> context; // 任务上下文参数 (JSON)
    private Long scheduledAt;     // 预定执行时间 (可选)
    private Integer maxRetries;   // 最大重试次数 (可选)
}
```

### 3.3 任务处理器接口 (ErpTaskHandler)
由各业务模块实现。
```java
public interface ErpTaskHandler {
    /**
     * 任务开始执行逻辑
     */
    ErpTaskResult onStart(ErpScheduleTaskDO task);

    /**
     * 任务轮询逻辑（针对 SUBMITTED 状态）
     */
    ErpTaskResult onPoll(ErpScheduleTaskDO task);

    /**
     * 获取处理器支持的任务类型
     */
    String getTaskType();
}
```

### 3.4 执行结果契约 (ErpTaskResult)
```java
public class ErpTaskResult {
    private String status;         // 下一个目标状态
    private String platformJobId;  // 远程平台/第三方ID
    private Map<String, Object> context; // 需更新的上下文
    private String errorMessage;   // 错误信息
}
```

## 4. 核心流程 (以异步任务为例)

1. **提交**: 业务模块调用 `engine.submitTask()`。
2. **调度**: 引擎扫描 `PENDING` 任务，分发给对应的 `ErpTaskHandler.onStart()`。
3. **流转**: Handler 返回 `SUBMITTED`（异步等待）或 `PROCESSING`（本地处理中）。
4. **结案**: 
   - 轮询结案：引擎定期调 `onPoll` 直到返回 `SUCCESS`。
   - 回调结案：外部/本地异步逻辑执行完后，调用 `engine.completeTask()`。

## 5. 存储设计
- **表名**: `erplus_schedule_task` (原 `ads_sync_task`)
- **索引**: `task_unique_id` (唯一索引或业务唯一组合索引)

## 6. 模块划分建议
- `hzapp-module-erplus-api`: 定义核心接口与模型（基础设施）。
- `hzapp-module-erplus-biz`: 实现任务调度引擎核心。
- 其他业务模块: 实现各自的 `ErpTaskHandler`。
// 传递上下文
    private String errorMessage;   // 错误信息
}
```

## 4. 核心流程

### 4.1 任务提交与分发
1. 业务模块调用 `engine.submitTask()`。
2. 引擎设置任务为 `PENDING`，计算 `scheduledAt`。
3. 调度器扫描到任务，根据 `taskType` 找到对应的 `AdsTaskHandler`。
4. 调用 `handler.onStart(task)`。

### 4.2 异步报表流转示例 (Amazon)
1. **PENDING**: `onStart` 调用 Amazon API 提交报表。
   - 返回 `status = SUBMITTED`, `platformJobId = amz_job_123`。
2. **SUBMITTED**: 引擎轮询调用 `onPoll`。
   - 若 Amazon 未完成：返回 `status = SUBMITTED`。
   - 若 Amazon 已完成：返回 `status = PROCESSING`。
3. **PROCESSING**: 
   - 业务模块启动异步下载（如通过另一个线程池或 MQ）。
   - **此时引擎停止调度该任务**。
4. **CALLBACK**: 
   - 异步下载完成后，调用 `engine.completeTask(taskId, success())`。
   - 引擎更新任务为 `SUCCESS`，并检查父任务状态。

## 5. 健壮性设计
- **重试机制**：`retryCount` < `maxRetries` 时，引擎自动计算退避时间并重新设为 `PENDING`。
- **超时检测 (Watchdog)**：引擎定时扫描处于 `PROCESSING` 状态超过阈值（如 2 小时）的任务，标记为 `FAILED` 或触发自动纠错。
- **父子任务同步**：当最后一个子任务标记为 `SUCCESS` 时，引擎自动汇总更新父任务状态。

## 6. 模块划分建议
- `hzapp-module-erplus-adv-api`: 定义接口 `AdsTaskEngine`, `AdsTaskHandler` 以及数据模型。
- `hzapp-module-erplus-adv-biz`: 实现 `AdsTaskEngine` 核心逻辑（调度器、状态机、API）。
- `hzapp-module-erplus-amz-biz`: 实现具体的 `AdsTaskHandler`（处理 Amazon 特定逻辑）。
