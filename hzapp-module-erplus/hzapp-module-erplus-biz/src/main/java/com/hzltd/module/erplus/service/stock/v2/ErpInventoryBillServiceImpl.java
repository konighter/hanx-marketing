package com.hzltd.module.erplus.service.stock.v2;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillItemPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillItemDO;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseInventoryMapper;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryBillItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryBillMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.STOCK_COUNT_NEGATIVE;

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
    private ErpNoRedisDAO noRedisDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInventoryBill(ErpInventoryBillSaveReqVO createReqVO) {
        // 1. 生成账单编号
        String billCode = noRedisDAO.generate("INV");

        // 2. 插入账单主表
        ErpInventoryBillDO bill = BeanUtils.toBean(createReqVO, ErpInventoryBillDO.class)
                .setBillCode(billCode)
                .setStatus(10); // 10:完成

        inventoryBillMapper.insert(bill);

        // 3. 处理明细并更新库存
        List<ErpInventoryBillItemDO> items = BeanUtils.toBean(createReqVO.getItems(), ErpInventoryBillItemDO.class);
        for (ErpInventoryBillItemDO item : items) {
            item.setBillId(bill.getId());

            // 确定要更新的仓库ID (基于单据类型)
            Long targetWarehouseId = getTargetWarehouseId(bill);
            if (targetWarehouseId != null) {
                // 锁定并查询当前最新库存 (Pessimistic Locking)
                ErpWarehouseInventoryDO inventory = warehouseInventoryMapper
                        .selectByWarehouseIdAndSkuForUpdate(targetWarehouseId, item.getSellerSku());
                if (inventory == null) {
                    // 如果不存在，则创建一个新的初始记录
                    inventory = ErpWarehouseInventoryDO.builder()
                            .warehouseId(targetWarehouseId.intValue())
                            .sellerSku(item.getSellerSku())
                            .totalCount(0)
                            .availableCount(0)
                            .reservedCount(0)
                            .blockCount(0)
                            .build();
                    warehouseInventoryMapper.insert(inventory);
                }

                // 计算新库存
                int newTotal = inventory.getTotalCount() + item.getQty();
                if (newTotal < 0) {
                    throw exception(STOCK_COUNT_NEGATIVE, item.getSellerSku(), targetWarehouseId,
                            inventory.getTotalCount(), item.getQty());
                }

                // 更新库存表
                inventory.setTotalCount(newTotal);
                inventory.setAvailableCount(inventory.getAvailableCount() + item.getQty());
                warehouseInventoryMapper.updateById(inventory);

                // 记录期末快照
                item.setSnapshotQty(newTotal);
            }
        }

        // 4. 插入明细表
        inventoryBillItemMapper.insertBatch(items);
        return bill.getId();
    }

    private Long getTargetWarehouseId(ErpInventoryBillDO bill) {
        // 逻辑简化：根据单据大类确定影响哪个仓库的物理库存
        if (Integer.valueOf(10).equals(bill.getType())) { // 入库
            return bill.getToId();
        } else if (Integer.valueOf(20).equals(bill.getType())) { // 出库
            return bill.getFromId();
        } else if (Integer.valueOf(30).equals(bill.getType())) { // 调拨 (发货方)
            // 调拨在 V2 中建议拆分为两个 Bill：一个出库单由 fromId 出，一个入库单由 toId 入。
            // 这里的简单处理仅示例。
            return bill.getFromId();
        } else if (Integer.valueOf(40).equals(bill.getType())) { // 调整
            return bill.getToId() != null ? bill.getToId() : bill.getFromId();
        }
        return null;
    }

    @Override
    public ErpInventoryBillDO getInventoryBill(Long id) {
        return inventoryBillMapper.selectById(id);
    }

    @Override
    public PageResult<ErpInventoryBillDO> getInventoryBillPage(ErpInventoryBillPageReqVO pageReqVO) {
        return inventoryBillMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ErpInventoryBillItemDO> getInventoryBillItemListByBillId(Long billId) {
        return inventoryBillItemMapper.selectListByBillId(billId);
    }

    @Override
    public PageResult<ErpInventoryBillItemDO> getInventoryBillItemPage(ErpInventoryBillItemPageReqVO pageReqVO) {
        return inventoryBillItemMapper.selectPage(pageReqVO);
    }

}
