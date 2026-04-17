# 商品 SPU/SKU 数据模型重构执行计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 SPU/SKU 的非核心参数从主表移至属性表，通过代码固化映射规则，实现主模型清减。

**Architecture:** 
- 采用代码注册（Registry）模式定义属性 Key 与 DTO。
- 采用严格分离策略（Option B），SPU/SKU 属性物理隔离。
- 业务层封装通用的 AttributeHandler 处理 JSON 到 DTO 的转换。

**Tech Stack:** Java, MyBatis-Plus, MapStruct, Vue 3, MySQL.

---

## Chunk 1: API 与常量定义

**Files:**
- Create: `hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/enums/ProductAttrKeyEnum.java`
- Create: `hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/dto/attr/ProductLogisticsAttrDTO.java`
- Create: `hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/dto/attr/ProductPurchaseAttrDTO.java`

- [ ] **Step 1: 定义属性 Key 枚举**
  在 API 模块中定义 `ProductAttrKeyEnum`，包含 key, name, 对应的 DTO Class。
  ```java
  public enum ProductAttrKeyEnum {
      LOGISTICS("logistics", "物流信息", ProductLogisticsAttrDTO.class),
      PURCHASE("purchase", "采购信息", ProductPurchaseAttrDTO.class);
      // ... 
  }
  ```

- [ ] **Step 2: 创建属性 DTO 结构**
  将原有散落在 DO 里的字段（如 weight, volume）整合进对应的 DTO 中。

- [ ] **Step 3: Commit**
  ```bash
  git add hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/enums/ hzapp-module-erplus-api/src/main/java/com/hzltd/module/erplus/dto/attr/
  git commit -m "feat: define product attribute registry and DTOs"
  ```

---

## Chunk 2: DO 与 VO 模型清减

**Files:**
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/dal/dataobject/spu/ProductSpuDO.java`
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/dal/dataobject/spu/ProductSkuDO.java`
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/controller/admin/spu/vo/ProductSpuRespVO.java`
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/controller/admin/spu/vo/ProductSpuSaveReqVO.java`

- [ ] **Step 1: 移除 DO/VO 中的非核心字段**
  移除 `itemDim`, `pkgDim`, `logistics`, `weight`, `volume` 等。

- [ ] **Step 2: 注入 attributes Map**
  在 RespVO 和 SaveReqVO 中增加 `Map<String, Object> attributes`。

- [ ] **Step 3: Commit**
  ```bash
  git commit -m "refactor: clean up product DO/VO fields and add attributes map"
  ```

---

## Chunk 3: Service 层通用逻辑重构

**Files:**
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/service/spu/ProductSpuServiceImpl.java`
- Modify: `hzapp-module-erplus-biz/src/main/java/com/hzltd/module/erplus/service/spu/ProductSkuServiceImpl.java`

- [ ] **Step 1: 抽象 AttributeHandler 逻辑**
  重构 `saveProductAttrs` 为通用方法，支持传入 `spuId` 或 `skuId`。
  
- [ ] **Step 2: 实现自动装箱/拆箱逻辑**
  在查询 SPU/SKU 详情时，自动从属性表根据 Key 查出记录，反序列化后塞入 `attributes` Map。

- [ ] **Step 3: 实现保存时的差异更新**
  保存 SPU 和 SKU 时，分别调用 `saveAttributes` 方法。

---

## Chunk 4: 前端 UI 适配

**Files:**
- Modify: `hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product_center/spu/form/ProductFormV2.vue`
- Modify: `hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product_center/spu/form/ProductLogisticsInfo.vue`

- [ ] **Step 1: 调整表单数据绑定路径**
  由 `form.logistics.weight` 改为 `form.attributes.logistics.weight`。

- [ ] **Step 2: 验证编辑回显逻辑**

---

## Chunk 5: 数据库清理 (Final Step)

- [ ] **Step 1: 生成并执行 SQL 清理脚本**
  通过 `ALTER TABLE` 移除主表对应字段。
