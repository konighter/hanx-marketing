-- ----------------------------
-- 商品 SKU 物流 & 报关信息 扩展字段
-- ----------------------------

-- 暂时作为扩展属性存储在 erplus_product_sku 表中
ALTER TABLE `erplus_product_sku` ADD COLUMN `logistics` json DEFAULT NULL COMMENT '物流报关信息 (JSON)';
