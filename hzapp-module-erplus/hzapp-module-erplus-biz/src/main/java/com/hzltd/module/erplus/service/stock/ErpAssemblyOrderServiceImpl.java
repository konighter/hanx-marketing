package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpProductMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyOrderDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpProductMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpAssemblyItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpAssemblyOrderMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.service.material.ErpMaterialStockRecordService;
import com.hzltd.module.erplus.service.material.ErpMaterialStockService;
import com.hzltd.module.erplus.service.material.bo.ErpMaterialStockRecordCreateReqBO;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import com.hzltd.module.erplus.service.stock.bo.ErpStockRecordCreateReqBO;
import com.hzltd.module.erplus.system.enums.ErpStockRecordBizTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

/**
 * ERP 装配单 Service 实现类
 *
 * 处理产成品装配业务逻辑，包括 BOM 自动展开、库存占用验证、成品入库及耗材自动核减。
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpAssemblyOrderServiceImpl implements ErpAssemblyOrderService {

    /**
     * 装配单状态：待启动
     */
    private static final Integer STATUS_PENDING = 0;
    /**
     * 装配单状态：装配中（已锁定库存或进入排产）
     */
    private static final Integer STATUS_ASSEMBLING = 1;
    /**
     * 装配单状态：已完成（库存已交割）
     */
    private static final Integer STATUS_COMPLETED = 2;
    /**
     * 装配单状态：已取消
     */
    private static final Integer STATUS_CANCELLED = 3;

    @Resource
    private ErpAssemblyOrderMapper assemblyOrderMapper;
    @Resource
    private ErpAssemblyItemMapper assemblyItemMapper;
    @Resource
    private ErpProductMaterialMapper productMaterialMapper;

    @Resource
    private ErpMaterialStockService materialStockService;
    @Resource
    private ErpMaterialStockRecordService materialStockRecordService;
    @Resource
    private ErpStockService stockService;
    @Resource
    private ErpStockRecordService stockRecordService;
    @Resource
    private ProductSkuService productSkuService;

    @Resource
    private ErpNoRedisDAO erpNoRedisDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAssemblyOrder(ErpAssemblyOrderSaveReqVO reqVO) {
        ErpAssemblyOrderDO order = BeanUtils.toBean(reqVO, ErpAssemblyOrderDO.class);
        
        // 1. 生成唯一的装配单号（使用 Redis 递增，确保高并发无重复）
        String no = erpNoRedisDAO.generate(ErpNoRedisDAO.ASSEMBLY_ORDER_NO_PREFIX);
        order.setNo(no);
        order.setStatus(STATUS_PENDING);
        
        // 默认实际数量等于计划数量
        if (order.getActualQty() == null) {
            order.setActualQty(order.getPlannedQty());
        }
        
        assemblyOrderMapper.insert(order);
        
        // 2. 根据 SPU 的 BOM 配置自动生成耗材需求明细
        createAssemblyItems(order);
        
        return order.getId();
    }

    @Override
    public void updateAssemblyOrder(ErpAssemblyOrderSaveReqVO reqVO) {
        ErpAssemblyOrderDO order = BeanUtils.toBean(reqVO, ErpAssemblyOrderDO.class);
        assemblyOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAssemblyOrder(Long id) {
        assemblyOrderMapper.deleteById(id);
        assemblyItemMapper.delete(ErpAssemblyItemDO::getOrderId, id);
    }

    @Override
    public ErpAssemblyOrderDO getAssemblyOrder(Long id) {
        return assemblyOrderMapper.selectById(id);
    }

    @Override
    public PageResult<ErpAssemblyOrderDO> getAssemblyOrderPage(ErpAssemblyOrderPageReqVO pageReqVO) {
        return assemblyOrderMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startAssemblyOrder(Long id) {
        ErpAssemblyOrderDO order = assemblyOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("装配单不存在");
        }
        if (!STATUS_PENDING.equals(order.getStatus())) {
            throw new RuntimeException("只有待启动状态的装配单才能启动");
        }
        
        // 验证当前仓库耗材库存，计算缺货量
        List<ErpAssemblyItemDO> items = assemblyItemMapper.selectListByOrderId(id);
        for (ErpAssemblyItemDO item : items) {
            BigDecimal stockQty = materialStockService.getMaterialStockCount(item.getMaterialId(), order.getWarehouseId());
            if (stockQty.compareTo(item.getExpectedQty()) < 0) {
                // 如果库存小于预期，记录缺货数量
                item.setShortfallQty(item.getExpectedQty().subtract(stockQty));
            } else {
                item.setShortfallQty(BigDecimal.ZERO);
            }
            assemblyItemMapper.updateById(item);
        }
        
        order.setStatus(STATUS_ASSEMBLING);
        assemblyOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAssemblyOrder(Long id) {
        // 1. 校验装配单状态
        ErpAssemblyOrderDO order = assemblyOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("装配单不存在");
        }
        if (!STATUS_ASSEMBLING.equals(order.getStatus())) {
            throw new RuntimeException("只有装配中状态的装配单才能完成");
        }
        
        // 2. 最终库存校验（防止在装配过程中耗材被其他单据领用）
        List<ErpAssemblyItemDO> items = assemblyItemMapper.selectListByOrderId(id);
        validateAndRecalculateShortfall(order, items);
        
        // 3. 生成生产批次号
        String batchNo = erpNoRedisDAO.generate(ErpNoRedisDAO.ASSEMBLY_BATCH_NO_PREFIX);
        order.setBatchNo(batchNo);
        
        // 4. 执行成品入库逻辑
        BigDecimal actualQty = order.getActualQty() != null ? order.getActualQty() : order.getPlannedQty();
        stockService.updateStockCountIncrement(order.getSkuId(), order.getWarehouseId(), actualQty);
        
        // 写入产成品库存明细记录
        stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                order.getSkuId(), order.getWarehouseId(), actualQty,
                ErpStockRecordBizTypeEnum.ASSEMBLY_IN.getType(), order.getId(), order.getId(), order.getNo()
        ));
        
        // 5. 执行耗材核减逻辑（扣除耗材库存）
        for (ErpAssemblyItemDO item : items) {
            // 通过 RecordService 确保在更新库存的同时记录审计日志
            materialStockRecordService.createMaterialStockRecord(new ErpMaterialStockRecordCreateReqBO(
                    item.getMaterialId(), order.getWarehouseId(), item.getExpectedQty().negate(),
                    ErpStockRecordBizTypeEnum.ASSEMBLY_OUT.getType(), order.getId(), item.getId(), order.getNo()
            ));
        }
        
        // 6. 更新装配单为已完成
        order.setStatus(STATUS_COMPLETED);
        assemblyOrderMapper.updateById(order);
    }

    /**
     * 校验并重新计算缺货量，如有不足则抛出异常
     */
    private void validateAndRecalculateShortfall(ErpAssemblyOrderDO order, List<ErpAssemblyItemDO> items) {
        boolean hasInsufficientStock = false;
        for (ErpAssemblyItemDO item : items) {
            BigDecimal stockQty = materialStockService.getMaterialStockCount(item.getMaterialId(), order.getWarehouseId());
            if (stockQty.compareTo(item.getExpectedQty()) < 0) {
                hasInsufficientStock = true;
                BigDecimal shortfall = item.getExpectedQty().subtract(stockQty);
                item.setShortfallQty(shortfall);
            } else {
                item.setShortfallQty(BigDecimal.ZERO);
            }
            assemblyItemMapper.updateById(item);
        }
        
        if (hasInsufficientStock) {
            throw new RuntimeException("耗材库存不足，无法完成装配，请补充库存后重试");
        }
    }

    @Override
    public void cancelAssemblyOrder(Long id) {
        ErpAssemblyOrderDO order = assemblyOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("装配单不存在");
        }
        if (STATUS_COMPLETED.equals(order.getStatus())) {
            throw new RuntimeException("已完成的装配单无法取消");
        }
        
        order.setStatus(STATUS_CANCELLED);
        assemblyOrderMapper.updateById(order);
    }

    @Override
    public List<ErpAssemblyItemDO> getAssemblyItemList(Long orderId) {
        return assemblyItemMapper.selectListByOrderId(orderId);
    }

    /**
     * 根据 SKU 的 BOM 展开计算耗材需求
     */
    private void createAssemblyItems(ErpAssemblyOrderDO order) {
        // 直接根据 SKU 编号获取绑定的耗材 BOM
        List<ErpProductMaterialDO> bomList = productMaterialMapper.selectListBySkuId(order.getSkuId());
        for (ErpProductMaterialDO bom : bomList) {
            ErpAssemblyItemDO item = new ErpAssemblyItemDO();
            item.setOrderId(order.getId());
            item.setMaterialId(bom.getMaterialId());
            // 计算预期消耗量 = 单个成品消耗量 * 计划生产数量
            item.setExpectedQty(bom.getUsageQuantity().multiply(order.getPlannedQty()));
            item.setShortfallQty(BigDecimal.ZERO);
            assemblyItemMapper.insert(item);
        }
    }

}
