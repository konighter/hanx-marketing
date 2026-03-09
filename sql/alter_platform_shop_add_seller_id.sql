-- erplus_platform_shop 表新增 seller_id 字段
ALTER TABLE erplus_platform_shop ADD COLUMN seller_id VARCHAR(64) DEFAULT NULL COMMENT '卖家ID (用于广告和店铺的关联)' AFTER auth_exp_time;
