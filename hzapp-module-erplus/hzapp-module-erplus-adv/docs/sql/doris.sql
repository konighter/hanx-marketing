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
