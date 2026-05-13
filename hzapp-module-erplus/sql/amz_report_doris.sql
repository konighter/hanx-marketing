CREATE TABLE `amazon_brand_performance_report` (
  -- ===== 基础维度 =====
  `start_date` DATE NOT NULL COMMENT '统计起始日期',
  `end_date` DATE NOT NULL COMMENT '统计结束日期',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `marketplace_id` VARCHAR(32) NOT NULL COMMENT '站点ID',
  `report_type` VARCHAR(64) NOT NULL COMMENT '报告类型 (SCP, SQP)',
  `asin` VARCHAR(64) NOT NULL COMMENT '商品 ASIN',
  `search_query` VARCHAR(256) DEFAULT '' COMMENT '搜索查询词 (SCP 类型此列存空字符串)',
  
  -- ===== 搜索热度 (仅 SQP 包含) =====
  `search_query_score` BIGINT DEFAULT '0' COMMENT '查询分数/热度',
  `search_query_volume` BIGINT DEFAULT '0' COMMENT '查询搜索量',

  -- ===== 1. 展现阶段 (Impression) =====
  `asin_impression_count` BIGINT DEFAULT '0' COMMENT 'ASIN 展现量',
  `total_impression_count` BIGINT DEFAULT '0' COMMENT '市场总展现量',
  `impression_share` DOUBLE DEFAULT '0' COMMENT 'ASIN 展现份额',
  `impression_median_price` DECIMAL(15, 2) DEFAULT '0' COMMENT '展现中值价格',
  `sameday_shipping_impression_count` BIGINT DEFAULT '0' COMMENT '当日达展现量',
  `oneday_shipping_impression_count` BIGINT DEFAULT '0' COMMENT '次日达展现量',
  `twoday_shipping_impression_count` BIGINT DEFAULT '0' COMMENT '两日达展现量',

  -- ===== 2. 点击阶段 (Click) =====
  `asin_click_count` BIGINT DEFAULT '0' COMMENT 'ASIN 点击量',
  `total_click_count` BIGINT DEFAULT '0' COMMENT '市场总点击量',
  `asin_click_share` DOUBLE DEFAULT '0' COMMENT 'ASIN 点击份额',
  `asin_click_rate` DOUBLE DEFAULT '0' COMMENT 'ASIN 点击率',
  `total_click_rate` DOUBLE DEFAULT '0' COMMENT '市场总点击率',
  `asin_median_click_price` DECIMAL(15, 2) DEFAULT '0' COMMENT 'ASIN 点击中值价格',
  `total_median_click_price` DECIMAL(15, 2) DEFAULT '0' COMMENT '市场点击中值价格',
  `sameday_shipping_click_count` BIGINT DEFAULT '0' COMMENT '当日达点击量',
  `oneday_shipping_click_count` BIGINT DEFAULT '0' COMMENT '次日达点击量',
  `twoday_shipping_click_count` BIGINT DEFAULT '0' COMMENT '两日达点击量',

  -- ===== 3. 加购阶段 (Cart Add) =====
  `asin_cart_add_count` BIGINT DEFAULT '0' COMMENT 'ASIN 加购量',
  `total_cart_add_count` BIGINT DEFAULT '0' COMMENT '市场总加购量',
  `asin_cart_add_share` DOUBLE DEFAULT '0' COMMENT 'ASIN 加购份额',
  `total_cart_add_rate` DOUBLE DEFAULT '0' COMMENT '市场总加购率',
  `asin_median_cart_add_price` DECIMAL(15, 2) DEFAULT '0' COMMENT 'ASIN 加购中值价格',
  `total_median_cart_add_price` DECIMAL(15, 2) DEFAULT '0' COMMENT '市场加购中值价格',
  `sameday_shipping_cart_add_count` BIGINT DEFAULT '0' COMMENT '当日达加购量',
  `oneday_shipping_cart_add_count` BIGINT DEFAULT '0' COMMENT '次日达加购量',
  `twoday_shipping_cart_add_count` BIGINT DEFAULT '0' COMMENT '两日达加购量',

  -- ===== 4. 购买阶段 (Purchase) =====
  `asin_purchase_count` BIGINT DEFAULT '0' COMMENT 'ASIN 购买量',
  `total_purchase_count` BIGINT DEFAULT '0' COMMENT '市场总购买量',
  `asin_purchase_share` DOUBLE DEFAULT '0' COMMENT 'ASIN 购买份额',
  `asin_purchase_sales` DECIMAL(15, 2) DEFAULT '0' COMMENT 'ASIN 搜索流量额 (searchTrafficSales)',
  `asin_conversion_rate` DOUBLE DEFAULT '0' COMMENT 'ASIN 转化率 (conversionRate)',
  `total_purchase_rate` DOUBLE DEFAULT '0' COMMENT '市场总购买率',
  `asin_median_purchase_price` DECIMAL(15, 2) DEFAULT '0' COMMENT 'ASIN 购买中值价格',
  `total_median_purchase_price` DECIMAL(15, 2) DEFAULT '0' COMMENT '市场购买中值价格',
  `sameday_shipping_purchase_count` BIGINT DEFAULT '0' COMMENT '当日达购买量',
  `oneday_shipping_purchase_count` BIGINT DEFAULT '0' COMMENT '次日达购买量',
  `twoday_shipping_purchase_count` BIGINT DEFAULT '0' COMMENT '两日达购买量',

  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '数据更新时间'
) ENGINE=OLAP
UNIQUE KEY(`start_date`, `end_date`, `shop_id`, `marketplace_id`, `report_type`, `asin`, `search_query`)
COMMENT '亚马逊品牌分析-综合表现报表'
DISTRIBUTED BY HASH(`asin`) BUCKETS 16
PROPERTIES (
  "replication_num" = "1",
  "enable_unique_key_merge_on_write" = "true"
);
