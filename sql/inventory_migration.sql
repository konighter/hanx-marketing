-- 1. 扩展 erplus_warehouse_inventory (仓库库存表)
ALTER TABLE erplus_warehouse_inventory 
    MODIFY COLUMN id BIGINT AUTO_INCREMENT COMMENT 'ID',
    MODIFY COLUMN warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    ADD COLUMN item_type TINYINT NOT NULL DEFAULT 1 COMMENT '物料类型 (1: SKU, 2: 耗材)' AFTER warehouse_id,
    ADD COLUMN item_id BIGINT NOT NULL COMMENT '物料ID' AFTER item_type,
    ADD COLUMN transit_count INT NOT NULL DEFAULT 0 COMMENT '在途数量' AFTER available_count,
    MODIFY COLUMN seller_sku VARCHAR(64) DEFAULT NULL COMMENT 'SKU (可选)';

-- 2. 扩展 erplus_inventory_bill (库存账单主表)
ALTER TABLE erplus_inventory_bill 
    MODIFY COLUMN from_id VARCHAR(64) DEFAULT NULL COMMENT '来源ID',
    MODIFY COLUMN to_id VARCHAR(64) DEFAULT NULL COMMENT '去向ID';

-- 3. 扩展 erplus_inventory_bill_item (库存账单明细表)
ALTER TABLE erplus_inventory_bill_item 
    ADD COLUMN item_type TINYINT NOT NULL DEFAULT 1 COMMENT '物料类型 (1: SKU, 2: 耗材)' AFTER bill_id,
    ADD COLUMN item_id BIGINT NOT NULL COMMENT '物料ID' AFTER item_type,
    MODIFY COLUMN seller_sku VARCHAR(64) DEFAULT NULL COMMENT 'SKU (可选)';

-- 4. 数据重置与初始化 (现有商品记录)
UPDATE erplus_warehouse_inventory SET item_type = 1 WHERE item_type IS NULL OR item_type = 1;

-- 5. 耗材库存平迁 (Data Migration from erplus_material_stock)
INSERT INTO erplus_warehouse_inventory (warehouse_id, item_type, item_id, total_count, available_count, transit_count, reserved_count, block_count, create_time, creator)
SELECT 
    warehouse_id, 
    2 as item_type, 
    material_id as item_id, 
    CAST(quantity AS SIGNED) as total_count, 
    CAST(quantity AS SIGNED) as available_count, 
    0 as transit_count,
    0 as reserved_count, 
    0 as block_count,
    NOW(),
    'SYSTEM'
FROM erplus_material_stock;

-- 6. 索引重构 (唯一性保证)
CREATE UNIQUE INDEX uk_warehouse_item ON erplus_warehouse_inventory (warehouse_id, item_type, item_id);

-- 7. 账单明细数据补全 (历史数据)
UPDATE erplus_inventory_bill_item SET item_type = 1 WHERE item_type IS NULL;

-- 8. 扩展 erplus_stock_move (调拨单主表)
ALTER TABLE erplus_stock_move
    ADD COLUMN from_warehouse_id BIGINT COMMENT '调出仓库ID' AFTER no,
    ADD COLUMN to_warehouse_id BIGINT COMMENT '调入仓库ID' AFTER from_warehouse_id,
    ADD COLUMN out_bill_id BIGINT COMMENT '调出账单ID' AFTER file_url,
    ADD COLUMN in_bill_id BIGINT COMMENT '调入账单ID' AFTER out_bill_id;

-- 9. 扩展 erplus_stock_move_item (调拨单明细表)
ALTER TABLE erplus_stock_move_item
    ADD COLUMN item_type TINYINT NOT NULL DEFAULT 1 COMMENT '物料类型 (1: SKU, 2: 耗材)' AFTER move_id,
    ADD COLUMN item_id BIGINT NOT NULL COMMENT '物料ID' AFTER item_type,
    MODIFY COLUMN product_id BIGINT DEFAULT NULL COMMENT '原商品ID (冗余)',
    DROP COLUMN from_warehouse_id,
    DROP COLUMN to_warehouse_id;

-- 10. 调拨单历史数据修复
UPDATE erplus_stock_move_item SET item_type = 1, item_id = product_id WHERE item_type IS NULL OR item_type = 1;

-- 11. 初始化调拨单主表仓库ID (基于明细项归并)
UPDATE erplus_stock_move m SET 
    from_warehouse_id = (SELECT from_warehouse_id FROM erplus_stock_move_item mi WHERE mi.move_id = m.id LIMIT 1),
    to_warehouse_id = (SELECT to_warehouse_id FROM erplus_stock_move_item mi WHERE mi.move_id = m.id LIMIT 1);
