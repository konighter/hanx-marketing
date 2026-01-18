package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.constant.WarehouseTypeEnum;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.warehouse.ErpWarehouseInventoryPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;
import com.hzltd.module.erplus.dal.mysql.stock.ErpWarehouseInventoryMapper;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.service.cross.ErplusCrossProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

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
    private ErpWarehouseInventoryMapper warehouseInventoryMapper;

    @Resource
    private ErplusCrossProductService crossProductService;

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
        return warehouseInventoryMapper.selectPage(reqVO, new LambdaQueryWrapperX<ErpWarehouseInventoryDO>()
                .eqIfPresent(ErpWarehouseInventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(ErpWarehouseInventoryDO::getSellerSku, reqVO.getKeyword()));
    }
}
