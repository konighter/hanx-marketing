# 亚马逊广告查询链路对齐实施计划 (2026-04-24)

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完成亚马逊广告查询链路的对齐，确保属性表中的数据正确展示，并清理前端不合理的数据合并逻辑。

**Architecture:** 
- **Backend**: 保持现有 `AdsCampaignServiceImpl.getCampaignAttributes` 逻辑，确保数据通过 `attributes` 字段返回。
- **Frontend**: 重构 `AdCampaignDetailDrawer` 与 `AmazonCampaignConfig`，实现数据解耦与标准键名绑定。

**Tech Stack:** Vue 3, Element Plus, Java (Spring Boot)

---

### Task 1: 前端详情抽屉重构 (AdCampaignDetailDrawer.vue)

**Files:**
- Modify: `hzapp-ui-admin-vue3-tiny/src/app/erplus/views/adv/components/AdCampaignDetailDrawer.vue`

- [ ] **Step 1: 移除不合理的数据合并逻辑**
  修改 `open` 方法，停止将 `shopId` 和 `accountId` 注入 `platformConfig`。

```javascript
// 修改前
platformConfig.value = {
  ...(extData.platformConfig || {}),
  shopId: res.shopId,
  accountId: res.accountId
}

// 修改后
platformConfig.value = res.attributes || {}
```

- [ ] **Step 2: 显式传递上下文属性**
  如果子组件需要 `shopId` 等信息，通过独立的 Prop 传递（检查子组件定义）。

- [ ] **Step 3: 清理保存逻辑中的清洗代码**
  在 `handleSave` 中移除对 `shopId` / `accountId` 的 `delete` 操作。

---

### Task 2: 亚马逊配置组件对齐 (AmazonCampaignConfig.vue)

**Files:**
- Modify: `hzapp-ui-admin-vue3-tiny/src/app/erplus/views/adv/components/AmazonCampaignConfig.vue`

- [ ] **Step 1: 更新模板键名绑定**
  将模板中所有 `modelValue.xxx` 的引用更新为 `modelValue.amz_xxx`。

```html
<!-- 示例 -->
<el-radio-group v-model="modelValue.amz_dynamic_bidding.strategy">
```

- [ ] **Step 2: 移除内部冗余逻辑**
  检查并移除组件内对 `shopId` 或 `accountId` 的内部逻辑，确保其仅处理 `attributes` 数据。

---

### Task 3: 验证链路

- [ ] **Step 1: 启动前端并进入广告计划详情**
- [ ] **Step 2: 验证竞价策略展示**
  确认“动态竞价”等 Amazon 特有配置是否能正确从 `attributes` 中加载并渲染。
- [ ] **Step 3: 检查控制台报错**
  确保没有因键名缺失或类型不匹配导致的运行时异常。
