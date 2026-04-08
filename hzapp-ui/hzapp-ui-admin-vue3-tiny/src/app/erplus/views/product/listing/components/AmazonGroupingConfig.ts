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
      'description',
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
      'standard_price',
      'list_price',
      'fulfillment_channel_code',
      'merchant_shipping_group',
      'quantity',
      'lead_time_to_ship_max_days',
      'restock_date',
      'merchant_release_date'
    ]
  }
];

/** Group Names Constants */
export const GROUP_NAME_REMAINING_REQUIRED = '其他必填属性';
export const GROUP_NAME_OPTIONAL = '附加可选属性';
