-- 耗材定义表
CREATE TABLE `erplus_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '耗材名称',
  `code` varchar(64) NOT NULL COMMENT '耗材编码',
  `category` varchar(64) DEFAULT NULL COMMENT '耗材类型',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `length` decimal(10,2) DEFAULT NULL COMMENT '长(cm)',
  `width` decimal(10,2) DEFAULT NULL COMMENT '宽(cm)',
  `height` decimal(10,2) DEFAULT NULL COMMENT '高(cm)',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量(kg)',
  `volume` decimal(10,4) DEFAULT NULL COMMENT '体积(m³)',
  `capacity` decimal(10,2) DEFAULT NULL COMMENT '容积',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材定义表';

-- 耗材库存表
CREATE TABLE `erplus_material_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NOT NULL COMMENT '耗材编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `quantity` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前库存数量',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_material_warehouse` (`material_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材库存表';

-- 商品耗材BOM表
CREATE TABLE `erplus_product_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sku_id` bigint(20) NOT NULL COMMENT '商品SKU编号',
  `material_id` bigint(20) NOT NULL COMMENT '耗材编号',
  `usage_quantity` decimal(10,2) NOT NULL DEFAULT '1.00' COMMENT '单个成品的用量',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品耗材BOM表';

-- 装配单表
CREATE TABLE `erplus_assembly_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `no` varchar(64) NOT NULL COMMENT '业务单号',
  `sku_id` bigint(20) NOT NULL COMMENT '目标SKU编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `planned_qty` decimal(10,2) NOT NULL COMMENT '计划生产数量',
  `actual_qty` decimal(10,2) DEFAULT NULL COMMENT '实际生产数量',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '生产批次号',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-待启动, 1-装配中, 2-已完成, 3-已取消',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_no` (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装配单表';

-- 装配单耗材明细表
CREATE TABLE `erplus_assembly_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint(20) NOT NULL COMMENT '关联装配单编号',
  `material_id` bigint(20) NOT NULL COMMENT '耗材编号',
  `expected_qty` decimal(10,2) NOT NULL COMMENT '应耗数量',
  `shortfall_qty` decimal(10,2) DEFAULT '0.00' COMMENT '缺料数量',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装配单耗材明细表';

-- 耗材库存明细表
CREATE TABLE `erplus_material_stock_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `material_id` bigint(20) NOT NULL COMMENT '耗材编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `count` decimal(10,2) NOT NULL COMMENT '出入库数量',
  `total_count` decimal(10,2) NOT NULL COMMENT '总库存量',
  `biz_type` tinyint(4) NOT NULL COMMENT '业务类型',
  `biz_id` bigint(20) NOT NULL COMMENT '业务编号',
  `biz_item_id` bigint(20) DEFAULT NULL COMMENT '业务项编号',
  `biz_no` varchar(64) DEFAULT NULL COMMENT '业务单号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材库存明细表';


-- 2026-04-15 添加耗材类型字段
ALTER TABLE `erplus_material` ADD COLUMN `category` varchar(64) DEFAULT NULL COMMENT '耗材类型' AFTER `code`;
