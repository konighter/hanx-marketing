-- 为 erplus_platform_shop 表添加 timezone 字段
ALTER TABLE `erplus_platform_shop` ADD COLUMN `timezone` VARCHAR(50) DEFAULT NULL COMMENT '时区' AFTER `account_id`;
