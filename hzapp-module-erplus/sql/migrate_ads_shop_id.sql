-- 1. 为各表添加 shop_id 字段
ALTER TABLE ads_campaign ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_ad_group ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_ad ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_keyword ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_report_daily ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_report_summary ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_optimization_rule ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_report_hourly ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_entity_change_log ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;
ALTER TABLE ads_optimization_rule_relation ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER id;
ALTER TABLE ads_optimization_rule_relation ADD COLUMN account_id bigint DEFAULT NULL COMMENT '广告账户ID' AFTER shop_id;
ALTER TABLE ads_sync_task ADD COLUMN shop_id bigint DEFAULT NULL COMMENT '所属店铺ID' AFTER account_id;



-- 3. 兜底策略：如果基于 account_id 找不到对应的 shop_id，则默认设置为 16
UPDATE ads_campaign SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_ad_group SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_ad SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_keyword SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_report_daily SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_report_summary SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_optimization_rule SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_report_hourly SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_entity_change_log SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_sync_task SET shop_id = 16 WHERE shop_id IS NULL;
UPDATE ads_optimization_rule_relation SET shop_id = 16 WHERE shop_id IS NULL;
