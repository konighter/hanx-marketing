  ## 核心痛点

  ### 1. 单体解析器 — 不可扩展

  AmazonListingSchemaService 在一个类里做了四件事：

  - 递归打平 JSON Schema properties
  - 判断字段结构类型 (LEAF/TRUE_ARRAY/NESTED)
  - 解析 if/then/allOf 联动规则
  - 生成 UI Widget 提示（如 isAmazonWrapper、valuePath）

  不同 productType 的 Schema 差异巨大（如 ABIS_BOOK 只有 ~30 字段，3D_PRINTER 有 ~200+），但所有逻辑走同一条路径，靠硬编码白名单（IMPORTANT_FIELDS_WHITELIST、
  TIME_INDICATOR_FIELDS、VALUE_INDICATOR_FIELDS）做特殊处理。

  ### 2. 打平策略过于激进 — 前端脆弱

  当前策略将所有嵌套结构打平为点分路径（如 purchasable_offer.0.our_price.0.schedule.0.value_with_tax），前端需要硬编码 AmazonPurchasableOffer.vue 做反向映射。新增 productType 如
  果有类似的深层嵌套，就必须写新的专用组件。

  ### 3. 静态 UI 配置 — 无法按 productType 差异化

  AmazonUiSchema.ts 是全局唯一的静态配置。AmazonGroupingConfig.ts 的分组也是写死的。不同 productType 应有不同的：

  - 分组策略（如 LUGGAGE 需要 "材质与尺寸" 组，BOOK 不需要）
  - 字段排序与默认折叠状态
  - 特定字段的 Widget 覆盖

  ### 4. 联动规则前后端双重维护

  后端 AmazonListingSchemaService 从 if/then 解析出 LogicExpressionVO，前端 AmazonListingDynamicFormV2.vue 还额外硬编码了 FBA/FBM、GTIN Exemption 的联动逻辑。两套规则容易不一致。

  ### 5. Mapping 注册方式不可扩展

  AmzAttributeSchemaMappingService 用 Map.of() 硬编码了 3 个 mapper，AmzAttributeMappingService 硬编码了 8 个。新增属性映射必须改代码+重新部署。没有利用 Spring 自动发现机制。

  ### 6. 无缓存 — 重复解析大 Schema

  generateFormConfig() 每次请求都从 DB 读取原始 JSON Schema 并重新递归解析。一个 ~200KB 的 Schema 解析开销不小。

  ———

  ## 重构方案：基于 productType 的分层动态渲染架构

  ### 总体设计

  ┌────────────────────────────────────────────────────────────────┐
  │                     前端 (DynamicForm V3)                      │
  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────┐ │
  │  │ SchemaStore  │  │ WidgetRegistry│  │ LinkageEngine      │ │
  │  │ (响应式状态) │  │ (组件注册表) │  │ (统一联动引擎)     │ │
  │  └──────┬──────┘  └──────┬───────┘  └──────────┬──────────┘ │
  │         │                │                      │            │
  │  ┌──────▼────────────────▼──────────────────────▼──────────┐ │
  │  │              AttributeRenderer (递归组件)               │ │
  │  └─────────────────────────┬──────────────────────────────┘ │
  └────────────────────────────┼─────────────────────────────────┘
                               │ GET /erplus/amz/listing/schema?productType=XX
  ┌────────────────────────────▼─────────────────────────────────┐
  │                     后端 API Layer                             │
  │  AmzListingController → ListingSchemaFacade                  │
  └────────────────────────────┬─────────────────────────────────┘
                               │
  ┌────────────────────────────▼─────────────────────────────────┐
  │               Listing Schema Pipeline (四阶段)              │
  │                                                              │
  │  Stage 1          Stage 2          Stage 3         Stage 4   │
  │  SchemaLoad  →  PropertyFlatten  →  RuleResolve  →  UiMerge │
  │  (DB/Cache)    (Strategy)          (Visitor)       (Overlay) │
  └──────────────────────────────────────────────────────────────┘

  ### 阶段 1：SchemaLoad — Schema 获取与缓存

  @Service
  public class ListingSchemaLoader {

      @Resource
      private SystemMetaCategoryService metaCategoryService;

      // LRU Cache: productType → parsed JsonNode
      private final Cache<String, JsonNode> schemaCache = Caffeine.newBuilder()
              .maximumSize(64)
              .expireAfterWrite(Duration.ofHours(6))
              .build();

      public JsonNode loadSchema(CrossPlatformEnum platform, String productType) {
          return schemaCache.get(productType, pt -> {
              CrossMetaCategoryModel model = metaCategoryService
                  .getCrossMetaCategoryByPlatformCategoryCode(platform, pt);
              if (model == null || StrUtil.isEmpty(model.getExtra())) {
                  throw new SchemaNotFoundException(pt);
              }
              return JsonUtils.parseTree(model.getExtra());
          });
      }

      public void invalidate(String productType) {
          schemaCache.invalidate(productType);
      }
  }

  收益：避免每次请求重复解析 ~200KB 的 JSON 字符串；AmazonSchemaSyncTask 写入 DB 后调用 invalidate() 即可。

  ### 阶段 2：PropertyFlatten — 可替换的打平策略

  将打平逻辑从单体中拆出，按 productType 选择策略：

  public interface PropertyFlattenStrategy {
      /** 判断是否支持此 productType */
      boolean supports(String productType);
      /** 执行打平，输出字段列表 */
      List<AmzListingFormFieldVO> flatten(JsonNode schemaRoot, FlattenContext ctx);
  }

  @Data
  public class FlattenContext {
      private String productType;
      private Map<String, String> fieldMapping;
      private Set<String> importantFields;   // 可按 productType 定制
  }

  默认策略（适用于大多数 productType）：

  @Service
  @Order(Ordered.LOWEST_PRECEDENCE)
  public class DefaultPropertyFlattenStrategy implements PropertyFlattenStrategy {

      @Override
      public boolean supports(String productType) { return true; } // 兜底

      @Override
      public List<AmzListingFormFieldVO> flatten(JsonNode schemaRoot, FlattenContext ctx) {
          List<AmzListingFormFieldVO> fields = new ArrayList<>();
          flattenProperties("", schemaRoot, fields, ctx.getFieldMapping(), true);
          return fields;
      }

      // ... 当前 AmazonListingSchemaService 中的递归逻辑迁移至此
  }

  产品类型专属策略（例如 purchasable_offer 嵌套特别深的类型）：

  @Service
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public class ComplexOfferFlattenStrategy implements PropertyFlattenStrategy {

      private static final Set<String> COMPLEX_OFFER_TYPES = Set.of(
          "3D_PRINTER", "CONSUMER_ELECTRONICS", "LUGGAGE"
      );

      @Override
      public boolean supports(String productType) {
          return COMPLEX_OFFER_TYPES.contains(productType);
      }

      @Override
      public List<AmzListingFormFieldVO> flatten(JsonNode schemaRoot, FlattenContext ctx) {
          // 使用更保守的打平深度，保留 purchasable_offer 为 NESTED 对象
          // 而非激进打平到 .0.our_price.0.schedule.0.value_with_tax
      }
  }

  策略选择器：

  @Service
  public class PropertyFlattenStrategyResolver {
      @Resource
      private List<PropertyFlattenStrategy> strategies; // Spring 自动注入

      public PropertyFlattenStrategy resolve(String productType) {
          return strategies.stream()
                  .filter(s -> s.supports(productType))
                  .findFirst()
                  .orElseThrow();
      }
  }

  收益：不同 productType 可有不同打平深度；新增 productType 只需加一个 Strategy 类；主流程不再有 if (id.contains("purchasable_offer")) 式的硬编码分支。

  ### 阶段 3：RuleResolve — 联动规则统一解析（Visitor 模式）

  将 if/then/allOf 解析从打平逻辑中完全分离，用 Visitor 遍历 Schema：

  public interface SchemaRuleVisitor {
      /** 处理 if/then 条件块 */
      void visitIfThen(JsonNode ifSchema, JsonNode thenSchema, String parentPrefix);
      /** 处理属性约束（enum/const/required 等） */
      void visitPropertyConstraint(String fieldId, JsonNode constraint);
  }

  @Service
  public class LinkageRuleResolver {

      private final List<SchemaRuleVisitor> visitors;

      public LinkageRuleResolver(List<SchemaRuleVisitor> visitors) {
          this.visitors = visitors;
      }

      public void resolve(JsonNode schemaRoot, List<AmzListingFormFieldVO> fields) {
          traverseForRules("", schemaRoot, fields);
      }

      private void traverseForRules(String prefix, JsonNode node, List<AmzListingFormFieldVO> fields) {
          // 1. 处理 allOf 中的 if/then
          JsonNode allOf = node.get("allOf");
          if (allOf != null && allOf.isArray()) {
              for (JsonNode sub : allOf) {
                  JsonNode ifNode = sub.get("if");
                  JsonNode thenNode = sub.get("then");
                  if (ifNode != null && thenNode != null) {
                      visitors.forEach(v -> v.visitIfThen(ifNode, thenNode, prefix));
                  }
              }
          }
          // 2. 递归子属性
          JsonNode properties = node.get("properties");
          if (properties != null) {
              // ... 递归
          }
      }
  }

  具体 Visitor 实现（将前端的硬编码联动统一到后端）：

  @Service
  @Order(1)
  public class IfThenRuleVisitor implements SchemaRuleVisitor {
      @Override
      public void visitIfThen(JsonNode ifSchema, JsonNode thenSchema, String parentPrefix) {
          // 将 if 条件解析为 LogicExpressionVO
          // 将 then 块解析为 visibility/requirement 规则
          // 输出到对应 AmzListingFormFieldVO.linkageRules
      }
  }

  @Service
  @Order(2)
  public class FulfillmentLinkageVisitor implements SchemaRuleVisitor {
      @Override
      public void visitIfThen(JsonNode ifSchema, JsonNode thenSchema, String parentPrefix) {
          // 特殊处理 FBA/FBM 联动
      }

      @Override
      public void visitPropertyConstraint(String fieldId, JsonNode constraint) {
          // 特殊处理 fulfillment_channel_code → merchant_shipping_group 等
      }
  }

  收益：

  - 后端统一输出所有联动规则（含 FBA/FBM、GTIN Exemption 等），前端不再硬编码
  - 新增联动逻辑只需加 Visitor
  - 前端联动引擎只需执行后端输出的 LogicExpressionVO，不再需要 new Function() 式的字符串 eval

  ### 阶段 4：UiMerge — UI 配置按 productType 覆盖

  后端：将 AmazonUiSchema.ts 的静态配置改造为可按 productType 加载的 Overlay 体系：

  @Data
  public class UiOverlay {
      private String productType;       // null = 全局默认
      private Map<String, FieldUiConfig> fieldConfigs;
  }

  @Data
  public class FieldUiConfig {
      private Integer order;
      private Integer span;
      private String uiWidget;
      private String label;
      private String placeholder;
      private String tooltip;
      private String groupName;           // 字段所属分组
      private Boolean defaultCollapsed;   // 默认是否折叠
      private DependsOnConfig dependsOn;
  }

  存储方式：在 DB 中增加 erplus_amz_listing_ui_overlay 表，或直接用 JSON 文件放在 classpath 下：

  // classpath: listing-ui-overlays/3D_PRINTER.json
  {
    "productType": "3D_PRINTER",
    "fieldConfigs": {
      "purchasable_offer": { "groupName": "Offer & Sales", "order": 30 },
      "item_package_weight": { "groupName": "Dimensions & Weight", "order": 50 },
      "print_technology": { "groupName": "3D Printing Specific", "order": 10, "uiWidget": "select" }
    }
  }

  合并逻辑：

  @Service
  public class UiOverlayMerger {

      private final Map<String, UiOverlay> overlayMap = new ConcurrentHashMap<>();

      @PostConstruct
      public void loadOverlays() {
          // 从 DB 或 classpath JSON 加载
      }

      public void merge(String productType, List<AmzListingFormFieldVO> fields) {
          // 1. 应用全局默认 overlay
          applyOverlay(overlayMap.get(null), fields);
          // 2. 应用 productType 特定 overlay（覆盖全局）
          applyOverlay(overlayMap.get(productType), fields);
      }
  }

  收益：

  - 不同 productType 可有不同分组名称、字段排序、Widget 覆盖
  - 运营/产品可通过修改 JSON 配置调整 UI，无需发版
  - 前端 AmazonUiSchema.ts 的 130+ 行静态 Map 可迁移到后端，前端变为纯渲染

  ### 前端重构：DynamicForm V3

  #### 核心变更

  1. SchemaStore — 响应式状态中心

  // composables/useListingSchema.ts
  export function useListingSchema(productType: Ref<string>) {
    const schema = ref<AmzListingFormConfigVO | null>(null)
    const loading = ref(false)
    const visibilityMap = reactive<Record<string, boolean>>({})
    const requirementMap = reactive<Record<string, boolean>>({})

    async function loadSchema() {
      loading.value = true
      try {
        schema.value = await request.get('/erplus/amz/listing/schema', {
          params: { productType: productType.value }
        })
        initVisibility()
      } finally {
        loading.value = false
      }
    }

    // 统一联动引擎：只消费后端输出的 LogicExpressionVO
    function applyLinkageRules(attributes: Record<string, any>) {
      schema.value?.fields.forEach(field => {
        field.linkageRules?.forEach(rule => {
          const result = evaluateLogicExpression(rule.conditionLogic, attributes)
          if (rule.type === 'visibility') {
            visibilityMap[field.id] = rule.action === 'show' ? result : !result
          } else if (rule.type === 'requirement') {
            requirementMap[field.id] = rule.action === 'required' ? result : !result
          }
        })
      })
    }

    watch(productType, loadSchema)
    return { schema, loading, visibilityMap, requirementMap, applyLinkageRules }
  }

  2. WidgetRegistry — 替代硬编码组件 Map

  // registry/widgetRegistry.ts
  const widgetMap = new Map<string, Component>()

  export function registerWidget(type: string, component: Component) {
    widgetMap.set(type, component)
  }

  export function resolveWidget(field: AmzListingFormFieldVO): Component {
    // 1. 优先使用后端指定的 uiWidget
    if (field.uiWidget && widgetMap.has(field.uiWidget)) {
      return widgetMap.get(field.uiWidget)!
    }
    // 2. 按类型推导
    const typeMap: Record<string, string> = {
      'string': 'input', 'number': 'input-number', 'integer': 'input-number',
      'array': 'array-editor', 'object': 'object-editor',
      'enum': 'select'
    }
    return widgetMap.get(typeMap[field.type] || 'input')!
  }

  // 注册内置组件
  registerWidget('input', ElInput)
  registerWidget('select', ElSelect)
  registerWidget('textarea', ElInput)     // ElInput type=textarea
  registerWidget('date-picker', ElDatePicker)
  registerWidget('input-number', ElInputNumber)
  registerWidget('array-editor', ArrayEditor)
  registerWidget('object-editor', ObjectEditor)
  registerWidget('purchasable-offer', PurchasableOfferWidget) // 特殊组件

  3. AttributeRenderer V3 — 递归组件统一渲染

  <!-- AttributeRendererV3.vue -->
  <template>
    <component
      :is="resolveWidget(field)"
      v-bind="fieldProps"
      v-model="modelValue"
      @change="handleChange"
    />
  </template>

  <script setup lang="ts">
  // fieldProps 根据 field.extra/field.uiWidget 计算属性
  // 不再需要 300+ 行的 template if/else 分支
  </script>

  4. 移除前端硬编码联动

  将 AmazonListingDynamicFormV2.vue 中 50+ 行的 FBA/FBM、GTIN Exemption 联动逻辑迁移到后端 FulfillmentLinkageVisitor + IdentifierExemptionVisitor。前端只保留 applyLinkageRules()
  统一执行。

  ### Mapping 层重构：Spring 自动发现

  // 替代 Map.of() 硬编码
  public interface AttributeSchemaMapper {
      String getAttributeCode();
      void mapAttributeSchema(CategoryAttributeModel model, ProductTypeSchemaItem schemaItem);
  }

  @Service
  public class AmzAttributeSchemaMappingService {
      private final Map<String, AttributeSchemaMapper> mapperMap;

      public AmzAttributeSchemaMappingService(List<AttributeSchemaMapper> mappers) {
          this.mapperMap = mappers.stream()
              .collect(Collectors.toMap(AttributeSchemaMapper::getAttributeCode, Function.identity()));
      }
  }

  同样改造 AmzAttributeMappingService。

  收益：新增 Mapper 只需实现接口 + @Component，无需修改注册代码。

  ———

  ## 迁移路径

  | 阶段 | 改动 | 影响范围 | 风险 |
  |---|---|---|---|
  | Phase 1 | 拆分 AmazonListingSchemaService → 4 阶段 Pipeline | 后端 amz-biz | 低：输出 VO 结构不变 |
  | Phase 2 | 加 Caffeine 缓存 + SchemaLoader | 后端 amz-biz | 低：纯性能优化 |
  | Phase 3 | Mapping 层改 Spring 自动发现 | 后端 amz-biz | 低：行为等价 |
  | Phase 4 | 引入 UiOverlay 表/API，前端从 AmazonUiSchema.ts 迁移到后端 overlay | 后端 + 前端 | 中：需同步修改前端 |
  | Phase 5 | 前端联动规则统一消费后端 LogicExpressionVO，移除 amzLinkage.ts 字符串 eval | 前端 | 中：需补全后端 Visitor |
  | Phase 6 | 前端 DynamicFormV3 + WidgetRegistry + SchemaStore | 前端 | 高：UI 重写，需回归测试 |

  ### Phase 1 详细类图

  ListingSchemaFacade
    ├── ListingSchemaLoader          (Phase 2: Cache)
    ├── PropertyFlattenStrategyResolver
    │     ├── DefaultPropertyFlattenStrategy
    │     ├── ComplexOfferFlattenStrategy
    │     └── (未来: xxxProductTypeStrategy)
    ├── LinkageRuleResolver
    │     ├── IfThenRuleVisitor
    │     ├── FulfillmentLinkageVisitor
    │     ├── IdentifierExemptionVisitor
    │     └── (未来: 更多 Visitor)
    └── UiOverlayMerger
          ├── 全局 overlay (classpath JSON)
          └── productType overlay (DB/JSON)

  Facade 入口：

  @Service
  public class ListingSchemaFacade {

      @Resource private ListingSchemaLoader schemaLoader;
      @Resource private PropertyFlattenStrategyResolver flattenResolver;
      @Resource private LinkageRuleResolver ruleResolver;
      @Resource private UiOverlayMerger uiMerger;

      public AmzListingFormConfigVO generateFormConfig(String productType) {
          // Stage 1: Load
          JsonNode schemaRoot = schemaLoader.loadSchema(AMAZON, productType);

          // Stage 2: Flatten
          FlattenContext ctx = new FlattenContext(productType);
          List<AmzListingFormFieldVO> fields = flattenResolver
              .resolve(productType)
              .flatten(schemaRoot, ctx);

          // Stage 3: Rules
          ruleResolver.resolve(schemaRoot, fields);

          // Stage 4: UI Overlay
          uiMerger.merge(productType, fields);

          // Assemble
          AmzListingFormConfigVO config = new AmzListingFormConfigVO();
          config.setProductType(productType);
          config.setFields(fields);
          config.setFieldMapping(ctx.getFieldMapping());
          return config;
      }
  }

  ———

  ## 总结

  | 痛点 | 重构方案 | 核心收益 |
  |---|---|---|
  | 单体解析器 | 四阶段 Pipeline + Strategy/Visitor 模式 | 每阶段可独立扩展和测试 |
  | 激进打平 | 按 productType 可选打平策略 | 前端不再需要逆向映射组件 |
  | 静态 UI 配置 | UiOverlay 按 productType 覆盖 | 运营可配置，无需发版 |
  | 前后端双重联动 | 后端 Visitor 统一输出规则，前端纯执行 | 单一事实来源，一致性保证 |
  | 硬编码 Mapper 注册 | Spring 自动发现 | 加 Mapper 零改动注册代码 |
  | 无缓存 | Caffeine LRU + 同步任务触发失效 | 请求响应提速，DB 压力降低 |
  | 前端 300 行 if/else 渲染 | WidgetRegistry + 递归组件 | 新字段类型只需注册组件 |

  该方案保持了现有 AmzListingFormConfigVO 的对外契约不变，Phase 1-3 为纯后端重构，前端无感知；Phase 4-6 逐步将配置与联动逻辑迁移到后端驱动，最终实现前端 "拿到 schema 即可渲染" 的
  声明式动态表单。