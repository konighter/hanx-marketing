-- 亚马逊广告活动表
CREATE TABLE IF NOT EXISTS erplus_amz_adv_campaign (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    shop_id VARCHAR(64) NOT NULL COMMENT '店铺ID',
    campaign_id VARCHAR(64) COMMENT '广告活动ID (Amazon侧)',
    name VARCHAR(255) NOT NULL COMMENT '广告活动名称',
    sync_status INT DEFAULT 0 COMMENT '同步状态(0-未同步，1-同步中，2-已同步，3-同步失败)',
    last_sync_time DATETIME COMMENT '最后同步时间',
    sync_error_msg VARCHAR(500) COMMENT '同步错误信息',
    state VARCHAR(20) DEFAULT 'enabled' COMMENT '广告活动状态(enabled,paused,archived)',
    campaign_type VARCHAR(50) DEFAULT 'sponsoredProducts' COMMENT '广告类型',
    daily_budget DECIMAL(10,2) COMMENT '每日预算',
    bidding_strategy VARCHAR(50) DEFAULT 'manual' COMMENT '出价策略',
    start_date DATETIME COMMENT '开始日期',
    end_date DATETIME COMMENT '结束日期',
    targeting_type VARCHAR(20) DEFAULT 'auto' COMMENT '目标市场(auto,manual)',
    campaign_sub_type VARCHAR(50) COMMENT '广告系列类型',
    description TEXT COMMENT '描述',
    tags VARCHAR(500) COMMENT '标签',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator BIGINT COMMENT '创建者',
    updater BIGINT COMMENT '更新者',
    deleted bit(1) DEFAULT b'0' COMMENT '是否删除'
) COMMENT='亚马逊广告活动表';

-- 亚马逊广告组表
CREATE TABLE IF NOT EXISTS erplus_amz_adv_ad_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    shop_id VARCHAR(64) NOT NULL COMMENT '店铺ID',
    ad_group_id VARCHAR(64) COMMENT '广告组ID (Amazon侧)',
    campaign_id VARCHAR(64) NOT NULL COMMENT '广告活动ID',
    name VARCHAR(255) NOT NULL COMMENT '广告组名称',
    state VARCHAR(20) DEFAULT 'enabled' COMMENT '广告组状态',
    default_bid DECIMAL(10,2) COMMENT '默认出价',
    max_bid DECIMAL(10,2) COMMENT '最高出价',
    placement VARCHAR(20) COMMENT '目标设备(desktop,mobile,other)',
    placement_type VARCHAR(20) COMMENT '商品投放类型(detail,homepage,other)',
    description TEXT COMMENT '描述',
    created_time DATETIME COMMENT '创建时间',
    updated_time DATETIME COMMENT '更新时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator BIGINT COMMENT '创建者',
    updater BIGINT COMMENT '更新者',
    deleted bit(1) DEFAULT b'0' COMMENT '是否删除'
) COMMENT='亚马逊广告组表';

-- 亚马逊广告关键词表
CREATE TABLE IF NOT EXISTS erplus_amz_adv_keyword (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    shop_id VARCHAR(64) NOT NULL COMMENT '店铺ID',
    keyword_id VARCHAR(64) COMMENT '关键词ID (Amazon侧)',
    campaign_id VARCHAR(64) NOT NULL COMMENT '广告活动ID',
    ad_group_id VARCHAR(64) NOT NULL COMMENT '广告组ID',
    keyword_text VARCHAR(255) NOT NULL COMMENT '关键词文本',
    match_type VARCHAR(20) NOT NULL COMMENT '关键词匹配类型(exact,phrase,broad)',
    state VARCHAR(20) DEFAULT 'enabled' COMMENT '关键词状态',
    bid DECIMAL(10,2) COMMENT '出价',
    suggested VARCHAR(10) COMMENT '关键词建议状态',
    priority INT DEFAULT 0 COMMENT '关键词优先级',
    description TEXT COMMENT '关键词描述',
    created_time DATETIME COMMENT '创建时间',
    updated_time DATETIME COMMENT '更新时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator BIGINT COMMENT '创建者',
    updater BIGINT COMMENT '更新者',
    deleted bit(1) DEFAULT b'0' COMMENT '是否删除'
) COMMENT='亚马逊广告关键词表';

-- 亚马逊广告出价策略表
CREATE TABLE IF NOT EXISTS erplus_amz_adv_bid_strategy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    shop_id VARCHAR(64) NOT NULL COMMENT '店铺ID',
    name VARCHAR(255) NOT NULL COMMENT '策略名称',
    strategy_type VARCHAR(50) NOT NULL COMMENT '策略类型(fixed,dynamic,rule_based)',
    config TEXT COMMENT '策略配置JSON',
    description TEXT COMMENT '策略描述',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    campaign_type VARCHAR(50) COMMENT '关联的广告活动类型',
    trigger_conditions VARCHAR(500) COMMENT '触发条件',
    adjustment_percentage DECIMAL(5,2) COMMENT '调整幅度',
    min_bid DECIMAL(10,2) COMMENT '最低出价限制',
    max_bid DECIMAL(10,2) COMMENT '最高出价限制',
    execution_frequency VARCHAR(20) DEFAULT 'daily' COMMENT '执行频率(hourly,daily,weekly)',
    created_time DATETIME COMMENT '创建时间',
    updated_time DATETIME COMMENT '更新时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator BIGINT COMMENT '创建者',
    updater BIGINT COMMENT '更新者',
    deleted bit(1) DEFAULT b'0' COMMENT '是否删除'
) COMMENT='亚马逊广告出价策略表';