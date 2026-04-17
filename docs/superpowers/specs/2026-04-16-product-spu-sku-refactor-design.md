# 商品 SPU/SKU 数据模型重构设计文档 (V5)

## 1. 背景与目标 (Background & Goals)

随着商品信息维度的增加，现有的 `erplus_product_spu` 和 `erplus_product_sku` 表字段急剧膨胀。为了提高系统的可维护性、降低数据冗余，并统一前后端数据处理逻辑，本次重构旨在：
1.  **明确主模型边界**：主表仅保留核心标识、业务约束及参与列表筛选/展示的字段。
2.  **属性化存储**：将采购、物流、海关、合规等扩展信息移至独立的属性表存储。
3.  **固化映射关系**：通过代码定义（Enum/Registry）固化属性 Key 与 DTO 的映射，确保类型安全。
4.  **严格区分层级**：SPU 与 SKU 属性物理分离，不支持 SKU 自动继承 SPU 的未定义属性。

## 2. 字段划分方案 (Field Mapping)

原则：**核心展示与筛选留主表，详细业务参数进属性。**

### 2.1 主模型保留字段 (Main Table)

| 字段 | SPU | SKU | 说明 |
| :--- | :---: | :---: | :--- |
| `id` | √ | √ | 主键 |
| `code` | √ | √ | 编码 (SPU Code / Seller SKU) |
| `name` | √ | √ | 展示名称 |
| `bar_code` | - | √ | SKU 条形码 (EAN/UPC) |
| `category_id` | √ | - | 分类约束 (SKU不独立定义) |
| `brand_id` | √ | - | 品牌约束 (SKU不独立定义) |
| `unit_id` | √ | - | 单位约束 |
| `pic_url` | √ | √ | 核心主图 |
| `slider_pic_urls` | √ | √ | 轮播图 |
| `description` | √ | √ | 详情描述 |
| `spec_type` | √ | - | 规格类型 (单规格/多规格) |
| `type` | - | √ | 产品类型 (普通/组合) |
| `status` | √ | √ | 商品状态 (上架/下架等) |
| `sort` | √ | - | 排序 |
| `price` | √* | √ | 销售价 (*SPU为聚合值) |
| `cost_price` | √* | √ | 成本价 (*SPU为聚合值) |
| `market_price` | √* | √ | 市场价 (*SPU为聚合值) |
| `stock` | √* | √ | 库存 (*SPU为聚合值) |

### 2.2 扩展属性 (Attributes)

以下信息将存储于 `erplus_product_spu_attrs` 或 `erplus_product_sku_attrs`：

- **物流规格 (Logistics)**：`weight`, `volume`, `itemDim`, `pkgDim`, `boxDim`, `inboxnum`, `packingSchemes`。
- **采购属性 (Purchase)**：供应商信息、起订量、采购周期等。
- **海关合规 (Customs)**：HS Code、申报名称、申报价值。
- **合规资质 (Compliance)**：认证信息、材质、安全标准、警告语、限制地区等。
- **销售拓展 (Sales)**：分销佣金、积分配置等。

## 3. 技术架构设计 (Technical Architecture)

### 3.1 后端实现 (Backend)

#### **1. 属性注册表 (Attribute Registry)**
在 `hzapp-module-erplus-api` 中定义 `ProductAttrRegistry` 枚举：
```java
public enum ProductAttrRegistry {
    LOGISTICS("logistics", "物流信息", ProductLogisticsDTO.class),
    PURCHASE("purchase", "采购信息", ProductPurchaseAttrDTO.class),
    // ...
}
```

#### **2. 数据传输对象 (VO)**
`ProductSpuRespVO` / `ProductSkuRespVO` 将保留原有核心字段，并移除打平的扩展字段，新增：
```java
private Map<String, Object> attributes;
```

#### **3. 持久化逻辑**
*   **读取**：查询详情时，根据 `spu_id` / `sku_id` 从 `erplus_product_xxx_attrs` 批量拉取数据，并根据 `attr_class` 解析为 DTO，放入 `attributes` Map。
*   **更新**：保存 SPU/SKU 时，拦截 `attributes` Map，对比属性表中的现有值，执行差异更新（Insert/Update/Delete）。

### 3.2 数据库变更 (SQL)

执行 `hzapp-module-erplus/sql/update_product_center_v1.4.sql`：
- `ALTER TABLE erplus_product_spu DROP COLUMN introduction, keyword, ..., item_dim, ...;`
- `ALTER TABLE erplus_product_sku DROP COLUMN weight, volume, logistics, ...;`
- 确保属性表字段 `attr_id` 索引。

### 3.3 前端实现 (Vue3)

- **表单结构**：统一使用 `activeRoom.attributes.logistics.weight` 类似的路径进行绑定。
- **按组渲染**：前端维护一组业务组件，根据属性 Key 自动加载对应的子表单。

## 4. 关键规则约束 (Constraints)

1.  **选项 B 策略**：SKU 不自动继承 SPU 属性。业务代码在业务层感知属性缺失。
2.  **类型检查**：后端在 `save` 阶段校验 Map 中的 Value 类型是否与 `ProductAttrRegistry` 定义一致。
3.  **列表性能**：扩展属性默认不进入 `PageQuery` 筛选，如需筛选，需在主表建立冗余字段并加索引。

## 5. 验证计划 (Verification Plan)

### 5.1 自动化测试
- 编写 MockMvc 测试用例，验证 SPU 创建时 `attributes` 是否正确落库到属性表。
- 验证 SKU 更新时，主表字段与属性表字段是否同步更新。

### 5.2 手动验收
- 在后台“商品编辑”页面修改物流信息，刷新后检查数据库属性表及前端展示是否一致。
- 验证单规格/多规格切换时，属性的绑定关系是否正确。
