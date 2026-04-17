package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.exception.ErrorCode;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpProductMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyOrderDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyItemRespVO;
import com.hzltd.module.erplus.dal.mysql.material.ErpProductMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpAssemblyItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpAssemblyOrderMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.service.material.ErpMaterialStockRecordService;
import com.hzltd.module.erplus.service.material.ErpMaterialStockService;
import com.hzltd.module.erplus.service.material.bo.ErpMaterialStockRecordCreateReqBO;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import com.hzltd.module.erplus.service.spu.ProductSpuService;
import com.hzltd.module.erplus.service.stock.bo.ErpStockRecordCreateReqBO;
import com.hzltd.module.erplus.system.enums.ErpStockRecordBizTypeEnum;
import com.hzltd.module.erplus.service.cross.backup.ProductUnitService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.util.collection.CollectionUtils;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialMapper;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderRespVO.ShortfallItem;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

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
    private ErpMaterialMapper materialMapper;

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
    private ProductUnitService productUnitService;

    @Resource
    private ErpNoRedisDAO erpNoRedisDAO;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ProductSpuService productSpuService;



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
        // 1. 校验装配单状态：只有待启动 or 已取消的才能删除
        ErpAssemblyOrderDO order = assemblyOrderMapper.selectById(id);
        if (order == null) {
            throw exception(new ErrorCode(1_030_403_501, "装配单不存在"));
        }
        if (STATUS_ASSEMBLING.equals(order.getStatus()) || STATUS_COMPLETED.equals(order.getStatus())) {
            throw exception(new ErrorCode(1_030_403_503, "只有待启动或已取消的装配单才能删除"));
        }
        
        // 2. 执行物理/逻辑删除
        assemblyOrderMapper.deleteById(id);
        assemblyItemMapper.delete(ErpAssemblyItemDO::getOrderId, id);
    }

    @Override
    public ErpAssemblyOrderDO getAssemblyOrder(Long id) {
        return assemblyOrderMapper.selectById(id);
    }

    @Override
    public ErpAssemblyOrderRespVO getAssemblyOrderResp(Long id) {
        ErpAssemblyOrderDO order = getAssemblyOrder(id);
        if (order == null) {
            return null;
        }
        return enrichAssemblyOrders(Collections.singletonList(order)).get(0);
    }

    @Override
    public PageResult<ErpAssemblyOrderRespVO> getAssemblyOrderPage(ErpAssemblyOrderPageReqVO pageReqVO) {
        PageResult<ErpAssemblyOrderDO> pageResult = assemblyOrderMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        List<ErpAssemblyOrderRespVO> enrichedList = enrichAssemblyOrders(pageResult.getList());
        return new PageResult<>(enrichedList, pageResult.getTotal());
    }

    private List<ErpAssemblyOrderRespVO> enrichAssemblyOrders(Collection<ErpAssemblyOrderDO> orders) {
        if (CollUtil.isEmpty(orders)) {
            return Collections.emptyList();
        }

        // 1. 批量查询关联数据
        // 仓库名称
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                CollectionUtils.convertSet(orders, ErpAssemblyOrderDO::getWarehouseId));
        // 缺料情况
        Map<Long, List<ShortfallItem>> shortfallMap = getAssemblyOrderShortfallItemsMap(
                CollectionUtils.convertSet(orders, ErpAssemblyOrderDO::getId));
        // SKU & SPU 元数据
        List<ProductSkuDO> skuList = productSkuService.getSkuList(
                CollectionUtils.convertSet(orders, ErpAssemblyOrderDO::getSkuId));
        Map<Long, ProductSkuDO> skuMap = CollectionUtils.convertMap(skuList, ProductSkuDO::getId);
        Map<Long, ProductSpuDO> spuMap = productSpuService.getSpuMap(
                CollectionUtils.convertSet(skuList, ProductSkuDO::getSpuId));

        // 2. 转换并填充 VO
        return BeanUtils.toBean(new ArrayList<>(orders), ErpAssemblyOrderRespVO.class, order -> {
            // 填充状态名
            order.setStatusName(getStatusName(order.getStatus()));
            // 填充仓库名
            MapUtils.findAndThen(warehouseMap, order.getWarehouseId(),
                    warehouse -> order.setWarehouseName(warehouse.getName()));
            // 填充缺料信息
            order.setShortfallItems(shortfallMap.get(order.getId()));
            order.setHasShortfall(CollUtil.isNotEmpty(order.getShortfallItems()));
            // 填充 SKU 元数据 (SPU名称 + SKU编码)
            MapUtils.findAndThen(skuMap, order.getSkuId(), sku -> {
                order.setSkuCode(sku.getCode());
                MapUtils.findAndThen(spuMap, sku.getSpuId(), spu -> order.setSkuName(spu.getName()));
            });
        });
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待启动";
            case 1 -> "装配中";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "";
        };
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
        
        // 验证当前仓库耗材库存，计算缺货量（批量优化）
        List<ErpAssemblyItemDO> items = assemblyItemMapper.selectListByOrderId(id);
        Map<Long, BigDecimal> stockMap = materialStockService.getMaterialStockCountMap(
                CollectionUtils.convertSet(items, ErpAssemblyItemDO::getMaterialId), order.getWarehouseId());
        
        boolean hasShortfall = false;
        for (ErpAssemblyItemDO item : items) {
            BigDecimal stockQty = stockMap.getOrDefault(item.getMaterialId(), BigDecimal.ZERO);
            if (stockQty.compareTo(item.getExpectedQty()) < 0) {
                // 如果库存小于预期，记录缺货数量
                BigDecimal shortfall = item.getExpectedQty().subtract(stockQty);
                item.setShortfallQty(shortfall);
                hasShortfall = true;
            } else {
                item.setShortfallQty(BigDecimal.ZERO);
            }
            assemblyItemMapper.updateById(item);
        }
        
        if (hasShortfall) {
            throw exception(new ErrorCode(1_030_403_500, "耗材库存不足，无法启动装配，请补充库存后重试"));
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
            throw exception(new ErrorCode(1_030_403_501, "装配单不存在"));
        }
        if (!STATUS_ASSEMBLING.equals(order.getStatus())) {
            throw exception(new ErrorCode(1_030_403_502, "只有装配中状态的装配单才能完成"));
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
        Map<Long, BigDecimal> stockMap = materialStockService.getMaterialStockCountMap(
                CollectionUtils.convertSet(items, ErpAssemblyItemDO::getMaterialId), order.getWarehouseId());
        
        for (ErpAssemblyItemDO item : items) {
            BigDecimal stockQty = stockMap.getOrDefault(item.getMaterialId(), BigDecimal.ZERO);
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
    public List<ErpAssemblyItemRespVO> getAssemblyItemList(Long orderId) {
        List<ErpAssemblyItemDO> items = assemblyItemMapper.selectListByOrderId(orderId);
        if (CollUtil.isEmpty(items)) {
            return List.of();
        }
        
        ErpAssemblyOrderDO order = assemblyOrderMapper.selectById(orderId);
        if (order != null && STATUS_PENDING.equals(order.getStatus())) {
            // 如果是待启动状态，执行实时库存校验并更新返回结果中的缺货量（批量优化）
            Map<Long, BigDecimal> stockMap = materialStockService.getMaterialStockCountMap(
                    CollectionUtils.convertSet(items, ErpAssemblyItemDO::getMaterialId), order.getWarehouseId());
            
            for (ErpAssemblyItemDO item : items) {
                BigDecimal stockQty = stockMap.getOrDefault(item.getMaterialId(), BigDecimal.ZERO);
                if (stockQty.compareTo(item.getExpectedQty()) < 0) {
                    item.setShortfallQty(item.getExpectedQty().subtract(stockQty));
                } else {
                    item.setShortfallQty(BigDecimal.ZERO);
                }
            }
        }

        // 批量查询物料基础信息和单位名称，完成数据脱壳/聚合
        List<ErpMaterialDO> materials = materialMapper.selectBatchIds(CollectionUtils.convertSet(items, ErpAssemblyItemDO::getMaterialId));
        Map<Long, ErpMaterialDO> materialMap = CollectionUtils.convertMap(materials, ErpMaterialDO::getId);
        Map<Long, String> unitMap = productUnitService.getProductUnitNameMap();

        return BeanUtils.toBean(items, ErpAssemblyItemRespVO.class, itemVO -> {
            ErpMaterialDO material = materialMap.get(itemVO.getMaterialId());
            if (material != null) {
                itemVO.setMaterialName(material.getName());
                itemVO.setMaterialCode(material.getCode());
                itemVO.setMaterialUnit(unitMap.get(material.getUnit()));
            }
            itemVO.setIsShortfall(itemVO.getShortfallQty() != null && itemVO.getShortfallQty().compareTo(BigDecimal.ZERO) > 0);
        });
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

    @Override
    public Map<Long, List<ShortfallItem>> getAssemblyOrderShortfallItemsMap(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Collections.emptyMap();
        }
        // 1. 获取所有明细
        List<ErpAssemblyItemDO> items = assemblyItemMapper.selectList(new LambdaQueryWrapper<ErpAssemblyItemDO>()
                .in(ErpAssemblyItemDO::getOrderId, orderIds));
        if (CollUtil.isEmpty(items)) {
            return Collections.emptyMap();
        }
        
        // 2. 获取对应的装配单（主要为了获取仓库 ID）
        List<ErpAssemblyOrderDO> orders = assemblyOrderMapper.selectBatchIds(orderIds);
        Map<Long, ErpAssemblyOrderDO> orderMap = CollectionUtils.convertMap(orders, ErpAssemblyOrderDO::getId);
        
        // 3. 获取对应的耗材信息
        Set<Long> materialIds = CollectionUtils.convertSet(items, ErpAssemblyItemDO::getMaterialId);
        List<ErpMaterialDO> materials = materialMapper.selectBatchIds(materialIds);
        Map<Long, ErpMaterialDO> materialMap = CollectionUtils.convertMap(materials, ErpMaterialDO::getId);
        
        // 4. 按 OrderId 分组并计算实时缺货
        Map<Long, String> unitMap = productUnitService.getProductUnitNameMap();
        Map<Long, List<ShortfallItem>> result = new HashMap<>();
        
        // 批量预取所有待启动订单所需耗材的库存 (优化 1:N 问题)
        List<Long> pendingOrderIds = orders.stream().filter(o -> STATUS_PENDING.equals(o.getStatus()))
                .map(ErpAssemblyOrderDO::getId).collect(Collectors.toList());
        Map<String, BigDecimal> multiStockMap = new HashMap<>(); // key: warehouseId + "_" + materialId
        if (CollUtil.isNotEmpty(pendingOrderIds)) {
            // 简单处理：获取所有涉及的仓库和物料组合。由于装配单通常针对一个特定仓库，我们可以按仓库分组拿
            Map<Long, List<ErpAssemblyOrderDO>> warehouseGroup = orders.stream()
                    .filter(o -> STATUS_PENDING.equals(o.getStatus()))
                    .collect(Collectors.groupingBy(ErpAssemblyOrderDO::getWarehouseId));
            
            warehouseGroup.forEach((warehouseId, warehouseOrders) -> {
                Set<Long> wOrderIds = CollectionUtils.convertSet(warehouseOrders, ErpAssemblyOrderDO::getId);
                Set<Long> wMaterialIds = items.stream()
                        .filter(i -> wOrderIds.contains(i.getOrderId()))
                        .map(ErpAssemblyItemDO::getMaterialId).collect(Collectors.toSet());
                Map<Long, BigDecimal> wStockMap = materialStockService.getMaterialStockCountMap(wMaterialIds, warehouseId);
                wStockMap.forEach((mId, qty) -> multiStockMap.put(warehouseId + "_" + mId, qty));
            });
        }

        for (ErpAssemblyItemDO item : items) {
            ErpAssemblyOrderDO order = orderMap.get(item.getOrderId());
            if (order == null) continue;
            
            BigDecimal shortfall = item.getShortfallQty();
            // 如果是待启动状态，执行实时库存校验
            if (STATUS_PENDING.equals(order.getStatus())) {
                BigDecimal stockQty = multiStockMap.getOrDefault(order.getWarehouseId() + "_" + item.getMaterialId(), BigDecimal.ZERO);
                if (stockQty.compareTo(item.getExpectedQty()) < 0) {
                    shortfall = item.getExpectedQty().subtract(stockQty);
                } else {
                    shortfall = BigDecimal.ZERO;
                }
            }
            
            if (shortfall != null && shortfall.compareTo(BigDecimal.ZERO) > 0) {
                ShortfallItem shortfallItem = new ShortfallItem();
                ErpMaterialDO material = materialMap.get(item.getMaterialId());
                shortfallItem.setMaterialName(material != null ? material.getName() : "未知耗材");
                shortfallItem.setMaterialUnit(material != null ? unitMap.get(material.getUnit()) : "");
                shortfallItem.setExpectedQty(item.getExpectedQty());
                shortfallItem.setShortfallQty(shortfall);
                
                result.computeIfAbsent(item.getOrderId(), k -> new ArrayList<>()).add(shortfallItem);
            }
        }
        return result;
    }

}
