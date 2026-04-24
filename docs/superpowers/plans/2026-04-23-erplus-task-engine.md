# Erplus 通用任务调度引擎实现计划 (Erplus Schedule Task Engine Implementation Plan)

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个全系统通用的任务调度引擎，将任务的生命周期管理与业务执行逻辑彻底解耦。

**Architecture:** 采用“API 定义模型与接口，BIZ 实现逻辑与存储”的解耦模式。引入 `ErpTaskModel` 作为 API 层的核心交互对象，隔离 `ErpScheduleTaskDO` 的底层变更。所有核心 API 均放置在 `system` 包下。

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, MySQL

---

## Chunk 1: 基础设施与模型定义

### Task 1: API 模块定义 (核心契约)
**Files:**
- Create: `hzapp-module-erplus/hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/system/api/ErpTaskEngine.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/system/api/ErpTaskHandler.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/system/model/ErpTaskModel.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/system/dto/ErpTaskSubmitRequest.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/system/dto/ErpTaskResult.java`

- [ ] **Step 1: 创建 ErpTaskModel** (与 DO 字段对应，作为 Handler 执行时的上下文对象)
- [ ] **Step 2: 定义 ErpTaskHandler 接口，使用 ErpTaskModel**
```java
public interface ErpTaskHandler {
    ErpTaskResult onStart(ErpTaskModel task);
    ErpTaskResult onPoll(ErpTaskModel task);
    String getTaskType();
}
```
- [ ] **Step 3: 定义 ErpTaskEngine 接口 (submit/submitChild/complete)**
- [ ] **Step 4: 定义 DTO (ErpTaskSubmitRequest / ErpTaskResult)**

### Task 2: BIZ 模块定义 (存储与 DO)
**Files:**
- Create: `hzapp-module-erplus/sql/v1.5_create_schedule_task.sql`
- Create: `hzapp-module-erplus/hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/system/dal/dataobject/ErpScheduleTaskDO.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/system/dal/mysql/ErpScheduleTaskMapper.java`

- [ ] **Step 1: 编写 SQL 创建 erplus_schedule_task 表** (保留 ads_sync_task 不动)
- [ ] **Step 2: 实现 ErpScheduleTaskDO** (定义在 biz 模块的 system 包下)
- [ ] **Step 3: 实现 ErpScheduleTaskMapper**

---

## Chunk 2: 核心引擎实现 (BIZ)

### Task 3: 引擎逻辑开发
**Files:**
- Create: `hzapp-module-erplus/hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/system/service/ErpTaskEngineImpl.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/system/convert/ErpTaskConvert.java`

- [ ] **Step 1: 实现 DO 与 Model 的互转逻辑 (Convert)**
- [ ] **Step 2: 实现 submitTask 逻辑** (基于 task_unique_id 的幂等处理)
- [ ] **Step 3: 实现 completeTask 回调逻辑** (级联父任务更新)
- [ ] **Step 4: 实现调度器分流逻辑**

---

## Chunk 3: 业务迁移与集成

### Task 4: 迁移 Amazon 广告报表任务
- [ ] **Step 1: 在 adv 模块实现 AmzReportSyncTaskHandler**
- [ ] **Step 2: 修改任务创建入口，改用新的 ErpTaskEngine 提交**

---

## Chunk 4: 验证

### Task 5: 全链路验证
- [ ] **Step 1: 验证任务在 erplus_schedule_task 表中的完整流转**
- [ ] **Step 2: 验证数据隔离性 (Model vs DO)**
