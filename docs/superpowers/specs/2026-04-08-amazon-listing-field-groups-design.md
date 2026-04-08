# Amazon Listing Dynamic Form V2 字段分组设计方案

## 背景

现有的 `AmazonListingDynamicForm.vue` 组件使用硬编码的 `AmazonGroups` 数组进行字段分组，采用关键字匹配（不区分大小写的 contains 匹配）方式。这种方式存在以下问题：

1. **误匹配问题** — 关键字匹配可能将字段分配到错误的分组，如 `material_type` 可能被错误分到"基本信息"
2. **缺乏精确控制** — 无法显式指定特定字段必须属于某个分组
3. **扩展性差** — 新增/调整分组需要修改代码

## 目标

1. 新增统一的分组配置 `AmazonFieldGroupsV2`，支持精确匹配、前缀匹配、关键字匹配三种方式
2. 创建新组件 `AmazonListingDynamicFormV2`，使用新的分组配置进行渲染
3. 解决嵌套属性（如数组索引 `item_name.0.value`）的匹配问题

---

## 一、统一的分组配置结构

### 1.1 新建文件

**文件路径**: `hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/components/AmazonFieldGroups.ts`

### 1.2 类型定义

```typescript
export interface FieldGroupRule {
  /** 字段指定方式 */
  type: 'exact' | 'prefix' | 'keyword';
  /** 匹配模式 */
  pattern: string;
}

export interface FieldGroupConfig {
  /** 分组展示名称 */
  name: string;
  /** 排序权重（数字越小越靠前） */
  order: number;
  /** 是否默认展开 */
  expanded?: boolean;
  /** 匹配规则（按优先级依次尝试：精确 > 前缀 > 关键字） */
  rules: FieldGroupRule[];
  /** 显式排除的字段 ID（不在此分组中显示） */
  excludePatterns?: string[];
}
```

### 1.3 分组配置示例

```typescript
export const AmazonFieldGroupsV2: FieldGroupConfig[] = [
  {
    name: '基本信息',
    order: 1,
    expanded: true,
    rules: [
      // 精确指定（最高优先级）
      { type: 'exact', pattern: 'item_name' },
      { type: 'exact', pattern: 'brand' },
      { type: 'exact', pattern: 'manufacturer' },
      { type: 'exact', pattern: 'part_number' },
      // 前缀匹配（处理数组索引，如 item_name.0.value, item_name.1.value）
      { type: 'prefix', pattern: 'item_name.' },
      { type: 'prefix', pattern: 'brand.' },
      // 关键字兜底
      { type: 'keyword', pattern: 'model_' },
      { type: 'keyword', pattern: 'country_of_origin' },
      { type: 'keyword', pattern: 'item_shape' },
    ],
    excludePatterns: []
  },
  {
    name: '变体属性',
    order: 2,
    rules: [
      { type: 'exact', pattern: 'variation_theme' },
      { type: 'prefix', pattern: 'variation_theme.' },
      { type: 'prefix', pattern: 'parentage' },
      { type: 'prefix', pattern: 'child_parent_sku' },
      { type: 'keyword', pattern: 'variation' },
    ]
  },
  {
    name: '产品标识',
    order: 3,
    rules: [
      { type: 'exact', pattern: 'externally_assigned_product_identifier' },
      { type: 'prefix', pattern: 'externally_assigned_product_identifier.' },
      { type: 'exact', pattern: 'supplier_declared_has_product_identifier_exemption' },
      { type: 'prefix', pattern: 'supplier_declared_has_product_identifier_exemption.' },
      { type: 'exact', pattern: 'merchant_suggested_asin' },
      { type: 'prefix', pattern: 'merchant_suggested_asin.' },
    ]
  },
  {
    name: '描述与关键字',
    order: 4,
    rules: [
      { type: 'exact', pattern: 'product_description' },
      { type: 'prefix', pattern: 'product_description.' },
      { type: 'exact', pattern: 'bullet_point' },
      { type: 'prefix', pattern: 'bullet_point.' },
      { type: 'keyword', pattern: 'description' },
      { type: 'keyword', pattern: 'bullet_point' },
      { type: 'keyword', pattern: 'generic_keyword' },
      { type: 'keyword', pattern: 'search_term' },
      { type: 'keyword', pattern: 'special_feature' },
    ]
  },
  {
    name: '图片资料',
    order: 5,
    rules: [
      { type: 'keyword', pattern: 'image' },
      { type: 'keyword', pattern: 'product_image' },
      { type: 'keyword', pattern: 'swatch' },
    ]
  },
  {
    name: '报价与销售',
    order: 6,
    rules: [
      { type: 'keyword', pattern: 'price' },
      { type: 'keyword', pattern: 'offer' },
      { type: 'keyword', pattern: 'purchasable' },
      { type: 'keyword', pattern: 'condition' },
      { type: 'keyword', pattern: 'fulfillment' },
      { type: 'keyword', pattern: 'lead_time' },
      { type: 'keyword', pattern: 'inventory_available' },
      { type: 'keyword', pattern: 'shipping_group' },
      { type: 'keyword', pattern: 'street_date' },
      { type: 'keyword', pattern: 'merchant_release_date' },
      { type: 'keyword', pattern: 'product_site_launch_date' },
    ]
  },
  {
    name: '尺寸与重量',
    order: 7,
    rules: [
      { type: 'keyword', pattern: 'dimension' },
      { type: 'keyword', pattern: 'weight' },
      { type: 'keyword', pattern: 'size' },
      { type: 'keyword', pattern: 'volume' },
      { type: 'keyword', pattern: 'capacity' },
      { type: 'keyword', pattern: 'item_length_width_height' },
    ]
  },
  {
    name: '合规与电池',
    order: 8,
    rules: [
      { type: 'keyword', pattern: 'battery' },
      { type: 'keyword', pattern: 'cell' },
      { type: 'keyword', pattern: 'compliance' },
      { type: 'keyword', pattern: 'regulation' },
      { type: 'keyword', pattern: 'warning' },
      { type: 'keyword', pattern: 'certification' },
      { type: 'keyword', pattern: 'supplier_declared_dg_hz_regulation' },
      { type: 'keyword', pattern: 'hazmat' },
      { type: 'keyword', pattern: 'gpsr' },
    ]
  },
  {
    name: '其他常规属性',
    order: 99,
    rules: [] // 默认兜底分组，无匹配规则
  },
];
```

---

## 二、匹配算法设计

### 2.1 核心匹配函数

```typescript
/**
 * 检查字段是否匹配指定分组
 * 匹配优先级：精确匹配 > 前缀匹配 > 关键字匹配
 */
const matchFieldToGroup = (fieldId: string, group: FieldGroupConfig): boolean => {
  const fieldIdLower = fieldId.toLowerCase();
  const rules = group.rules;
  
  // 1. 精确匹配 (fieldId === pattern)
  const exactMatch = rules
    .filter(r => r.type === 'exact')
    .some(r => fieldIdLower === r.pattern.toLowerCase());
  if (exactMatch) return true;
  
  // 2. 前缀匹配 (fieldId.startsWith(pattern))
  // 处理数组索引：如 item_name.0.value 匹配 item_name.
  const prefixMatch = rules
    .filter(r => r.type === 'prefix')
    .some(r => fieldIdLower.startsWith(r.pattern.toLowerCase()));
  if (prefixMatch) return true;
  
  // 3. 关键字匹配 (fieldId.includes(pattern))
  // 不区分大小写的包含匹配，作为兜底
  const keywordMatch = rules
    .filter(r => r.type === 'keyword')
    .some(r => fieldIdLower.includes(r.pattern.toLowerCase()));
  if (keywordMatch) return true;
  
  return false;
};

/**
 * 检查字段是否在排除列表中
 */
const isExcluded = (fieldId: string, excludePatterns: string[]): boolean => {
  const fieldIdLower = fieldId.toLowerCase();
  return excludePatterns.some(pattern => 
    fieldIdLower === pattern.toLowerCase() || 
    fieldIdLower.startsWith(pattern.toLowerCase() + '.') ||
    fieldIdLower.includes('.' + pattern.toLowerCase())
  );
};
```

### 2.2 分组分配逻辑

```typescript
const assignFieldToGroup = (fieldId: string, groups: FieldGroupConfig[]): FieldGroupConfig | null => {
  // 倒序遍历，分组 order 越小优先级越高
  for (const group of groups) {
    if (isExcluded(fieldId, group.excludePatterns || [])) {
      continue;
    }
    if (matchFieldToGroup(fieldId, group)) {
      return group;
    }
  }
  // 兜底到最后一个分组（其他常规属性）
  return groups[groups.length - 1] || null;
};
```

### 2.3 嵌套属性匹配注意事项

对于复杂属性（数组或嵌套对象），需要考虑以下情况：

| 字段 ID 示例 | 匹配模式 | 说明 |
|-------------|---------|------|
| `item_name.0.value` | `item_name.` (prefix) | 匹配数组元素 |
| `item_name.0` | `item_name.` (prefix) | 匹配数组索引节点 |
| `purchasable_offer.0.our_price.value` | `purchasable_offer.` (prefix) | 匹配嵌套对象深层属性 |
| `externally_assigned_product_identifier.0.type` | `externally_assigned_product_identifier.` (prefix) | 匹配对象子属性 |

---

## 三、新组件实现

### 3.1 文件结构

```
hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/components/
  ├── AmazonFieldGroups.ts          # 新增：分组配置
  ├── AmazonListingDynamicForm.vue  # 现有组件
  └── AmazonListingDynamicFormV2.vue # 新增：V2 组件
```

### 3.2 V2 组件核心差异

| 特性 | AmazonListingDynamicForm | AmazonListingDynamicFormV2 |
|------|--------------------------|---------------------------|
| 分组配置来源 | `AmazonGroups` (旧配置) | `AmazonFieldGroupsV2` (新配置) |
| 匹配方式 | 仅关键字匹配 | 精确 + 前缀 + 关键字 |
| 字段指定 | 无 | 支持显式指定字段归属 |
| excludePatterns | 无 | 支持排除模式 |

### 3.3 组件代码结构（关键部分）

```typescript
import { AmazonFieldGroupsV2, type FieldGroupConfig } from './AmazonFieldGroups';

// 分组字段计算
const groupedFieldsV2 = computed(() => {
  if (!config.value || !config.value.fields) return [];
  
  const fields = config.value.fields.filter((f: any) => !f.optional && !f.hidden);
  const groupBuckets: Record<string, any[]> = {};
  
  // 初始化分组桶
  AmazonFieldGroupsV2.forEach(g => {
    groupBuckets[g.name] = [];
  });
  
  // 分配字段到分组
  fields.forEach((field: any) => {
    const group = assignFieldToGroup(field.id, AmazonFieldGroupsV2);
    if (group && groupBuckets[group.name]) {
      groupBuckets[group.name].push(field);
    }
  });
  
  // 按 order 排序并过滤空分组
  return AmazonFieldGroupsV2
    .filter(g => groupBuckets[g.name]?.length > 0)
    .map(g => ({
      name: g.name,
      expanded: g.expanded,
      fields: groupBuckets[g.name].sort((a, b) => {
        const orderA = a.order ?? 999;
        const orderB = b.order ?? 999;
        return orderA - orderB;
      })
    }));
});
```

---

## 四、配置扩展性

### 4.1 未来可扩展方向

1. **分组嵌套** — 支持二级分组（如基本信息下分"品牌信息"、"产品信息"）
2. **可视化配置** — 通过 JSON 编辑器或 UI 界面管理分组规则
3. **场景化配置** — 不同产品类型使用不同分组方案
4. **动态分组** — 后端返回分组配置，前端动态加载

### 4.2 配置校验

```typescript
/**
 * 校验分组配置的合法性
 */
export const validateFieldGroups = (groups: FieldGroupConfig[]): string[] => {
  const errors: string[] = [];
  
  // 检查 order 是否有重复
  const orders = groups.map(g => g.order);
  const duplicates = orders.filter((o, i) => orders.indexOf(o) !== i);
  if (duplicates.length > 0) {
    errors.push(`分组 order 存在重复: ${duplicates.join(', ')}`);
  }
  
  // 检查是否有空分组名
  groups.forEach((g, i) => {
    if (!g.name?.trim()) {
      errors.push(`第 ${i + 1} 个分组的 name 不能为空`);
    }
  });
  
  return errors;
};
```

---

## 五、实施计划

### 5.1 阶段一：配置层
- [ ] 创建 `AmazonFieldGroups.ts` 文件
- [ ] 定义类型接口 `FieldGroupRule`, `FieldGroupConfig`
- [ ] 实现 `AmazonFieldGroupsV2` 分组配置
- [ ] 实现匹配函数 `matchFieldToGroup`, `assignFieldToGroup`

### 5.2 阶段二：组件层
- [ ] 创建 `AmazonListingDynamicFormV2.vue`
- [ ] 复用 `AmazonAttributeItem.vue`, `AmazonPurchasableOffer.vue`
- [ ] 替换分组逻辑为新的 `AmazonFieldGroupsV2`
- [ ] 实现可选字段的动态添加/移除

### 5.3 阶段三：测试与优化
- [ ] 测试各种字段 ID 格式的匹配正确性
- [ ] 验证嵌套属性（数组、对象）的匹配
- [ ] 对比旧组件与新组件的分组结果
- [ ] 性能优化（如需要）

---

## 六、附录

### 6.1 字段 ID 格式参考

从 `example_resp.json` 分析，字段 ID 常见格式：

| 格式 | 示例 | 说明 |
|------|------|------|
| 简单属性 | `color` | 基本属性 |
| 带数组索引 | `item_name.0.value` | 数组中第一个元素的值 |
| 带嵌套对象 | `externally_assigned_product_identifier.0.type` | 嵌套对象的 type 属性 |
| 多层级嵌套 | `purchasable_offer.0.our_price.value` | 多层嵌套 |

### 6.2 匹配优先级说明

```
1. 精确匹配 (exact)
   - fieldId === pattern
   - 例: "item_name" === "item_name" ✓

2. 前缀匹配 (prefix)  
   - fieldId.startsWith(pattern)
   - 例: "item_name.0.value".startsWith("item_name.") ✓

3. 关键字匹配 (keyword)
   - fieldId.includes(pattern)
   - 例: "brand_manufacturer_info.0.value".includes("brand") ✓
```
