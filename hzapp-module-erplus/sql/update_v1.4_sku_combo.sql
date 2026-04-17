-- ----------------------------
-- 商品 SKU 组合关联关系
-- 用于定义组合产品 (Bundle) 由哪些子 SKU 组成
-- ----------------------------

CREATE TABLE IF NOT EXISTS `erplus_product_sku_combo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `main_sku_id` bigint NOT NULL COMMENT '组合 SKU 编号 (Bundle SKU)',
  `child_sku_id` bigint NOT NULL COMMENT '子 SKU 编号 (Component SKU)',
  `quantity` double NOT NULL DEFAULT '1' COMMENT '包含数量',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_main_sku` (`main_sku_id`) USING BTREE,
  KEY `idx_child_sku` (`child_sku_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品 SKU 组合关联表';
