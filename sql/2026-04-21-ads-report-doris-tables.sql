CREATE TABLE `ads_report_batch` (
  -- ===== 维度列 (作为 Unique Key) =====
  `report_date` DATE NOT NULL COMMENT '批次覆盖目标的一天',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `platform` VARCHAR(32) NOT NULL COMMENT '广告平台 (AMAZON, META, GOOGLE)',
  `campaign_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告活动ID',
  `ad_group_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告组ID',
  `ad_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告ID',
  `keyword_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '关键词/匹配对象ID',
  `placement` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '广告位',
  `product_asin` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '商品 ASIN',
  `record_type` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '记录类型 (CAMPAIGN, AD_GROUP, AD, TARGETING, PLACEMENT, SEARCH_TERM)',
  `search_term` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '搜索词',

  -- ===== 基础指标列 =====
  `impressions` BIGINT DEFAULT '0' COMMENT '曝光量',
  `clicks` BIGINT DEFAULT '0' COMMENT '点击量',
  `cost` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '花费',
  `sales` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '通用销售额',
  `orders` BIGINT DEFAULT '0' COMMENT '通用订单量',

  -- ===== Amazon 扩展归因指标 =====
  `amz_attributed_sales_1d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 1天归因销售额',
  `amz_attributed_sales_7d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 7天归因销售额',
  `amz_attributed_sales_14d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 14天归因销售额',
  `amz_attributed_sales_30d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 30天归因销售额',
  `amz_attributed_units_ordered_1d` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天归因订单',
  `amz_attributed_units_ordered_7d` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天归因订单',
  `amz_attributed_units_ordered_14d` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天归因订单(Units)',
  `amz_attributed_units_ordered_30d` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天归因订单(Units)',
  `amz_attributed_conversions_1d` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天归因转化数(Orders)',
  `amz_attributed_conversions_7d` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天归因转化数(Orders)',
  `amz_attributed_conversions_14d` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天归因转化数(Orders)',
  `amz_attributed_conversions_30d` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天归因转化数(Orders)',
  `amz_attributed_sales_1d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 1天归因同SKU销售额',
  `amz_attributed_sales_7d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 7天归因同SKU销售额',
  `amz_attributed_sales_14d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 14天归因同SKU销售额',
  `amz_attributed_sales_30d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 30天归因同SKU销售额',
  `amz_attributed_units_ordered_1d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天归因同SKU订单',
  `amz_attributed_units_ordered_7d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天归因同SKU订单',
  `amz_attributed_units_ordered_14d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天归因同SKU订单(Units)',
  `amz_attributed_units_ordered_30d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天归因同SKU订单(Units)',
  `amz_attributed_conversions_1d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天归因同SKU转化数(Orders)',
  `amz_attributed_conversions_7d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天归因同SKU转化数(Orders)',
  `amz_attributed_conversions_14d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天归因同SKU转化数(Orders)',
  `amz_attributed_conversions_30d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天归因同SKU转化数(Orders)',

  -- ===== Meta 扩展归因指标 =====
  `meta_reach` BIGINT DEFAULT '0' COMMENT '[Meta] 到达人数 (Reach)',
  `meta_frequency` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Meta] 展示频率',
  `meta_purchases_1d_click` BIGINT DEFAULT '0' COMMENT '[Meta] 1天点击归因购买数',
  `meta_purchases_7d_click` BIGINT DEFAULT '0' COMMENT '[Meta] 7天点击归因购买数',
  `meta_purchases_1d_view` BIGINT DEFAULT '0' COMMENT '[Meta] 1天浏览归因购买数',

  -- ===== Google 扩展归因指标 =====
  `gg_view_through_conversions` BIGINT DEFAULT '0' COMMENT '[Google] 浏览型转化数',
  `gg_conversions` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Google] 转化数(支持小数)',
  `gg_conversion_value` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Google] 转化总价值'

) ENGINE=OLAP
UNIQUE KEY(`report_date`, `shop_id`, `platform`, `campaign_id`, `ad_group_id`, `ad_id`, `keyword_id`, `placement`, `product_asin`, `record_type`, `search_term`)
COMMENT '离线统一广告多维批处理聚合表'
PARTITION BY RANGE(`report_date`) ()
DISTRIBUTED BY HASH(`shop_id`) BUCKETS 16
PROPERTIES (
  "replication_num" = "1",
  "dynamic_partition.enable" = "true",
  "dynamic_partition.time_unit" = "MONTH",
  "dynamic_partition.start" = "-30",
  "dynamic_partition.end" = "2",
  "dynamic_partition.prefix" = "p",
  "dynamic_partition.buckets" = "16",
  "enable_unique_key_merge_on_write" = "true"
);


CREATE TABLE `ads_report_stream_realtime` (
  -- ===== 维度列 (作为 Unique Key) =====
  `window_start_time` DATETIME NOT NULL COMMENT '流窗口起始时间 (UTC)',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `platform` VARCHAR(32) NOT NULL COMMENT '广告平台 (AMAZON, META, GOOGLE)',
  `campaign_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告活动ID',
  `ad_group_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告组ID',
  `ad_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '广告ID',
  `keyword_id` VARCHAR(64) NOT NULL DEFAULT '0' COMMENT '关键词/匹配对象ID',
  `placement` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '广告位',
  `product_asin` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '商品 ASIN',
  `search_term` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '搜索词',

  -- ===== 基础指标列 =====
  `impressions` BIGINT DEFAULT '0' COMMENT '曝光量',
  `clicks` BIGINT DEFAULT '0' COMMENT '点击量',
  `cost` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '花费',
  `sales` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '估算销售额',
  `orders` BIGINT DEFAULT '0' COMMENT '估算订单量',

  -- ===== Amazon 扩展归因指标 =====
  `amz_attributed_sales_1d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 1天估算归因销售额',
  `amz_attributed_sales_7d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 7天估算归因销售额',
  `amz_attributed_sales_14d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 14天估算归因销售额',
  `amz_attributed_sales_30d` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 30天估算归因销售额',
  `amz_attributed_units_ordered_1d` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天估算归因订单',
  `amz_attributed_units_ordered_7d` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天估算归因订单',
  `amz_attributed_units_ordered_14d` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天估算归因订单(Units)',
  `amz_attributed_units_ordered_30d` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天估算归因订单(Units)',
  `amz_attributed_conversions_1d` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天估算归因转化数(Orders)',
  `amz_attributed_conversions_7d` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天估算归因转化数(Orders)',
  `amz_attributed_conversions_14d` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天估算归因转化数(Orders)',
  `amz_attributed_conversions_30d` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天估算归因转化数(Orders)',
  `amz_attributed_sales_1d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 1天估算归因同SKU销售额',
  `amz_attributed_sales_7d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 7天估算归因同SKU销售额',
  `amz_attributed_sales_14d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 14天估算归因同SKU销售额',
  `amz_attributed_sales_30d_same_sku` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Amazon] 30天估算归因同SKU销售额',
  `amz_attributed_units_ordered_1d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天估算归因同SKU订单',
  `amz_attributed_units_ordered_7d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天估算归因同SKU订单',
  `amz_attributed_units_ordered_14d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天估算归因同SKU订单(Units)',
  `amz_attributed_units_ordered_30d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天估算归因同SKU订单(Units)',
  `amz_attributed_conversions_1d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 1天估算归因同SKU转化数(Orders)',
  `amz_attributed_conversions_7d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 7天估算归因同SKU转化数(Orders)',
  `amz_attributed_conversions_14d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 14天估算归因同SKU转化数(Orders)',
  `amz_attributed_conversions_30d_same_sku` BIGINT DEFAULT '0' COMMENT '[Amazon] 30天估算归因同SKU转化数(Orders)',

  -- ===== Meta 扩展归因指标 =====
  `meta_reach` BIGINT DEFAULT '0' COMMENT '[Meta] 到达人数 (Reach)',
  `meta_frequency` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Meta] 展示频率',
  `meta_purchases_1d_click` BIGINT DEFAULT '0' COMMENT '[Meta] 1天点击估算归因购买数',
  `meta_purchases_7d_click` BIGINT DEFAULT '0' COMMENT '[Meta] 7天点击估算归因购买数',
  `meta_purchases_1d_view` BIGINT DEFAULT '0' COMMENT '[Meta] 1天浏览估算归因购买数',

  -- ===== Google 扩展归因指标 =====
  `gg_view_through_conversions` BIGINT DEFAULT '0' COMMENT '[Google] 浏览型估算转化数',
  `gg_conversions` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Google] 转化数(支持小数)',
  `gg_conversion_value` DECIMAL(15, 4) DEFAULT '0.0000' COMMENT '[Google] 转化总价值'

) ENGINE=OLAP
UNIQUE KEY(`window_start_time`, `shop_id`, `platform`, `campaign_id`, `ad_group_id`, `ad_id`, `keyword_id`, `placement`, `product_asin`, `search_term`)
COMMENT '实时统一广告多维流数据宽表'
PARTITION BY RANGE(`window_start_time`) ()
DISTRIBUTED BY HASH(`shop_id`) BUCKETS 16
PROPERTIES (
  "replication_num" = "1",
  "dynamic_partition.enable" = "true",
  "dynamic_partition.time_unit" = "DAY",
  "dynamic_partition.create_history_partition" = "true",
  "dynamic_partition.start" = "-180",
  "dynamic_partition.end" = "3",
  "dynamic_partition.prefix" = "p",
  "dynamic_partition.buckets" = "16",
  "enable_unique_key_merge_on_write" = "true"
);
