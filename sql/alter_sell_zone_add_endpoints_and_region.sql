-- erplus_sell_zone 表新增字段
ALTER TABLE erplus_sell_zone
    ADD COLUMN currency VARCHAR(16) DEFAULT NULL COMMENT '货币' AFTER language,
    ADD COLUMN region VARCHAR(32) DEFAULT NULL COMMENT '所属大区 (如 NA, EU, FE)' AFTER currency,
    ADD COLUMN country_code VARCHAR(16) DEFAULT NULL COMMENT '国家代码 (如 US, GB, DE)' AFTER region,
    ADD COLUMN sp_endpoint VARCHAR(128) DEFAULT NULL COMMENT 'SP-API Endpoint' AFTER country_code,
    ADD COLUMN adv_endpoint VARCHAR(128) DEFAULT NULL COMMENT '广告 API Endpoint' AFTER sp_endpoint;
