package com.hzltd.module.erplus.service.material;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialSaveReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.stock.ErpMaterialStockPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.stock.ErpMaterialStockRespVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseInventoryMapper;
import com.hzltd.module.erplus.service.stock.ErpWarehouseService;
import com.hzltd.module.erplus.system.enums.ErpItemTypeEnum;
import com.hzltd.framework.common.util.collection.MapUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * ERP 耗材 Service 实现类
 *
 * 管理耗材的基础档案信息。
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpMaterialServiceImpl implements ErpMaterialService {

    @Resource
    private ErpMaterialMapper materialMapper;
    @Resource
    private ErpWarehouseInventoryMapper warehouseInventoryMapper;
    @Resource
    private ErpWarehouseService warehouseService;

    @Override
    public BigDecimal getMaterialStockCount(Long materialId) {
        List<ErpWarehouseInventoryDO> stocks = warehouseInventoryMapper.selectList(
                new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                        .eq(ErpWarehouseInventoryDO::getItemType, ErpItemTypeEnum.MATERIAL)
                        .eq(ErpWarehouseInventoryDO::getItemId, materialId));
        return stocks.stream()
                .map(s -> BigDecimal.valueOf(s.getTotalCount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getMaterialStockCount(Long materialId, Long warehouseId) {
        ErpWarehouseInventoryDO stock = warehouseInventoryMapper.selectByWarehouseAndItem(
                warehouseId, ErpItemTypeEnum.MATERIAL, materialId);
        return stock != null ? BigDecimal.valueOf(stock.getTotalCount()) : BigDecimal.ZERO;
    }

    @Override
    public Map<Long, BigDecimal> getMaterialStockCountMap(Collection<Long> materialIds) {
        if (CollUtil.isEmpty(materialIds)) {
            return Map.of();
        }
        // 1. 批量获取库存记录（使用统一库存表 erplus_warehouse_inventory，itemType=2）
        List<ErpWarehouseInventoryDO> stocks = warehouseInventoryMapper.selectListByItemTypeAndItemIds(
                ErpItemTypeEnum.MATERIAL, materialIds);
        
        // 2. 按耗材 ID 分组并累加库存数量
        return stocks.stream().collect(Collectors.groupingBy(
                ErpWarehouseInventoryDO::getItemId,
                Collectors.reducing(BigDecimal.ZERO,
                        s -> BigDecimal.valueOf(s.getTotalCount()),
                        BigDecimal::add)
        ));
    }

    @Override
    public Long createMaterial(ErpMaterialSaveReqVO reqVO) {
        // 转换并插入耗材档案
        ErpMaterialDO material = BeanUtils.toBean(reqVO, ErpMaterialDO.class);
        materialMapper.insert(material);
        return material.getId();
    }

    @Override
    public void updateMaterial(ErpMaterialSaveReqVO reqVO) {
        // 更新耗材档案信息
        ErpMaterialDO material = BeanUtils.toBean(reqVO, ErpMaterialDO.class);
        materialMapper.updateById(material);
    }

    @Override
    public void deleteMaterial(Long id) {
        // 删除耗材档案
        materialMapper.deleteById(id);
    }

    @Override
    public ErpMaterialDO getMaterial(Long id) {
        // 获取指定 ID 的耗材档案
        return materialMapper.selectById(id);
    }

    @Override
    public List<ErpMaterialDO> getMaterialList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return List.of();
        }
        // 批量查询耗材档案
        return materialMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ErpMaterialDO> getMaterialPage(ErpMaterialPageReqVO pageReqVO) {
        // 分页查询耗材档案
        return materialMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<Long, ErpMaterialDO> getMaterialMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        // 查询并转换为 Map 结构，方便业务层进行数据装配
        List<ErpMaterialDO> list = getMaterialList(ids);
        return list.stream().collect(Collectors.toMap(ErpMaterialDO::getId, m -> m));
    }

    @Override
    public PageResult<ErpMaterialStockRespVO> getMaterialStockPage(ErpMaterialStockPageReqVO pageReqVO) {
        // 1. 查询统一库存表 (itemType=MATERIAL)
        PageResult<ErpWarehouseInventoryDO> pageResult = warehouseInventoryMapper.selectPage(
                pageReqVO,
                new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                        .eq(ErpWarehouseInventoryDO::getItemType, ErpItemTypeEnum.MATERIAL)
                        .eqIfPresent(ErpWarehouseInventoryDO::getWarehouseId, pageReqVO.getWarehouseId())
                        .eqIfPresent(ErpWarehouseInventoryDO::getItemId, pageReqVO.getMaterialId())
                        .orderByDesc(ErpWarehouseInventoryDO::getId));
        
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        
        // 2. 批量查询关联数据（物料档案和仓库信息）
        Map<Long, ErpMaterialDO> materialMap = getMaterialMap(
                com.hzltd.framework.common.util.collection.CollectionUtils.convertList(
                        pageResult.getList(), ErpWarehouseInventoryDO::getItemId));
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                com.hzltd.framework.common.util.collection.CollectionUtils.convertList(
                        pageResult.getList(), ErpWarehouseInventoryDO::getWarehouseId));
        
        // 3. 封装返回结果
        return new PageResult<>(com.hzltd.framework.common.util.collection.CollectionUtils.convertList(pageResult.getList(), stockDO -> {
            ErpMaterialStockRespVO respVO = new ErpMaterialStockRespVO();
            respVO.setId(stockDO.getId());
            respVO.setMaterialId(stockDO.getItemId());
            respVO.setWarehouseId(stockDO.getWarehouseId());
            respVO.setQuantity(BigDecimal.valueOf(stockDO.getTotalCount()));
            respVO.setCreateTime(stockDO.getCreateTime());
            
            // 填充元数据
            MapUtils.findAndThen(materialMap, stockDO.getItemId(), material -> {
                respVO.setMaterialName(material.getName());
                respVO.setMaterialCode(material.getCode());
            });
            MapUtils.findAndThen(warehouseMap, stockDO.getWarehouseId(), 
                    warehouse -> respVO.setWarehouseName(warehouse.getName()));
            return respVO;
        }), pageResult.getTotal());
    }

    @Override
    public Map<Long, Integer> getMaterialAvailableStockMap(Long warehouseId, Collection<Long> materialIds) {
        if (CollUtil.isEmpty(materialIds)) {
            return Map.of();
        }
        List<ErpWarehouseInventoryDO> stocks = warehouseInventoryMapper.selectListByWarehouseAndItems(
                warehouseId, ErpItemTypeEnum.MATERIAL, materialIds);
        return stocks.stream().collect(Collectors.toMap(
                ErpWarehouseInventoryDO::getItemId,
                ErpWarehouseInventoryDO::getAvailableCount
        ));
    }

}
