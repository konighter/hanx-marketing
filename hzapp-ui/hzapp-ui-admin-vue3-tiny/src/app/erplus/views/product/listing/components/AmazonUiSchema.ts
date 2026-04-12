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
  /** 
   * 字段依赖规则：当指定字段的值满足条件时，当前字段才显示
   * - field: 依赖的字段 ID
   * - value: 目标值（当依赖字段等于该值时显示）
   * - negate: 为 true 时反转逻辑（当依赖字段不等于该值时显示）
   */
  dependsOn?: {
    field: string;
    value: any;
    negate?: boolean;
  };
}

/**
 * 亚马逊前端展示专用 Schema 映射配置层
 * 主要是针对核心字段（标题、SKU、价格相关）做精细化排版与体验调优
 * 支持基于前缀的匹配，比如 purchasable_offer.our_price
 */
export const AmazonUiSchema: Record<string, AmazonUiFieldConfig> = {
  // === 描述与关键字模块 (排在第一组) ===
  'product_description': { order: 1, span: 24, uiWidget: 'textarea', placeholder: 'Detailed description of product features, materials, dimensions, etc.' },
  'bullet_point': { order: 3, span: 24 },
  'generic_keyword': { order: 4, span: 24, placeholder: 'Separate multiple keywords with spaces, do not repeat words from product title' },
  'special_feature': { span: 24 },
  'target_audience_keyword': { span: 24 },
  'subject_keyword': { span: 24 },
  'platinum_keyword': { span: 24 },

  // === 基本信息模块 ===
  'item_name': { 
    order: 1, 
    span: 24, 
    uiWidget: 'textarea', 
    placeholder: 'Recommend title-case, include key features, suggest under 200 characters' 
  },
  'brand': { order: 2, span: 24 },
  'manufacturer': { order: 3, span: 24 },
  'part_number': { order: 4, span: 24 },
  'model_': { order: 5, span: 24 },
  'model_number': { order: 6, span: 24 },
  'model_name': { order: 7, span: 24 },
  'main_product_image_locator': { span: 24 },
  'swatch_product_image_locator': { span: 24 },
  'other_product_image_locator': { span: 24 },

  // === 常用属性简称 ===
  'country_of_origin': { span: 24 },
  'item_shape': { span: 24 },
  'style': { span: 24 },
  'material': { span: 24 },
  'color': { span: 24 },
  'size': { span: 24 },
  'pattern': { span: 24 },
  'theme': { span: 24 },
  'finish_type': { span: 24 },
  'item_form': { span: 24 },
  'item_type_keyword': { span: 24 },
  'metal_type': { span: 24 },
  'recommended_browse_nodes': { span: 24 },
  'variation_theme': { span: 24 },

  // === 产品尺寸与重量子字段 ===
  'item_package_quantity': { order: 40, span: 24 },
  'number_of_items': { order: 41, span: 24 },
  
  'item_package_dimensions': { span: 24 },
  'item_package_dimensions.height': { span: 24 },
  'item_package_dimensions.height.value': { order: 50, span: 16 },
  'item_package_dimensions.height.unit': { order: 51, span: 8 },
  'item_package_dimensions.length': { span: 24 },
  'item_package_dimensions.length.value': { order: 52, span: 16 },
  'item_package_dimensions.length.unit': { order: 53, span: 8 },
  'item_package_dimensions.width': { span: 24 },
  'item_package_dimensions.width.value': { order: 54, span: 16 },
  'item_package_dimensions.width.unit': { order: 55, span: 8 },

  'item_package_weight': { span: 24 },
  'item_package_weight.value': { order: 60, span: 16 },
  'item_package_weight.unit': { order: 61, span: 8 },
  'item_weight': { span: 24 },
  'item_weight.value': { order: 62, span: 16 },
  'item_weight.unit': { order: 63, span: 8 },
  
  // 原有的净尺寸也做类似微调
  'item_dimensions': { span: 24 },
  'item_dimensions.height': { span: 24 },
  'item_dimensions.height.value': { order: 70, span: 16 },
  'item_dimensions.height.unit': { order: 71, span: 8 },
  'item_dimensions.length': { span: 24 },
  'item_dimensions.length.value': { order: 72, span: 16 },
  'item_dimensions.length.unit': { order: 73, span: 8 },
  'item_dimensions.width': { span: 24 },
  'item_dimensions.width.value': { order: 74, span: 16 },
  'item_dimensions.width.unit': { order: 75, span: 8 },

  // === 产品标识 ===
  'supplier_declared_has_product_identifier_exemption': { order: 1, span: 24 },
  'externally_assigned_product_identifier': { 
    order: 2,
    dependsOn: { field: 'supplier_declared_has_product_identifier_exemption.0.value', value: true, negate: true }
  },
  'merchant_suggested_asin': { order: 3, span: 24 },

  // === 报价与销售 ===
  'product_site_launch_date': { order: 10, span: 24, uiWidget: 'date-picker', tooltip: '商品在亚马逊上可见可搜索的日期' },
  'merchant_release_date': { order: 11, span: 24, uiWidget: 'date-picker', tooltip: 'Release date set by the seller' },
  'condition_type': { order: 12, span: 24 },
  'fulfillment_availability': { span: 24 },
  'fulfillment_availability.0.fulfillment_channel_code': { order: 13, span: 24 },
  'fulfillment_channel_code': { order: 13, span: 24 }, 
  
  'merchant_shipping_group': { 
    order: 20, span: 24,
    dependsOn: { field: 'fulfillment_availability.0.fulfillment_channel_code', value: 'DEFAULT' }
  },
  'is_inventory_available': { 
    order: 21, span: 24,
    dependsOn: { field: 'fulfillment_availability.0.fulfillment_channel_code', value: 'DEFAULT' }
  },
  'quantity': { 
    order: 22, span: 24,
    dependsOn: { field: 'fulfillment_availability.0.fulfillment_channel_code', value: 'DEFAULT' }
  },
  'lead_time_to_ship_max_days': { 
    order: 23, span: 24,
    dependsOn: { field: 'fulfillment_availability.0.fulfillment_channel_code', value: 'DEFAULT' }
  },
  'restock_date': { 
    order: 24, span: 24, uiWidget: 'date-picker',
    dependsOn: { field: 'fulfillment_availability.0.fulfillment_channel_code', value: 'DEFAULT' }
  },
  
  // 价格信息排在最后
  'purchasable_offer': { order: 30, span: 24 },
  'list_price': { order: 31, span: 24 },
  'street_date': { order: 32, span: 24, uiWidget: 'date-picker' },

  // === 合规与电池 ===
  'batteries_required': { span: 24, order: 1 },
  'batteries_included': { span: 24, order: 2 },
  'supplier_declared_dg_hz_regulation': { span: 24, order: 3 },
  'hazmat': { span: 24 },
  'gpsr_safety_attestation': { span: 24 },
  'gpsr_manufacturer_reference': { span: 24 },
  'dsa_responsible_party_address': { span: 24 },
  'compliance_media': { span: 24 },
  'country_of_origin_product_level': { span: 24 },
  'cpsia_cautionary_statement': { span: 24 },
  'california_proposition_65': { span: 24 },

  // === 日期类组件覆盖 ===
  'start_at': { uiWidget: 'date-picker', span: 12 },
  'end_at': { uiWidget: 'date-picker', span: 12 },
  // restock_date already defined above with dependsOn
  'offering_release_date': { uiWidget: 'date-picker', span: 12 },
  'sell_end_date': { uiWidget: 'date-picker', span: 12 },
  'product_site_launch_date_alt': { uiWidget: 'date-picker', span: 12 }
};

/**
 * 根据 Field ID 获取对应的 UI 配置
 */
export function getUiConfigForField(fieldId: string): AmazonUiFieldConfig {
  // 0. 预处理：移除路径中的数字索引（如 .0.），以便更好地匹配 UI Schema
  // 例如：item_package_dimensions.0.height.value -> item_package_dimensions.height.value
  const normalizedFieldId = fieldId.replace(/\.\d+\./g, '.');

  // 1. 精确匹配
  if (AmazonUiSchema[normalizedFieldId]) {
    return AmazonUiSchema[normalizedFieldId];
  }
  
  // 2. 尝试后缀/包含匹配（支持简写匹配）
  const sortedKeys = Object.keys(AmazonUiSchema).sort((a, b) => b.length - a.length);
  
  for (const key of sortedKeys) {
    const config = AmazonUiSchema[key];
    
    // 如果 normalizedFieldId 包含完整的 key 路径，或者以 .key 结尾
    if (normalizedFieldId === key || normalizedFieldId.endsWith('.' + key) || normalizedFieldId.includes('.' + key + '.')) {
      return config;
    }
    
    // 3. 前缀匹配（继承父级分组配置，如 span/dependsOn）
    if (normalizedFieldId.startsWith(key + '.')) {
      const inherited: AmazonUiFieldConfig = {};
      if (config.span) inherited.span = config.span;
      if (config.order !== undefined) inherited.order = config.order;
      if (config.dependsOn) inherited.dependsOn = config.dependsOn;
      
      // 特殊情况：如果是 .value 或 .unit 结尾，且当前 key 是父路径
      if (normalizedFieldId.endsWith('.value')) {
         if (config.label) inherited.label = config.label;
      }
      if (normalizedFieldId.endsWith('.unit')) {
         inherited.label = 'Unit';
      }
      return inherited;
    }
  }
  
  return {};
};
