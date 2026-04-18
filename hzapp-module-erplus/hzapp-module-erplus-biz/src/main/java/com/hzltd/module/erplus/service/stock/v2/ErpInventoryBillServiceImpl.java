package com.hzltd.module.erplus.service.stock.v2;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillItemPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillItemDO;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseInventoryMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseMapper;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryBillItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryBillMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.enums.stock.ErpInventoryBillStatusEnum;
import com.hzltd.module.erplus.enums.stock.ErpItemTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.OPT_NOT_SUPPORT;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.STOCK_COUNT_NEGATIVE;

/**
 * ERP 库存账单 (V2) Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpInventoryBillServiceImpl implements ErpInventoryBillService {

    @Resource
    private ErpInventoryBillMapper inventoryBillMapper;
    @Resource
    private ErpInventoryBillItemMapper inventoryBillItemMapper;
    @Resource
    private ErpWarehouseInventoryMapper warehouseInventoryMapper;
    @Resource
    private ErpWarehouseMapper warehouseMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInventoryBill(ErpInventoryBillSaveReqVO createReqVO) {
        // 1. 校验并发约束
        validateWarehousePlatformRestriction(createReqVO);

        // 2. 生成账单编号
        String billCode = noRedisDAO.generate("INV");

        // 3. 插入账单主表
        ErpInventoryBillDO bill = BeanUtils.toBean(createReqVO, ErpInventoryBillDO.class)
                .setBillCode(billCode);
        
        // 状态流转：如果是调拨单，初始为待收货；否则直接完成
        if (ObjectUtil.equals(bill.getType(), 30)) {
            bill.setStatus(ErpInventoryBillStatusEnum.WAIT_RECEIVE);
        } else {
            bill.setStatus(ErpInventoryBillStatusEnum.COMPLETED);
        }

        inventoryBillMapper.insert(bill);

        // 4. 处理明细并更新库存
        List<ErpInventoryBillItemDO> items = BeanUtils.toBean(createReqVO.getItems(), ErpInventoryBillItemDO.class);
        for (ErpInventoryBillItemDO item : items) {
            item.setBillId(bill.getId());

            // --- 两步法调拨特殊处理 ---
            if (ObjectUtil.equals(bill.getType(), 30)) { // 调拨
                handleTransferStockUpdate(bill, item);
            } else {
                // 普通入库/出库/盘点/生产/装配
                handleStandardStockUpdate(bill, item);
            }
        }

        inventoryBillItemMapper.insertBatch(items);
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveInventoryBill(Long id) {
        // 1. 校验单据
        ErpInventoryBillDO bill = inventoryBillMapper.selectById(id);
        if (bill == null) {
            throw exception(OPT_NOT_SUPPORT, "单据不存在");
        }
        if (!ObjectUtil.equals(bill.getStatus(), ErpInventoryBillStatusEnum.WAIT_RECEIVE)) {
            throw exception(OPT_NOT_SUPPORT, "单据状态不可收货");
        }
        if (!ObjectUtil.equals(bill.getType(), 30)) {
            throw exception(OPT_NOT_SUPPORT, "仅调拨单支持收货确认");
        }

        // 2. 处理明细结转库存
        List<ErpInventoryBillItemDO> items = inventoryBillItemMapper.selectListByBillId(id);
        for (ErpInventoryBillItemDO item : items) {
            handleTransferReceiptStockUpdate(bill, item);
            inventoryBillItemMapper.updateById(item); // 更新快照信息
        }

        // 3. 更新单据状态为完成
        inventoryBillMapper.updateById(new ErpInventoryBillDO().setId(id)
                .setStatus(ErpInventoryBillStatusEnum.COMPLETED));
    }

    /**
     * 处理标准出入库逻辑 (IN/OUT/ADJUST)
     */
    private void handleStandardStockUpdate(ErpInventoryBillDO bill, ErpInventoryBillItemDO item) {
        String targetWarehouseIdStr = getTargetWarehouseId(bill);
        if (StrUtil.isEmpty(targetWarehouseIdStr) || !StrUtil.isNumeric(targetWarehouseIdStr)) {
            return;
        }
        Long warehouseId = Long.parseLong(targetWarehouseIdStr);
        
        ErpWarehouseInventoryDO inventory = getOrCreateInventory(warehouseId, item);

        // 自动处理正负号：入库单(10)取正，出库单(20)取负，盘点/调整(40)保留原始符号
        int mutationQty = item.getQty();
        if (ObjectUtil.equals(bill.getType(), 10)) {
            mutationQty = Math.abs(mutationQty);
        } else if (ObjectUtil.equals(bill.getType(), 20)) {
            mutationQty = -Math.abs(mutationQty);
        }
        item.setQty(mutationQty);

        // 计算新库存 (Standard: +/- total & available)
        int newTotal = inventory.getTotalCount() + mutationQty;
        if (newTotal < 0) {
            throw exception(STOCK_COUNT_NEGATIVE, 
                    ObjectUtil.defaultIfNull(item.getSellerSku(), item.getItemId().toString()), warehouseId,
                    inventory.getTotalCount(), mutationQty);
        }

        inventory.setTotalCount(newTotal);
        inventory.setAvailableCount(inventory.getAvailableCount() + mutationQty);
        warehouseInventoryMapper.updateById(inventory);
        item.setSnapshotQty(newTotal);
    }

    /**
     * 处理两步法调拨逻辑 (TRANSFER_OUT / TRANSFER_IN)
     */
    private void handleTransferStockUpdate(ErpInventoryBillDO bill, ErpInventoryBillItemDO item) {
        // 调拨发货 (TRANSFER_OUT): 扣减源仓 Physical, 增加目标仓 Transit
        if (StrUtil.isNotEmpty(bill.getFromId()) && StrUtil.isNumeric(bill.getFromId()) &&
            StrUtil.isNotEmpty(bill.getToId()) && StrUtil.isNumeric(bill.getToId())) {
            
            Long fromWarehouseId = Long.parseLong(bill.getFromId());
            Long toWarehouseId = Long.parseLong(bill.getToId());

            // 1. 源仓库：扣减库存 (锁定行以防并发超卖)
            ErpWarehouseInventoryDO fromInv = getOrCreateInventory(fromWarehouseId, item);
            int neededQty = Math.abs(item.getQty());
            if (fromInv.getTotalCount() < neededQty) {
                 throw exception(STOCK_COUNT_NEGATIVE, 
                         ObjectUtil.defaultIfNull(item.getSellerSku(), item.getItemId().toString()), 
                         fromWarehouseId, fromInv.getTotalCount(), -neededQty);
            }
            
            fromInv.setTotalCount(fromInv.getTotalCount() - neededQty);
            fromInv.setAvailableCount(fromInv.getAvailableCount() - neededQty);
            warehouseInventoryMapper.updateById(fromInv);

            // 2. 目标仓库：增加在途 (Transit)
            ErpWarehouseInventoryDO toInv = getOrCreateInventory(toWarehouseId, item);
            toInv.setTransitCount(ObjectUtil.defaultIfNull(toInv.getTransitCount(), 0) + Math.abs(item.getQty()));
            warehouseInventoryMapper.updateById(toInv);
            
            item.setSnapshotQty(fromInv.getTotalCount());
        } 
    }

    /**
     * 调拨入库逻辑 (结转在途库存)
     */
    private void handleTransferReceiptStockUpdate(ErpInventoryBillDO bill, ErpInventoryBillItemDO item) {
        if (StrUtil.isEmpty(bill.getToId()) || !StrUtil.isNumeric(bill.getToId())) {
            return;
        }
        Long warehouseId = Long.parseLong(bill.getToId());
        ErpWarehouseInventoryDO inventory = getOrCreateInventory(warehouseId, item);
        
        // 扣减在途
        int oldTransit = ObjectUtil.defaultIfNull(inventory.getTransitCount(), 0);
        inventory.setTransitCount(Math.max(0, oldTransit - Math.abs(item.getQty())));
        // 增加实物
        inventory.setTotalCount(inventory.getTotalCount() + Math.abs(item.getQty()));
        inventory.setAvailableCount(inventory.getAvailableCount() + Math.abs(item.getQty()));
        
        warehouseInventoryMapper.updateById(inventory);
        item.setSnapshotQty(inventory.getTotalCount());
    }

    private ErpWarehouseInventoryDO getOrCreateInventory(Long warehouseId, ErpInventoryBillItemDO item) {
        ErpWarehouseInventoryDO inventory = warehouseInventoryMapper
                .selectByWarehouseAndItemForUpdate(warehouseId, item.getItemType(), item.getItemId());
        
        if (inventory == null) {
            inventory = ErpWarehouseInventoryDO.builder()
                    .warehouseId(warehouseId)
                    .itemType(item.getItemType())
                    .itemId(item.getItemId())
                    .sellerSku(item.getSellerSku())
                    .totalCount(0)
                    .availableCount(0)
                    .transitCount(0)
                    .reservedCount(0)
                    .blockCount(0)
                    .build();
            warehouseInventoryMapper.insert(inventory);
        }
        return inventory;
    }

    /**
     * 校验平台仓限制
     */
    private void validateWarehousePlatformRestriction(ErpInventoryBillSaveReqVO vo) {
        if (ObjectUtil.equals(vo.getType(), 10) || ObjectUtil.equals(vo.getType(), 40)) {
            checkPlatformLimit(vo.getToId(), vo.getRefType());
        }
        if (ObjectUtil.equals(vo.getType(), 20) || ObjectUtil.equals(vo.getType(), 40)) {
            checkPlatformLimit(vo.getFromId(), vo.getRefType());
        }
        if (ObjectUtil.equals(vo.getType(), 30) || 
            StrUtil.equals(vo.getRefType(), "PRODUCTION") || 
            StrUtil.equals(vo.getRefType(), "ASSEMBLY")) {
            checkPlatformLimit(vo.getFromId(), vo.getRefType());
            checkPlatformLimit(vo.getToId(), vo.getRefType());
        }
    }

    private void checkPlatformLimit(String warehouseIdStr, String refType) {
        if (StrUtil.isEmpty(warehouseIdStr) || !StrUtil.isNumeric(warehouseIdStr)) {
            return;
        }
        Long warehouseId = Long.parseLong(warehouseIdStr);
        ErpWarehouseDO warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse != null && ObjectUtil.equals(warehouse.getType(), 0)) {
            if (!StrUtil.equals(refType, "SHIPMENT")) {
                throw exception(OPT_NOT_SUPPORT, "平台仓仅支持同步平台货件数据，禁止手动操作");
            }
        }
    }

    private String getTargetWarehouseId(ErpInventoryBillDO bill) {
        if (Integer.valueOf(10).equals(bill.getType())) return bill.getToId();
        if (Integer.valueOf(20).equals(bill.getType())) return bill.getFromId();
        if (Integer.valueOf(40).equals(bill.getType())) return bill.getToId() != null ? bill.getToId() : bill.getFromId();
        return null;
    }

    @Override public ErpInventoryBillDO getInventoryBill(Long id) { return inventoryBillMapper.selectById(id); }
    @Override public PageResult<ErpInventoryBillDO> getInventoryBillPage(ErpInventoryBillPageReqVO pageReqVO) { return inventoryBillMapper.selectPage(pageReqVO); }
    @Override public List<ErpInventoryBillItemDO> getInventoryBillItemListByBillId(Long billId) { return inventoryBillItemMapper.selectListByBillId(billId); }
    @Override public PageResult<ErpInventoryBillItemDO> getInventoryBillItemPage(ErpInventoryBillItemPageReqVO pageReqVO) { return inventoryBillItemMapper.selectPage(pageReqVO); }

}
