# 耗材管理与生产装配设计文档

本方案旨在为 ERP 系统增加耗材（Material）管理功能，并通过“生产装配”流程建立成品商品（SPU/SKU）与耗材之间的 BOM 关联，实现耗材库存的自动扣减与成品库存的自动增加。

## 1. 业务术语定义

*   **耗材 (Material)**: 指不可拆分的生产原材料或包装材料（如：纸箱、标签、胶带、零件等）。
*   **BOM (Bill of Materials)**: 商品的耗材清单，定义了产出一个成品所属的各种耗材及其配比。
*   **装配单 (Production Order)**: 手动发起的生产指令，记录目标产品、计划数量及消耗的耗材明细。
*   **批次号 (Batch No)**: 系统自动生成的生产批次，关联成品的入库记录。

## 2. 核心架构设计

### 2.1 模块划分
*   **商品模块 (Product Module)**: 增加 SPU 层级的耗材 BOM 配置。
*   **库存模块 (Stock Module)**: 
    *   新增【耗材库存】管理：展示耗材在各仓的存量。
    *   新增【组合装配】业务：管理生产任务的全生命周期。

### 2.2 数据模型 (Entity)

#### ErpMaterialDO (耗材定义)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 主键 |
| name | String | 耗材名称 |
| code | String | 耗材编码 |
| unit | String | 单位 (个, 米, kg等) |
| length | BigDecimal | 长 (cm) |
| width | BigDecimal | 宽 (cm) |
| height | BigDecimal | 高 (cm) |
| weight | BigDecimal | 重量 (kg) |
| volume | BigDecimal | 体积 (m³) |
| capacity | BigDecimal | 容积 |
| remark | String | 备注 |

#### ErpMaterialStockDO (耗材库存)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 主键 |
| materialId | Long | 关联 ErpMaterialDO |
| warehouseId | Long | 关联 ErpWarehouseDO |
| quantity | BigDecimal | 当前库存数量 |

#### ErpProductMaterialDO (商品耗材BOM)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 主键 |
| spuId | Long | 关联 ProductSpuDO |
| materialId | Long | 关联 ErpMaterialDO |
| usageQuantity | BigDecimal | 单个成品的标称用量 |

#### ErpAssemblyOrderDO (装配单)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 主键 |
| no | String | 业务单号 (自动生成) |
| skuId | Long | 产出的目标 SKU |
| warehouseId | Long | 生产/出库所属仓库 |
| plannedQty | BigDecimal | 计划生产数量 |
| actualQty | BigDecimal | 实际生产数量 (默认等于计划数) |
| batchNo | String | 批次号 (完成生产时由系统分配合规编号) |
| status | Integer | 状态: 0-待启动, 1-生产中, 2-已完成, 3-已取消 |

#### ErpAssemblyItemDO (装配单耗材明细)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 主键 |
| orderId | Long | 关联生产单 |
| materialId | Long | 耗材编号 |
| expectedQty | BigDecimal | 应耗数量 (计划数 * BOM单量) |
| shortfallQty | BigDecimal | 缺料数量 (当前库存不足时的差值) |

## 3. 业务流程与逻辑

### 3.1 生产单生命周期
1.  **新建生产单**:
    *   用户选择目标 SKU 和计划数量。
    *   系统根据该 SKU 所属的 SPU 查询 `ErpProductMaterialDO`。
    *   自动生成 `ErpAssemblyItemDO` 明细，初始状态为“待启动”。
2.  **启动生产**:
    *   **触发动作**: 点击“启动生产”。
    *   **库存校验**: 检查对应仓库内各耗材的 `ErpMaterialStockDO` 是否足够。
    *   **分支 A (库存充足)**: 状态更新为“生产中”。
    *   **分支 B (库存不足)**: 状态保持为“待启动”，系统计算并填充 `shortfallQty`。该明细数据可展示为“待采购清单”。
3.  **完成生产**:
    *   **生成批次号**: 系统生成规则推荐为 `PROD-YYYYMMDD-序号`。
    *   **库存变更 (事务性)**:
        *   `ErpStockDO`: 增加目标成品 SKU 的库存。
        *   `ErpMaterialStockDO`: 扣减耗材库存。
        *   `ErpStockRecordDO`: 写入产品库存明细 (业务类型为“组合装配”)。
    *   **更新状态**: 状态更新为“已完成”。

## 4. UI 界面设计要求

### 4.1 SPU 详情页
*   增加 `耗材 BOM` 标签页。
*   核心交互：新增/删除耗材行，设置单位用量。

### 4.2 库存管理模块
*   **耗材查询**: 表格展示耗材基础信息及总库存/分仓库存情况。
*   **组合装配**: 
    *   列表展示生产单历史状态、单号、批次号。
    *   详情页展示耗材明细，对于库存不足的项用红色高亮 `缺料量`。

## 5. 待确认与补充 (后续迭代)
*   **采购集成**: 当前版本暂不关联采购单，缺料清单仅作为显示参考。
*   **SKU 弱化策略**: 根据用户反馈，当前以 SPU 配置为主，未来若 SKU 级联需求增加，将调整关联表外键。
