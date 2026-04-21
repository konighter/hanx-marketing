package com.hzltd.module.erplus.service.stock;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.warehouse.ErpWarehouseInventoryPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSpuMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseInventoryMapper;
import com.hzltd.module.erplus.service.cross.ErplusCrossProductService;
import com.hzltd.module.erplus.spapi.enums.WarehouseTypeEnum;
import com.hzltd.module.erplus.system.enums.ErpItemTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ERP 库存 Service 实现类
 *
 * @author 翰展科技
 */
@Service("erplusStockService")
public class ErplusStockServiceImpl extends ErpStockServiceImpl implements ErplusStockService {

    @Resource
    private ErpWarehouseService warehouseService;

    @Resource
    private ErplusCrossProductService crossProductService;

    @Resource
    private ErpWarehouseInventoryMapper warehouseInventoryMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ProductSpuMapper productSpuMapper;
    @Resource
    private ErpMaterialMapper erpMaterialMapper;

    @Override
    public List<ErpTransferAvailableRespVO> getTransferAvailableStock(ErpTransferAvailableReqVO reqVO) {
        if (reqVO.getWarehouseId().equals(reqVO.getInboundWarehouseId())) {
            throw new IllegalArgumentException("出库仓库和入库仓库不能相同");
        }
        ErpWarehouseDO warehouse = warehouseService.getWarehouse(reqVO.getWarehouseId());
        ErpWarehouseDO inboundWarehouse = warehouseService.getWarehouse(reqVO.getInboundWarehouseId());

        // 获得出库仓库的库存
        List<ErpWarehouseInventoryDO> warehouseInventoryList = getWarehouseInventoryList(reqVO.getWarehouseId(),
                reqVO.getSkus());

        if (WarehouseTypeEnum.PLATFORM.getValue().equals(inboundWarehouse.getType())) {
            List<String> skus = warehouseInventoryList.stream().map(ErpWarehouseInventoryDO::getSellerSku).toList();
            // 筛选已在平台上架的产品
            List<CrossProductDO> crossProducts = crossProductService.getBasicCrossProductBySkus(
                    inboundWarehouse.getPlatformId(), inboundWarehouse.getShopId(), inboundWarehouse.getMarketId(),
                    skus);
            // 筛选已在平台上架的产品的SKU
            Map<String, CrossProductDO> availableSkus = crossProducts.stream()
                    .collect(Collectors.toMap(CrossProductDO::getSellerSkuCode, Function.identity()));

            warehouseInventoryList = warehouseInventoryList.stream()
                    .filter(inventory -> availableSkus.containsKey(inventory.getSellerSku())).toList();

            return BeanUtils.toBean(warehouseInventoryList, ErpTransferAvailableRespVO.class, (respVO) -> {
                CrossProductDO crossProduct = availableSkus.get(respVO.getSellerSku());
                respVO.setPlatformId(crossProduct.getPlatformId())
                        .setShopId(crossProduct.getShopId())
                        .setPlatformProductCode(crossProduct.getPlatformProductCode());
            });
        }

        return BeanUtils.toBean(warehouseInventoryList, ErpTransferAvailableRespVO.class);
    }

    private List<ErpWarehouseInventoryDO> getWarehouseInventoryList(Long warehouseId, List<String> skus) {

        return warehouseInventoryMapper.selectList(new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eq(ErpWarehouseInventoryDO::getWarehouseId, warehouseId)
                .inIfPresent(ErpWarehouseInventoryDO::getSellerSku, skus));
    }

    @Override
    public PageResult<ErpWarehouseInventoryDO> getWarehouseInventoryPage(ErpWarehouseInventoryPageReqVO reqVO) {
        PageResult<ErpWarehouseInventoryDO> pageResult = warehouseInventoryMapper.selectPage(reqVO, new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eqIfPresent(ErpWarehouseInventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(ErpWarehouseInventoryDO::getItemType, reqVO.getItemType())
                .likeIfPresent(ErpWarehouseInventoryDO::getSellerSku, reqVO.getKeyword()));
        
        if (CollUtil.isEmpty(pageResult.getList())) {
            return pageResult;
        }

        // 1. 收集 ID
        Set<Long> skuIds = pageResult.getList().stream().filter(i -> ErpItemTypeEnum.SKU.equals(i.getItemType())).map(ErpWarehouseInventoryDO::getItemId).collect(Collectors.toSet());
        Set<Long> materialIds = pageResult.getList().stream().filter(i -> ErpItemTypeEnum.MATERIAL.equals(i.getItemType())).map(ErpWarehouseInventoryDO::getItemId).collect(Collectors.toSet());

        // 2. 批量查询
        Map<Long, ProductSkuDO> skuMap = CollUtil.isEmpty(skuIds) ? Map.of() : 
            productSkuMapper.selectBatchIds(skuIds).stream().collect(Collectors.toMap(ProductSkuDO::getId, Function.identity()));
        Map<Long, ProductSpuDO> spuMap = CollUtil.isEmpty(skuMap) ? Map.of() :
            productSpuMapper.selectBatchIds(skuMap.values().stream().map(ProductSkuDO::getSpuId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ProductSpuDO::getId, Function.identity()));
        Map<Long, ErpMaterialDO> materialMap = CollUtil.isEmpty(materialIds) ? Map.of() :
            erpMaterialMapper.selectBatchIds(materialIds).stream().collect(Collectors.toMap(ErpMaterialDO::getId, Function.identity()));

        // 3. 填充数据
        pageResult.getList().forEach(item -> {
            if (ErpItemTypeEnum.SKU.equals(item.getItemType())) {
                ProductSkuDO sku = skuMap.get(item.getItemId());
                if (sku != null) {
                    ProductSpuDO spu = spuMap.get(sku.getSpuId());
                    item.setName(spu != null ? spu.getName() : "");
                    item.setImage(sku.getPicUrl());
                }
            } else if (ErpItemTypeEnum.MATERIAL.equals(item.getItemType())) {
                ErpMaterialDO material = materialMap.get(item.getItemId());
                if (material != null) {
                    item.setName(material.getName());
                    // 耗材目前没有图片，可设为空或默认图
                    item.setImage(""); 
                }
            }
        });

        return pageResult;
    }
}
