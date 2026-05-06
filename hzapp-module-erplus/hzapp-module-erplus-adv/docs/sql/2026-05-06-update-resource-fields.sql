-- 为广告自动化计划资源关联表增加店铺字段
ALTER TABLE `ads_automation_plan_resource` 
ADD COLUMN `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺 ID';
