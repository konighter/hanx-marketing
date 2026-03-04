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
