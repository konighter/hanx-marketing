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
    name: 'Description & Keywords',
    match: [
      'product_description',
      'bullet_point',
      'generic_keyword'
    ]
  },
  {
    name: 'Basic Information',
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
    name: 'Product Identifiers',
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
    name: 'Offer & Sales',
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
    name: 'Product Dimensions',
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
export const GROUP_NAME_REMAINING_REQUIRED = 'Other Required Attributes';
export const GROUP_NAME_OPTIONAL = 'Optional Attributes';
