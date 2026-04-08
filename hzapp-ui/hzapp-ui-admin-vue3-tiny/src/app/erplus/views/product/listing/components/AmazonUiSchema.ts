export interface AmazonUiFieldConfig {
  /** 排序权重，数字越小越排在前面 */
  order?: number;
  /** 栅格占据的列数，总分 24。比如 12 即为占据半行 */
  span?: number;
  /** 强制覆盖的组件类型，例如 textarea, date-picker, time-picker */
  uiWidget?: string;
  /** 覆盖原始亚马逊晦涩标题，显示更为友好的名称 */
  label?: string;
  /** 输入框提示语 */
  placeholder?: string;
  /** 额外的字段问号提示说明 */
  tooltip?: string;
}

export interface AmazonGroupConfig {
  /** 分组展示名称 */
  name: string;
  /** 用于匹配字段 ID 的关键字列表（不区分大小写） */
  match: string[];
}

/**
 * 亚马逊属性分组配置
 * 按照定义的顺序进行匹配，匹配到第一个即停止
 */
export const AmazonGroups: AmazonGroupConfig[] = [
  { name: '基本信息', match: ['item_name', 'brand', 'manufacturer', 'part_number', 'model_', 'country_of_origin', 'subject_code', 'color', 'material', 'item_shape', 'style', 'metal_type', 'handmade_classification'] },
  { name: '产品标识', match: ['externally_assigned_product_identifier', 'supplier_declared_has_product_identifier_exemption', 'merchant_suggested_asin'] },
  { name: '描述与关键字', match: ['description', 'bullet_point', 'generic_keyword', 'search_term', 'feature', 'intended_use', 'target_audience', 'flavor', 'pattern', 'orientation', 'special_feature'] },
  { name: '图片资料', match: ['image', 'product_image'] },
  { name: '报价与销售', match: ['price', 'offer', 'purchasable', 'condition', 'fulfillment', 'lead_time', 'inventory_available', 'shipping_group', 'max_order_quantity', 'gift_options', 'list_price', 'product_tax_code', 'skip_offer', 'map_policy', 'street_date', 'merchant_release_date', 'product_site_launch_date'] },
  { name: '尺寸与重量', match: ['dimension', 'weight', 'size', 'volume', 'capacity', 'item_length_width_height'] },
  { name: '变体属性', match: ['variation', 'parentage'] },
  { name: '合规与电池', match: ['battery', 'batteries', 'cell', 'compliance', 'regulation', 'warning', 'certification', 'supplier_declared_dg_hz_regulation', 'safety', 'hazmat', 'gpsr', 'dsa', 'responsible_party', 'attestation', 'is_this_product_subject_to_buyer_age_restrictions'] },
  { name: '其他常规属性', match: [] } // 默认兜底分组
];

/**
 * 报价与销售模块的白名单字段
 * 这些字段即使不是必填，也会在第一列次优先显示，不被标记为 optional
 */
export const AmazonPriceWhitelist = [
  'purchasable_offer', 'condition_type', 'fulfillment_channel_code', 
  'merchant_shipping_group', 'fulfillment_availability', 'quantity', 'price'
];

/** 产品标识模块核心显示字段 (即使不是必填也显示) */
export const AmazonIdentifierWhitelist = [
  'supplier_declared_has_product_identifier_exemption',
  'externally_assigned_product_identifier'
];

/**
 * 报价与销售模块的黑名单
 * 这些子字段通常在特定场景下才需要且会导致视觉干扰，默认标记为 optional
 */
export const AmazonPriceBlacklist = [
  'discounted_price', 'minimum_advertised_price', 'sale_price', 'offer_end', 
  'offer_start', 'stops_selling', 'pricing_action', 'national_price', 'business_price'
];

/** 识别报价与销售模块的关键词 */
export const AmazonPriceOfferKeywords = ['price', 'offer', 'purchasable', 'condition', 'fulfillment'];

/** purchasable_offer 子字段前缀 — 这些字段由 AmazonPurchasableOffer 组件单独管理 */
export const PURCHASABLE_OFFER_PREFIX = 'purchasable_offer.';

/** purchasable_offer 中在创建时不显示的字段 (由日期联动自动设值或后续功能管理) */
export const PURCHASABLE_OFFER_HIDDEN_FIELDS = [
  'start_at',   // 报价生效日期 → 自动同步 merchant_release_date
  'end_at',     // 报价停售日期 → 创建时不设置
  'audience',   // 受众 → 隐藏字段，默认 ALL
  'automated_pricing_merchandising_rule_plan', // 自动定价规则
];

/**
 * 亚马逊前端展示专用 Schema 映射配置层
 * 主要是针对核心字段（标题、SKU、价格相关）做精细化排版与体验调优
 * 支持基于前缀的匹配，比如 purchasable_offer.our_price
 */
export const AmazonUiSchema: Record<string, AmazonUiFieldConfig> = {
  // --- 基本信息模块 ---
  'item_name': { 
    order: 1, 
    span: 24, 
    uiWidget: 'textarea', 
    placeholder: '推荐首字母大写，包含关键特征，由于平台限制，建议控制在200字符内' 
  },
  'brand': { order: 2, span: 12, label: '品牌名称' },
  'manufacturer': { order: 3, span: 12, label: '制造商' },
  'part_number': { order: 4, span: 12, label: '制造商零件编号' },
  'model_': { order: 5, span: 12, label: '型号' },
  'model_number': { order: 6, span: 12, label: '型号' },
  'model_name': { order: 7, span: 12, label: '型号名称' },
  'item_shape': { label: '形状' },
  'style': { label: '风格' },
  'material': { label: '材质' },
  'color': { label: '颜色' },
  'size': { label: '尺寸' },
  'metal_type': { label: '金属种类' },

  // --- 描述与关键字模块 ---
  'description': { order: 1, span: 24, uiWidget: 'textarea', label: '产品描述' },
  'product_description': { order: 1, span: 24, uiWidget: 'textarea', label: '产品描述' },
  'bullet_point': { order: 2, span: 24, label: '五点描述' },
  'generic_keyword': { order: 3, span: 24, label: '搜索关键词' },
  'special_feature': { label: '特殊功能' },

  // --- 报价与销售模块 ---
  'condition_type': { order: 10, span: 12, label: '物品状况' },
  'fulfillment_channel_code': { order: 11, span: 12, label: '发货渠道' },
  'merchant_shipping_group': { order: 12, span: 12, label: '运费模板' },
  'is_inventory_available': { order: 13, span: 12, label: '始终有货' },
  'quantity': { order: 14, span: 12, label: '库存数量' },
  'lead_time_to_ship_max_days': { order: 15, span: 12, label: '处理时间' },
  // 日期字段 — 归入报价与销售组
  'product_site_launch_date': { order: 20, span: 12, uiWidget: 'date-picker', label: '上架日期', tooltip: '商品在亚马逊上可见可搜索的日期' },
  'merchant_release_date': { order: 21, span: 12, uiWidget: 'date-picker', label: '发售日期', tooltip: '卖家设定的可出售日期，之前可见但无法购买' },
  'street_date': { order: 22, span: 12, uiWidget: 'date-picker', label: '首次可发货日期' },
  
  // --- 产品标识 ---
  'supplier_declared_has_product_identifier_exemption': { order: 1, span: 24, label: '是否免除外部产品ID' },
  'externally_assigned_product_identifier': { order: 2, label: '产品 ID (UPC/EAN)' },
  'merchant_suggested_asin': { order: 3, span: 12, label: '建议 ASIN' },

  // --- 合规与电池 ---
  'batteries_required': { label: '需要电池', order: 1 },
  'batteries_included': { label: '附带电池', order: 2 },
  'supplier_declared_dg_hz_regulation': { label: '危险品法规', order: 3 },
  'hazmat': { label: '危险品合规' },
  'gpsr_safety_attestation': { label: 'GPSR 安全认证' },
  'gpsr_manufacturer_reference': { label: 'GPSR 制造商参考' },
  'dsa_responsible_party_address': { label: '负责人电子地址(GPSR)' },
  'compliance_media': { label: '合规资料/文档' },

  // --- 其他核心属性 ---
  'item_length_width_height': { label: '商品尺寸 (长x宽x高)', order: 1 },
  'recommended_browse_nodes': { label: '推荐分类节点' },
  'main_product_image_locator': { label: '主图链接' },
  'swatch_product_image_locator': { label: '样本图片链接' },

  // 其他日期组件重写
  'start_at': { uiWidget: 'date-picker', span: 12 },
  'end_at': { uiWidget: 'date-picker', span: 12 },
  'restock_date': { uiWidget: 'date-picker', span: 12 },
  'offering_release_date': { uiWidget: 'date-picker', span: 12 },
  'sell_end_date': { uiWidget: 'date-picker', span: 12 },
  'product_site_launch_date_alt': { uiWidget: 'date-picker', span: 12 }
};

/**
 * 根据 Field ID 获取对应的 UI 配置
 * 支持精确匹配和长尾嵌套字段后缀匹配 (例如 start_at 其实是在 schedule[0].start_at 里面)
 * 
 * 匹配策略:
 * 1. 精确匹配: fieldId === key  →  返回完整配置
 * 2. 后缀匹配: fieldId 以 .key 结尾或包含 .key.  →  返回完整配置
 * 3. 前缀匹配: fieldId 以 key. 开头  →  仅返回布局属性 (span/order)，不传递 label/uiWidget
 *    原因: 父级 key (如 purchasable_offer) 的 label 不应覆盖子字段的原始标题
 */
export const getUiConfigForField = (fieldId: string): AmazonUiFieldConfig => {
  // 1. 精确匹配 — 最高优先级
  if (AmazonUiSchema[fieldId]) {
    return AmazonUiSchema[fieldId];
  }
  
  // 按 key 长度降序排序，优先匹配更具体的配置
  const sortedKeys = Object.keys(AmazonUiSchema).sort((a, b) => b.length - a.length);
  
  for (const key of sortedKeys) {
    const config = AmazonUiSchema[key];
    
    // 2. 后缀/中间匹配 — fieldId 的尾部包含 key (例如 schedule.0.start_at 匹配 start_at)
    if (fieldId.endsWith('.' + key) || fieldId.includes('.' + key + '.')) {
      return config;
    }
    
    // 3. 前缀匹配 — fieldId 以 key. 开头 (例如 purchasable_offer.0.xxx 匹配 purchasable_offer)
    //    继承布局属性，如果是 .value 子字段则继承 label
    if (fieldId.startsWith(key + '.')) {
      const inherited: AmazonUiFieldConfig = {};
      if (config.span) inherited.span = config.span;
      if (config.order !== undefined) inherited.order = config.order;
      
      // 特殊逻辑: 如果子字段是 .value，且父级定义了 label，则继承该 label
      // 避免 type/unit 等字段也被打上同样的父级 label
      if (config.label && (fieldId.endsWith('.value'))) {
        inherited.label = config.label;
      }
      return inherited;
    }
  }
  
  return {};
};
