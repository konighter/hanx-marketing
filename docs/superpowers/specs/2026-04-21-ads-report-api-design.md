# AdsReportApi 结构化查询设计方案

## 1. 背景与目标

为了支持跨平台（Amazon, Meta, Google 等）、多层级（Campaign, AdGroup, Ad, Keyword 等）以及多维度聚合的广告绩效数据查询，我们需要设计一套底层的、高灵活性的 API。

该 API 将作为以下场景的基础设施：
- **前端仪表盘**: 提供汇总数据和趋势图。
- **数据列表页**: 提供多层级筛选和细粒度绩效展示。
- **AI Agent 工具**: 提供“切片与切块”的强悍数据检索能力。

## 2. 核心设计：结构化查询 DSL

采用扁平化、类 OLAP 的查询模型，调用方通过传递 `dimensions`，`metrics` 和具体明确的筛选参数来获取所需数据。

### 2.1 请求对象 (AdsReportQueryReqVO)

完全放弃抽象的动态 Filter 匹配，改用强类型字段。这极大增强了 Swagger 接口文档的直观性以及代码的健壮性。

```java
public class AdsReportQueryReqVO {
    
    // ===== 必填：时间与隔离边界 =====
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
    
    // ===== 核心：分组与指标 =====
    @Schema(description = "聚合维度（分组依据），例如: ['platform', 'campaign_id', 'date']")
    private List<String> dimensions; 

    @Schema(description = "请求的基础/平台指标，例如: ['impressions', 'spend', 'sales']")
    private List<String> metrics;

    // ===== 明确枚举的过滤条件 (支持下钻与切片) =====
    private List<String> platforms;
    private List<Long> campaignIds;
    private List<Long> adGroupIds;
    private List<Long> adIds;
    private List<Long> keywordIds;
    private List<String> productIds; // (ASIN/SKU等)

    // ===== 展现控制 =====
    @Schema(description = "时间聚合粒度: DAY, WEEK, MONTH, TOTAL")
    private String interval;

    @Schema(description = "用于图表等非分页防超载 (例如限制 1000)")
    private Integer limit; 
    
    @Schema(description = "分页使用")
    private Integer pageNo;
    private Integer pageSize;
    
    @Schema(description = "基于基础指标或维度的排序，计算型指标排序由前端处理")
    private String orderBy; 
    private Boolean isAsc;
}
```

### 2.2 响应对象 (AdsReportDataRespVO)

定义高度自描述的 Row 结构，使得维度（身份）和指标（数值）分离。前端负责根据店铺 Settings 进行单位展示和格式化。

```java
public class AdsReportDataRespVO {
    // 多维数据行列表
    private List<AdsReportRowVO> rows;
    
    // 全局汇总行（仅含总体指标计算，无维度）
    private AdsReportRowVO summary;
    
    // 总行数（分页用）
    private Long total;
}

@Data
public class AdsReportRowVO {
    private List<DimensionValueVO> dimensions;
    private List<MetricValueVO> metrics;
}

@Data
public class DimensionValueVO {
    private String key;       // 维度标识，如 "campaign_id"
    private String value;     // 原始值，如 "12345"
    private String label;     // 显示名称，如 "Summer_SP_Auto"。后续在组装数据时，通过本地广告缓存(Redis/Caffeine)高效填充。
}

@Data
public class MetricValueVO {
    private String key;       // 指标标识，如 "spend"
    private Object value;     // 原始数字数值，例如 120.5。多币种、格式化(千分位/百分比)均由前端根据当前店铺设置处理。
}
```

## 3. 标准化语义注册 (Metadata Routing)

后端维护一套映射逻辑，将逻辑名称（Logical Key）映射到 Doris 数据库的实际物理列。

### 3.1 维度表 (Dimensions)

| 逻辑名称 | 物理映射 (Doris) | 说明 |
| :--- | :--- | :--- |
| `platform` | `platform` | 广告平台 |
| `campaign_id` | `campaign_id` | 广告活动 ID |
| `ad_group_id` | `ad_group_id` | 广告组 ID |
| `ad_id` | `ad_id` | 广告 ID |
| `keyword_id`| `keyword_id` | 关键词 ID |
| `date` | `report_date` | 报表日期 |

### 3.2 基础与平台扩展指标 (Metrics)

为了支持各平台的差异化特征，核心存储表将基础指标拉平，平台特色归因作为扩展列。

#### 基础指标 (跨平台通用)
| 逻辑名称 | 物理计算逻辑 | 说明 |
| :--- | :--- | :--- |
| `impressions` | `SUM(impressions)` | 曝光量 |
| `clicks` | `SUM(clicks)` | 点击量 |
| `spend` | `SUM(cost)` | 花费 |
| `sales` | `SUM(sales)` | 通用/估算销售额 |
| `orders` | `SUM(orders)` | 通用/估算订单数 |
| `ctr` | [后置计算] `clicks / impressions`| 点击率 (不支持 DB 排序) |
| `cpc` | [后置计算] `cost / clicks` | 单次点击成本 (不支持 DB 排序) |
| `acos`| [后置计算] `cost / sales` | ACOS (不支持 DB 排序) |
| `roas`| [后置计算] `sales / cost` | ROAS (不支持 DB 排序) |

#### Amazon 平台专属扩展指标
| 逻辑名称 | 物理计算逻辑 | 说明 |
| :--- | :--- | :--- |
| `amz_sales_1d` | `SUM(amz_attributed_sales_1d)` | 1天归因销售额 |
| `amz_sales_7d` | `SUM(amz_attributed_sales_7d)` | 7天归因销售额 |
| `amz_sales_14d` | `SUM(amz_attributed_sales_14d)` | 14天归因销售额 |
| `amz_sales_30d` | `SUM(amz_attributed_sales_30d)` | 30天归因销售额 |
| `amz_orders_1d` | `SUM(amz_attributed_units_ordered_1d)` | 1天归因订单 |
| `amz_orders_7d` | `SUM(amz_attributed_units_ordered_7d)` | 7天归因订单 |
| `amz_orders_14d` | `SUM(amz_attributed_units_ordered_14d)` | 14天归因订单 |
| `amz_orders_30d` | `SUM(amz_attributed_units_ordered_30d)` | 30天归因订单 |
| `amz_sales_1d_same_sku` | `SUM(amz_attributed_sales_1d_same_sku)` | 1天归因同SKU销售额 |
| `amz_sales_7d_same_sku` | `SUM(amz_attributed_sales_7d_same_sku)` | 7天归因同SKU销售额 |
| `amz_sales_14d_same_sku` | `SUM(amz_attributed_sales_14d_same_sku)`| 14天归因同SKU销售额 |
| `amz_sales_30d_same_sku` | `SUM(amz_attributed_sales_30d_same_sku)`| 30天归因同SKU销售额 |

#### Meta (Facebook) 平台专属扩展指标
| 逻辑名称 | 物理计算逻辑 | 说明 |
| :--- | :--- | :--- |
| `meta_reach` | `SUM(meta_reach)` | 到达人数 (Reach) |
| `meta_frequency` | `SUM(meta_frequency)` | 展示频率 |
| `meta_purchases_1d_click`| `SUM(meta_purchases_1d_click)` | 1天点击归因购买数 |
| `meta_purchases_7d_click`| `SUM(meta_purchases_7d_click)` | 7天点击归因购买数 |
| `meta_purchases_1d_view` | `SUM(meta_purchases_1d_view)` | 1天浏览归因购买数 |

#### Google 平台专属扩展指标
| 逻辑名称 | 物理计算逻辑 | 说明 |
| :--- | :--- | :--- |
| `gg_view_through_conversions` | `SUM(gg_view_through_conversions)` | 浏览型转化数 |
| `gg_conversions` | `SUM(gg_conversions)` | 转化数 (支持小数) |
| `gg_conversion_value`| `SUM(gg_conversion_value)`| 转化总价值 |

## 4. 后端执行流程

1. **安全边界限定**: `shopId` 是顶级属性并带有 `@NotNull` 校验。
2. **过滤构造**: 将对象上非空的 ID 集合（如 `campaignIds`、`platforms` 数组）拼接为 SQL 的 `IN (...)` 子句。
3. **聚合解析**: 将 `dimensions` 转换为 SQL `GROUP BY` 子句。基础 `metrics` 转为 `SUM()` 聚合。
4. **SQL 查询**: 基于 Doris 报表视图执行查询，结合 `LIMIT` 以及 `ORDER BY` 控制条数与排序保障速度。
5. **引擎后计算**: 
   - 内存二次计算如 `ACOS`, `CTR` 比例类指标。
   - 【Cache 关联】提取返回结果内的实体 ID，请求本地 Cache 获得中文/原名，写入 `label`。
6. **对象封包**: 输出 List 格式的结构以供前端视图适配。
