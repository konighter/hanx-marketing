# 本地商品层级结构与管理方案设计 (SPU/SKU Redesign)

## 背景与目标
为了解决现有系统中商品编码管理混乱、SKU 与 SPU 关系不明确、以及组合产品管理缺失的问题，本方案旨在重新规划本地商品的层级结构。

核心目标：
1. **明确层级关系**：SPU:SKU = 1:N。每个 SKU 必须且只能属于一个 SPU。
2. **支持自动关联**：单独创建 SKU 时自动生成单规格 SPU。
3. **区分产品类型**：明确 SPU 的“单/多规格”与 SKU 的“普通/组合”业务定义。
4. **统一编码规则**：定义 `spu_code` (内部) 与 `sku_code` (外部/Amazon seller_sku)，支持自动生成。
5. **优化查询性能**：SKU 视图以 SKU 为中心，高效关联 SPU 基础信息，消除 1+N 查询瓶颈。

---

## 业务定义与类型 (Type Definitions)

### 1. SPU 规格类型 (ErplusProductSpuDetailType)
定义在后端 `com.hzltd.module.erplus.enums.product.ProductSpuSpecTypeEnum`：
- **SINGLE (1, "单规格")**: 一个 SPU 仅包含一个与之对应的 SKU。
- **MULTI (2, "多规格")**: 一个 SPU 包含多个不同规格属性（如颜色、尺寸）的子 SKU。

### 2. SKU 产品类型 (ErplusProductSkuType)
定义在后端 `com.hzltd.module.erplus.enums.product.ProductSkuTypeEnum`：
- **ORDINARY (1, "普通产品")**: 标准的实物库存单元。
- **COMBO (2, "组合产品")**: 虚拟的销售单元，由一个或多个普通 SKU 按照特定数量构成。

---

## 数据模型变更

### 1. 数据库变更 (SQL)
```sql
-- SPU 表扩展
ALTER TABLE `erplus_product_spu` 
    ADD COLUMN `code` varchar(64) DEFAULT NULL COMMENT '内部SPU编码' AFTER `id`,
    MODIFY COLUMN `spec_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规格类型: 1-单规格, 2-多规格';

-- SKU 表扩展
ALTER TABLE `erplus_product_sku` 
    ADD COLUMN `code` varchar(64) DEFAULT NULL COMMENT '外部SKU编码(seller_sku)' AFTER `spu_id`,
    ADD COLUMN `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '产品类型: 1-普通产品, 2-组合产品';

-- 组合产品明细表 (NEW)
CREATE TABLE `erplus_product_sku_combo` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_sku_id` bigint(20) NOT NULL COMMENT '组合SKU编号',
    `child_sku_id` bigint(20) NOT NULL COMMENT '成分SKU编号',
    `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '成分数量',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_sku` (`parent_sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组合商品明细表';
```

---

## 核心业务流程

### 1. 编码自动生成 (ProductCodeService)
规则：`[分类前缀] + [YYYYMMDD] + [4位流水号]`
- 接口：`String generateSpuCode(Long categoryId)`
- 接口：`String generateSkuCode(Long categoryId)`
- 逻辑：在保存前，若前端未手动输入 code，则调用此服务。

### 2. 自动 SPU 关联逻辑
当用户在“SKU 模式”下添加产品时（实际上是创建 SKU）：
1. 检查 `spuId` 是否为空。
2. 若为空：
   - 自动创建一个 `ProductSpuDO`，名称取 SKU 名称，`spec_type = SINGLE`。
   - 自动生成 `spu_code`。
   - 将生成的 `spuId` 赋给待创建的 SKU。
3. 插入 SKU。

### 3. 查询视图优化
#### SKU 中心查询方式：
后端 `ProductSkuMapper` 提供 `selectPageWithSpuInfo` 接口：
```sql
SELECT 
    sku.*, 
    spu.categoryId, spu.brandId, spu.name as spuName, spu.code as spuCode 
FROM erplus_product_sku sku
LEFT JOIN erplus_product_spu spu ON sku.spu_id = spu.id
WHERE sku.deleted = 0 AND spu.deleted = 0
<筛选条件...>
```
此举规避了循环查询 SPU 的性能问题，支持：
- 按 `sku_code` / `bar_code` 搜索。
- 按 `spu_code` 搜索。
- 按分类、品牌筛选（直接由 JOIN 的 SPU 提供字段）。

---

## 验证计划
### 自动化测试
- [ ] 测试 `createSku` 时，单规格 SPU 是否正确生成。
- [ ] 测试组合产品 (Combo SKU) 的 components 是否正确存入 `erplus_product_sku_combo`。
- [ ] 测试分页查询 SQL 性能，确认在大数据量下联查 SPU 字段的效率。
### 手动校验
- [ ] 界面切换为 “SKU 模式”，添加一个产品，查看展示是否以 SKU 为主。
- [ ] 验证 `sku_code` 修改后，Amazon Listing 匹配链路是否通畅。
