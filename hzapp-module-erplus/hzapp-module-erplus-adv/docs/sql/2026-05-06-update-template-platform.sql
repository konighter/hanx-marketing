-- 为广告自动化模版增加平台字段
ALTER TABLE `ads_automation_template` 
ADD COLUMN `platform` varchar(32) DEFAULT NULL COMMENT '适用平台 (AMAZON/META)' AFTER `type`;

-- 更新已有数据
UPDATE `ads_automation_template` SET `platform` = 'AMAZON' WHERE `platform` IS NULL;
