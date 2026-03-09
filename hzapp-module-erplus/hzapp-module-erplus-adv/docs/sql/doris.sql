DROP TABLE IF EXISTS ads_report_daily;
CREATE TABLE IF NOT EXISTS ads_report_daily (
                                                `date` DATE NOT NULL COMMENT '报告日期',
                                                `account_id` BIGINT NOT NULL COMMENT '广告账户',
                                                `group_column` VARCHAR(64) NOT NULL COMMENT '维度类型: campaign, adGroup, placement, targeting, searchTerm',
    `campaign_id` BIGINT NOT NULL COMMENT '广告活动ID',
    `ad_group_id` BIGINT COMMENT '广告组ID',
    `targeting` VARCHAR(512) COMMENT '投放关键词/对象',
    `search_term` VARCHAR(512) COMMENT '搜索词',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `payload` VARIANT COMMENT '各维度性能指标明细(JSON对象)',
    -- 倒排索引
    INDEX idx_variant_payload (`payload`) USING INVERTED COMMENT '指标数据倒排索引'
    ) ENGINE=OLAP
    DUPLICATE KEY(`date`,`account_id`, `group_column`, `campaign_id`, `ad_group_id`, `targeting`, `search_term`, `tenant_id`)
    COMMENT '亚马逊SP广告原始明细数据表'
    PARTITION BY RANGE(`date`) (
                                   FROM ("2026-01-01") TO ("2026-03-01") INTERVAL 1 DAY
    )
    DISTRIBUTED BY HASH(`campaign_id`) BUCKETS 10  -- 此处也建议指定具体分桶数
    PROPERTIES (
                   "replication_allocation" = "tag.location.default: 1",
                   "dynamic_partition.enable" = "true",
                   "dynamic_partition.time_unit" = "DAY",
                   "dynamic_partition.start" = "-365",
                   "dynamic_partition.end" = "3",
                   "dynamic_partition.prefix" = "p",
                   "dynamic_partition.buckets" = "10" -- 修正此处：将 AUTO 改为具体的数值
               );

-- ----------------------------
-- 广告小时绩效报表 (Doris 聚合模型)
-- ----------------------------
DROP TABLE IF EXISTS `ads_report_hourly`;
CREATE TABLE `ads_report_hourly` (
  `account_id`     BIGINT         NOT NULL COMMENT '广告账户ID',
  `group_column`   VARCHAR(32)    NOT NULL COMMENT '聚合维度: campaign / adGroup / ad / keyword',
  `report_hour`    DATETIME       NOT NULL COMMENT '报表小时点',
  `campaign_id`    BIGINT         NOT NULL DEFAULT '-1' COMMENT '广告活动ID (-1 = 不在聚合维度)',
  `ad_group_id`    BIGINT         NOT NULL DEFAULT '-1' COMMENT '广告组ID',
  `ad_id`          BIGINT         NOT NULL DEFAULT '-1' COMMENT '广告ID',
  `keyword_id`     BIGINT         NOT NULL DEFAULT '-1' COMMENT '关键词ID',
  `impressions`    BIGINT         SUM      DEFAULT '0' COMMENT '展示数',
  `clicks`         BIGINT         SUM      DEFAULT '0' COMMENT '点击数',
  `cost`           DECIMAL(18,4)  SUM      DEFAULT '0' COMMENT '花费',
  `sales7d`        DECIMAL(18,4)  SUM      DEFAULT '0' COMMENT '7天归因销售额',
  `orders7d`       BIGINT         SUM      DEFAULT '0' COMMENT '7天归因订单量',
  `sales14d`       DECIMAL(18,4)  SUM      DEFAULT '0' COMMENT '14天归因销售额',
  `orders14d`      BIGINT         SUM      DEFAULT '0' COMMENT '14天归因订单量',
  `sales30d`       DECIMAL(18,4)  SUM      DEFAULT '0' COMMENT '30天归因销售额',
  `orders30d`      BIGINT         SUM      DEFAULT '0' COMMENT '30天归因订单量'
) ENGINE=OLAP
AGGREGATE KEY(`account_id`, `group_column`, `report_hour`, `campaign_id`, `ad_group_id`, `ad_id`, `keyword_id`)
DISTRIBUTED BY HASH(`account_id`) BUCKETS 8
PROPERTIES (
  "replication_allocation" = "tag.location.default: 1"
);



DROP TABLE IF EXISTS `ads_amazon_stream_sp_traffic`;
CREATE TABLE `ads_amazon_stream_sp_traffic` (
  `advertiser_id`   VARCHAR(32)      COMMENT '广告主ID',
  `dataset_id`      VARCHAR(32)      NOT NULL COMMENT '数据集ID: sp-traffic, sp-conversion',
  `time_window_start` VARCHAR(50)       NOT NULL COMMENT '时间窗口开始时间戳',
  `campaign_id`     VARCHAR(32)      NOT NULL COMMENT '广告活动外部ID',
  `ad_group_id`     VARCHAR(32)      COMMENT '广告组外部ID',
  `ad_id`           VARCHAR(32)      COMMENT '广告外部ID',
  `keyword_id`      VARCHAR(32)      COMMENT '关键词外部ID',
  `keyword_text`    VARCHAR(512)     COMMENT '关键词文本',
  `match_type`      VARCHAR(16)      COMMENT '匹配类型',
  `placement`       VARCHAR(128)     COMMENT '广告位',
  `currency`        VARCHAR(8)       COMMENT '币种',
  `marketplace_id`  VARCHAR(32)      COMMENT '市场ID',


  `impressions`     BIGINT           SUM DEFAULT 0 COMMENT '展示数',
  `clicks`          BIGINT           SUM DEFAULT 0 COMMENT '点击数',
  `cost`            DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '花费'
) ENGINE=OLAP
AGGREGATE KEY(`advertiser_id`, `dataset_id`, `time_window_start`, `campaign_id`,
              `ad_group_id`, `ad_id`, `keyword_id`, `keyword_text`,
              `match_type`, `placement`, `currency`, `marketplace_id`)
DISTRIBUTED BY HASH(`advertiser_id`) BUCKETS 8
PROPERTIES ("replication_allocation" = "tag.location.default: 1");


-- ----------------------------
-- Amazon SP-Conversion 归因转化明细流数据
-- ----------------------------
DROP TABLE IF EXISTS `ads_amazon_stream_sp_conversion`;
CREATE TABLE `ads_amazon_stream_sp_conversion` (
  `advertiser_id`                        VARCHAR(32)     NOT NULL COMMENT '广告主ID (profileId)',
  `dataset_id`                           VARCHAR(32)     NOT NULL COMMENT '数据集ID: sp-conversion',
  `time_window_start`                    BIGINT          NOT NULL COMMENT '时间窗口开始时间戳(Unix秒)',
  `campaign_id`                          VARCHAR(32)     NOT NULL COMMENT '广告活动外部ID',
  `ad_group_id`                          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '广告组外部ID',
  `ad_id`                                VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '广告外部ID',
  `keyword_id`                           VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '关键词外部ID',
  `placement`                            VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '广告位',
  `marketplace_id`                       VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '市场ID',
  `currency`                             VARCHAR(8)      NOT NULL DEFAULT '' COMMENT '币种',

  -- 归因转化次数
  `attributed_conversions_1d`            BIGINT          SUM DEFAULT 0 COMMENT '24h内归因转化次数',
  `attributed_conversions_7d`            BIGINT          SUM DEFAULT 0 COMMENT '7天内归因转化次数',
  `attributed_conversions_14d`           BIGINT          SUM DEFAULT 0 COMMENT '14天内归因转化次数',
  `attributed_conversions_30d`           BIGINT          SUM DEFAULT 0 COMMENT '30天内归因转化次数',
  `attributed_conversions_1d_same_sku`   BIGINT          SUM DEFAULT 0 COMMENT '24h内同SKU归因转化次数',
  `attributed_conversions_7d_same_sku`   BIGINT          SUM DEFAULT 0 COMMENT '7天内同SKU归因转化次数',
  `attributed_conversions_14d_same_sku`  BIGINT          SUM DEFAULT 0 COMMENT '14天内同SKU归因转化次数',
  `attributed_conversions_30d_same_sku`  BIGINT          SUM DEFAULT 0 COMMENT '30天内同SKU归因转化次数',

  -- 归因销售额
  `attributed_sales_1d`                  DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '24h内归因销售额',
  `attributed_sales_7d`                  DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '7天内归因销售额',
  `attributed_sales_14d`                 DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '14天内归因销售额',
  `attributed_sales_30d`                 DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '30天内归因销售额',
  `attributed_sales_1d_same_sku`         DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '24h内同SKU归因销售额',
  `attributed_sales_7d_same_sku`         DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '7天内同SKU归因销售额',
  `attributed_sales_14d_same_sku`        DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '14天内同SKU归因销售额',
  `attributed_sales_30d_same_sku`        DECIMAL(18,4)   SUM DEFAULT 0 COMMENT '30天内同SKU归因销售额',

  -- 归因订单单量
  `attributed_units_ordered_1d`          BIGINT          SUM DEFAULT 0 COMMENT '24h内归因销售件数',
  `attributed_units_ordered_7d`          BIGINT          SUM DEFAULT 0 COMMENT '7天内归因销售件数',
  `attributed_units_ordered_14d`         BIGINT          SUM DEFAULT 0 COMMENT '14天内归因销售件数',
  `attributed_units_ordered_30d`         BIGINT          SUM DEFAULT 0 COMMENT '30天内归因销售件数',
  `attributed_units_ordered_1d_same_sku` BIGINT          SUM DEFAULT 0 COMMENT '24h内同SKU归因销售件数',
  `attributed_units_ordered_7d_same_sku` BIGINT          SUM DEFAULT 0 COMMENT '7天内同SKU归因销售件数',
  `attributed_units_ordered_14d_same_sku` BIGINT         SUM DEFAULT 0 COMMENT '14天内同SKU归因销售件数',
  `attributed_units_ordered_30d_same_sku` BIGINT         SUM DEFAULT 0 COMMENT '30天内同SKU归因销售件数'

) ENGINE=OLAP
AGGREGATE KEY(`advertiser_id`, `dataset_id`, `time_window_start`, `campaign_id`,
              `ad_group_id`, `ad_id`, `keyword_id`, `placement`, `marketplace_id`, `currency`)
DISTRIBUTED BY HASH(`advertiser_id`) BUCKETS 8
PROPERTIES ("replication_allocation" = "tag.location.default: 1");