# 亚马逊广告运营开发计划

## 项目概述
基于现有亚马逊广告模块(hzapp-module-erplus-amz-adv)，扩展完整的亚马逊广告运营功能，重点支持Sponsored Products(商品推广)类型广告。

## 功能模块设计

### 1. 广告活动管理模块
- **目标**：实现广告活动的全生命周期管理
- **位置**：`hzapp-module-erplus/hzapp-module-erplus-amz-adv/src/main/java/com/hzltd/module/erplus/amzadv/service/AmzAdvCampaignService.java`
- **功能**：
  - 创建/修改/暂停/启动广告活动
  - 批量操作广告活动
  - 广告活动列表查询和筛选

### 2. 广告组和关键词管理模块
- **目标**：管理广告组和关键词投放策略
- **位置**：`hzapp-module-erplus/hzapp-module-erplus-amz-adv/src/main/java/com/hzltd/module/erplus/amzadv/service/AmzAdvAdGroupService.java`
- **功能**：
  - 广告组创建和管理
  - 关键词添加、删除、修改
  - 关键词匹配类型管理(精准、短语、广泛)

### 3. 预算和出价管理模块
- **目标**：自动化预算分配和智能出价策略
- **位置**：`hzapp-module-erplus/hzapp-module-erplus-amz-adv/src/main/java/com/hzltd/module/erplus/amzadv/service/AmzAdvBidStrategyService.java`
- **功能**：
  - 广告活动预算设置
  - 自动出价策略
  - 预算监控和提醒

### 4. 报告和数据分析模块
- **目标**：扩展现有的报告功能，提供更丰富的分析
- **位置**：扩展`hzapp-module-erplus/hzapp-module-erplus-amz-adv/src/main/java/com/hzltd/module/erplus/amzadv/service/AmzAdvReportService.java`
- **功能**：
  - 细粒度数据报告
  - 数据可视化
  - 性能分析报表

## 开发步骤

### 第一阶段：基础设施开发
1. 创建新的服务类和数据模型
2. 完善现有认证机制
3. 设计数据库表结构

### 第二阶段：核心功能开发
1. 实现广告活动管理功能
2. 实现广告组和关键词管理功能
3. 实现出价和预算管理功能

### 第三阶段：报告和分析功能
1. 扩展现有报告功能
2. 添加数据可视化功能
3. 性能优化

### 第四阶段：UI界面开发
1. 创建前端管理页面
2. 集成到现有管理后台
3. 用户体验优化

## 技术实现细节

### 服务层扩展
- 继承现有抽象基类`AbsAmzAdvService`
- 遵循现有代码规范和模式
- 使用相同的认证和授权机制

### 控制器层扩展
- 在`AmzAdvReportController`基础上添加新功能
- 遵循RESTful API设计原则
- 集成Swagger文档

### 数据持久化
- 创建相应的DO(数据对象)类
- 创建对应的Mapper接口
- 使用MyBatis-Plus进行数据库操作

## Git工作流规范
- 从develop分支创建特性分支
- 功能开发完成后提交PR合并到develop
- 遵循约定的提交信息格式

## 时间安排
- 第一阶段：2-3天
- 第二阶段：5-7天
- 第三阶段：3-5天
- 第四阶段：2-3天