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

/**
 * 亚马逊前端展示专用 Schema 映射配置层
 * 主要是针对核心字段（标题、SKU、价格相关）做精细化排版与体验调优
 * 支持基于前缀的匹配，比如 purchasable_offer.our_price
 */
export const AmazonUiSchema: Record<string, AmazonUiFieldConfig> = {
  // === 描述与关键字模块 (排在第一组) ===
  'product_description': { order: 1, span: 24, uiWidget: 'textarea', label: '产品描述', placeholder: '详细描述产品的功能、材质、尺寸等信息' },
  'description': { order: 2, span: 24, uiWidget: 'textarea', label: '产品描述' },
  'bullet_point': { order: 3, span: 24, label: '五点描述 (Bullet Points)' },
  'generic_keyword': { order: 4, span: 24, label: '搜索关键词', placeholder: '用空格分隔多个关键词，不要重复商品标题中的词' },
  'special_feature': { label: '特殊功能', span: 24 },
  'target_audience_keyword': { label: '目标受众', span: 12 },
  'subject_keyword': { label: '主题关键词', span: 12 },
  'platinum_keyword': { label: '白金关键词', span: 12 },

  // === 基本信息模块 ===
  'item_name': { 
    order: 1, 
    span: 24, 
    uiWidget: 'textarea', 
    label: '商品标题',
    placeholder: '推荐首字母大写，包含关键特征，建议控制在200字符内' 
  },
  'brand': { order: 2, span: 12, label: '品牌' },
  'manufacturer': { order: 3, span: 12, label: '制造商' },
  'part_number': { order: 4, span: 12, label: '零件编号' },
  'model_': { order: 5, span: 12, label: '型号' },
  'model_number': { order: 6, span: 12, label: '型号' },
  'model_name': { order: 7, span: 12, label: '型号名称' },
  'main_product_image_locator': { label: '主图链接', span: 24 },
  'swatch_product_image_locator': { label: '样本图链接', span: 12 },
  'other_product_image_locator': { label: '其他图片链接', span: 12 },

  // === 常用属性简称 ===
  'country_of_origin': { label: '原产国', span: 12 },
  'item_shape': { label: '形状', span: 12 },
  'style': { label: '风格', span: 12 },
  'material': { label: '材质', span: 12 },
  'color': { label: '颜色', span: 12 },
  'size': { label: '尺寸', span: 12 },
  'pattern': { label: '图案', span: 12 },
  'theme': { label: '主题', span: 12 },
  'finish_type': { label: '表面处理', span: 12 },
  'item_weight': { label: '商品重量', span: 12 },
  'item_package_weight': { label: '包装重量', span: 12 },
  'item_length_width_height': { label: '商品尺寸', span: 12 },
  'item_package_dimensions': { label: '包装尺寸', span: 12 },
  'number_of_items': { label: '件数', span: 12 },
  'number_of_pieces': { label: '组件数', span: 12 },
  'item_form': { label: '产品形态', span: 12 },
  'item_type_keyword': { label: '商品类型', span: 12 },
  'metal_type': { label: '金属种类', span: 12 },
  'recommended_browse_nodes': { label: '分类节点', span: 12 },
  'variation_theme': { label: '变体主题', span: 12 },

  // === 产品标识 ===
  'supplier_declared_has_product_identifier_exemption': { order: 1, span: 24, label: '免除产品ID' },
  'externally_assigned_product_identifier': { order: 2, label: '产品 ID (UPC/EAN)' },
  'merchant_suggested_asin': { order: 3, span: 12, label: '建议 ASIN' },

  // === 报价与销售 ===
  'condition_type': { order: 10, span: 12, label: '物品状况' },
  'fulfillment_channel_code': { order: 11, span: 12, label: '发货渠道' },
  'merchant_shipping_group': { order: 12, span: 12, label: '运费模板' },
  'is_inventory_available': { order: 13, span: 12, label: '始终有货' },
  'quantity': { order: 14, span: 12, label: '库存数量' },
  'lead_time_to_ship_max_days': { order: 15, span: 12, label: '处理时间' },
  'product_site_launch_date': { order: 20, span: 12, uiWidget: 'date-picker', label: '上架日期', tooltip: '商品在亚马逊上可见可搜索的日期' },
  'merchant_release_date': { order: 21, span: 12, uiWidget: 'date-picker', label: '发售日期', tooltip: '卖家设定的可出售日期' },
  'street_date': { order: 22, span: 12, uiWidget: 'date-picker', label: '首次发货日' },

  // === 合规与电池 ===
  'batteries_required': { label: '需要电池', span: 12, order: 1 },
  'batteries_included': { label: '附带电池', span: 12, order: 2 },
  'supplier_declared_dg_hz_regulation': { label: '危险品法规', span: 12, order: 3 },
  'hazmat': { label: '危险品合规', span: 12 },
  'gpsr_safety_attestation': { label: 'GPSR安全认证', span: 12 },
  'gpsr_manufacturer_reference': { label: 'GPSR制造商', span: 12 },
  'dsa_responsible_party_address': { label: '负责人地址(GPSR)', span: 12 },
  'compliance_media': { label: '合规文档', span: 12 },
  'country_of_origin_product_level': { label: '产品级原产国', span: 12 },
  'cpsia_cautionary_statement': { label: 'CPSIA警示', span: 12 },
  'california_proposition_65': { label: 'CA Prop 65', span: 12 },

  // === 日期类组件覆盖 ===
  'start_at': { uiWidget: 'date-picker', span: 12 },
  'end_at': { uiWidget: 'date-picker', span: 12 },
  'restock_date': { uiWidget: 'date-picker', span: 12, label: '补货日期' },
  'offering_release_date': { uiWidget: 'date-picker', span: 12 },
  'sell_end_date': { uiWidget: 'date-picker', span: 12, label: '停售日期' },
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
