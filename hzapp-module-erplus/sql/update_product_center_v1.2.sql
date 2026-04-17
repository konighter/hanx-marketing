-- ----------------------------
-- 管理后台 - 商品中心字段补充
-- ----------------------------

-- SPU 表补充字段
ALTER TABLE `erplus_product_spu` 
ADD COLUMN `unit_id` bigint NULL COMMENT '单位编号' AFTER `brand_id`,
ADD COLUMN `code` varchar(128) NULL COMMENT '商品编码' AFTER `status`,
ADD COLUMN `spec_type` tinyint NULL DEFAULT 1 COMMENT '规格类型：1-单规格，2-多规格' AFTER `code`,
ADD COLUMN `price` int NULL DEFAULT 0 COMMENT '价格（分）' AFTER `spec_type`,
ADD COLUMN `market_price` int NULL DEFAULT 0 COMMENT '市场价（分）' AFTER `price`,
ADD COLUMN `cost_price` int NULL DEFAULT 0 COMMENT '成本价（分）' AFTER `market_price`,
ADD COLUMN `stock` int NULL DEFAULT 0 COMMENT '库存' AFTER `cost_price`;

-- SKU 表补充字段（确保一致性）
ALTER TABLE `erplus_product_sku`
ADD COLUMN `code` varchar(128) NULL COMMENT 'SKU 编码' AFTER `spu_id`,
ADD COLUMN `type` tinyint NULL DEFAULT 1 COMMENT '产品类型：1-普通产品，2-组合产品' AFTER `code`,
ADD COLUMN `bar_code` varchar(128) NULL COMMENT '条码' AFTER `type`,
ADD COLUMN `keyword` varchar(1024) NULL COMMENT '关键词' AFTER `bar_code`,
ADD COLUMN `introduction` varchar(2048) NULL COMMENT '简介' AFTER `keyword`,
ADD COLUMN `description` text NULL COMMENT '详情' AFTER `introduction`,
ADD COLUMN `pic_url` varchar(512) NULL COMMENT '图片地址' AFTER `description`,
ADD COLUMN `slider_pic_urls` varchar(2048) NULL COMMENT '轮播图' AFTER `pic_url`,
ADD COLUMN `price` int NULL DEFAULT 0 COMMENT '价格（分）' AFTER `slider_pic_urls`,
ADD COLUMN `market_price` int NULL DEFAULT 0 COMMENT '市场价（分）' AFTER `price`,
ADD COLUMN `cost_price` int NULL DEFAULT 0 COMMENT '成本价（分）' AFTER `market_price`,
ADD COLUMN `stock` int NULL DEFAULT 0 COMMENT '库存' AFTER `cost_price`,
ADD COLUMN `weight` double NULL COMMENT '重量' AFTER `stock`,
ADD COLUMN `volume` double NULL COMMENT '体积' AFTER `weight`;
