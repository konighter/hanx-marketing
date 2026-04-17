
DROP TABLE IF EXISTS `erplus_product_spu_attrs`;
-- 商品 SPU 扩展属性表
CREATE TABLE IF NOT EXISTS `erplus_product_spu_attrs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `spu_id` bigint(20) NOT NULL COMMENT 'SPU 编号',
  `attr_id` varchar(64) NOT NULL COMMENT '属性 ID (如 itemDim, certifications)',
  `attr_name` varchar(128) NOT NULL COMMENT '属性名称',
  `attr_value` text NOT NULL COMMENT '属性值 (JSON 格式)',
  `attr_class` varchar(255) DEFAULT NULL COMMENT '属性类路径 (用于反序列化)',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_spu_id` (`spu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品 SPU 扩展属性表';

DROP TABLE IF EXISTS `erplus_product_sku_attrs`;
-- 商品 SKU 扩展属性表
CREATE TABLE IF NOT EXISTS `erplus_product_sku_attrs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU 编号',
  `attr_id` varchar(64) NOT NULL COMMENT '属性 ID',
  `attr_name` varchar(128) NOT NULL COMMENT '属性名称',
  `attr_value` text NOT NULL COMMENT '属性值 (JSON 格式)',
  `attr_class` varchar(255) DEFAULT NULL COMMENT '属性类路径',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品 SKU 扩展属性表';


-- ----------------------------
-- 商品中心主表建表 SQL (SPU & SKU)
-- 只包含基础审计字段、业务核心字段 (约束、标识、基础展示)
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for erplus_product_spu
-- ----------------------------
DROP TABLE IF EXISTS `erplus_product_spu`;
CREATE TABLE `erplus_product_spu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `keyword` varchar(1024) DEFAULT NULL COMMENT '关键词',
  `introduction` varchar(2048) DEFAULT NULL COMMENT '商品简介',
  `description` text DEFAULT NULL COMMENT '商品详情',
  `category_id` bigint NOT NULL COMMENT '分类编号',
  `brand_id` bigint DEFAULT NULL COMMENT '品牌编号',
  `unit_id` bigint DEFAULT NULL COMMENT '单位编号',
  `pic_url` varchar(512) DEFAULT NULL COMMENT '封面图',
  `slider_pic_urls` varchar(2048) DEFAULT NULL COMMENT '轮播图',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `code` varchar(128) DEFAULT NULL COMMENT 'SPU 编码',
  `spec_type` tinyint NOT NULL DEFAULT '1' COMMENT '规格类型：1-单规格，2-多规格',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category` (`category_id`) USING BTREE,
  KEY `idx_brand` (`brand_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品 SPU 表';

-- ----------------------------
-- Table structure for erplus_product_sku
-- ----------------------------
DROP TABLE IF EXISTS `erplus_product_sku`;
CREATE TABLE `erplus_product_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `spu_id` bigint NOT NULL COMMENT 'SPU 编号',
  `properties` text DEFAULT NULL COMMENT '规格属性组合 JSON',
  `code` varchar(128) DEFAULT NULL COMMENT 'SKU 编码 (Seller SKU)',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '产品类型：1-普通产品，2-组合产品',
  `bar_code` varchar(128) DEFAULT NULL COMMENT '条码/EAN/UPC',
  `keyword` varchar(1024) DEFAULT NULL COMMENT '关键词',
  `introduction` varchar(2048) DEFAULT NULL COMMENT '商品简介',
  `description` text DEFAULT NULL COMMENT '详情',
  `pic_url` varchar(512) DEFAULT NULL COMMENT '图片地址',
  `slider_pic_urls` varchar(2048) DEFAULT NULL COMMENT '轮播图',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_spu_id` (`spu_id`) USING BTREE,
  KEY `idx_sku_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品 SKU 表';

SET FOREIGN_KEY_CHECKS = 1;
