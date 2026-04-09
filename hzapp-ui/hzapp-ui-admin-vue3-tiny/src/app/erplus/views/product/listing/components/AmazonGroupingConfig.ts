/**
 * Amazon Listing Grouping Configuration (v2)
 *
 * This configuration defines the "Fixed Groups" that capture specific, high-priority
 * mandatory fields.
 */

export interface AmazonFixedGroupConfig {
  name: string;
  /** Explicit field IDs or key prefixes that belong to this group */
  match: string[];
}

export const AmazonFixedGroups: AmazonFixedGroupConfig[] = [
  {
    name: '描述与关键字',
    match: [
      'product_description',
      'bullet_point',
      'generic_keyword'
    ]
  },
  {
    name: '基础信息',
    match: [
      'item_name',
      'brand',
      'manufacturer',
      'model_number',
      'part_number',
      'main_product_image_locator'
    ]
  },
  {
    name: '产品标识',
    match: [
      'externally_assigned_product_identifier',
      'supplier_declared_has_product_identifier_exemption',
      'merchant_suggested_asin',
      'product_id',
      'upc',
      'ean',
      'isbn',
      'gtin'
    ]
  },
  {
    name: '报价与销售',
    match: [
      'purchasable_offer',
      'fulfillment',
      'fulfillment_availability',
      'standard_price',
      'list_price',
      'price',
      'condition_type',
      'merchant_shipping_group',
      'fulfillment_channel_code',
      'is_inventory_available',
      'quantity',
      'lead_time_to_ship_max_days',
      'restock_date',
      'merchant_release_date',
      'product_site_launch_date',
      'street_date',
      'offering_release_date',
      'sell_end_date'
    ]
  },
  {
    name: '产品尺寸',
    match: [
      'item_package_quantity',
      'item_weight',
      'item_package_weight',
      'item_package_dimensions',
      'item_length_width_height',
      'item_dimensions',
      'number_of_items',
      'number_of_pieces',
      'item_display_weight',
      'item_volume'
    ]
  }
];

/** Group Names Constants */
export const GROUP_NAME_REMAINING_REQUIRED = '其他必填属性';
export const GROUP_NAME_OPTIONAL = '附加可选属性';
