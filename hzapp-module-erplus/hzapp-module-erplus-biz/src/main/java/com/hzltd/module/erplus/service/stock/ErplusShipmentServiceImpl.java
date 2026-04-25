package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.amz.spapi.AmzCancelInboundPlanRequest;
import com.hzltd.module.amz.spapi.service.AmzFulfillmentService;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentAuditReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentItemVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.StockShipmentPlanReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanRespVO;
import com.hzltd.module.erplus.convert.shipment.ShipmentConvert;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentHisDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import com.hzltd.module.erplus.dal.mysql.stock.ShipmentHisMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ShipmentItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ShipmentPlanMapper;
import com.hzltd.module.erplus.enums.AuditAction;
import com.hzltd.module.erplus.enums.ShipmentStatusEnum;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.shop.ShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.*;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.*;

/**
 * ERP 货件计划服务实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
public class ErplusShipmentServiceImpl implements ErplusShipmentService {

    @Resource
    private ShipmentPlanMapper shipmentPlanMapper;

    @Resource
    private ShipmentItemMapper shipmentItemMapper;

    @Resource
    private ShipmentHisMapper shipmentHisMapper;

    @Resource
    private ErpAddressService addressService;

    @Resource
    private ShopService shopService;

    @Resource
    private SellPlatformService platformService;

    @Resource
    private ErpWarehouseService warehouseService;

    @Resource
    private AmzFulfillmentService amzFulfillmentService;


    @Override
    public PageResult<StockShipmentPlanRespVO> getShipmentPlanPage(StockShipmentPlanPageReqVO reqVO) {

        // 1. 查询货件计划分页
        PageResult<ShipmentPlanDO> pageResult = shipmentPlanMapper.selectPage(reqVO);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }

        Map<Integer, ShopDO> shopMap = shopService.getShopsByIds(pageResult.getList().stream().map(ShipmentPlanDO::getShopId).collect(Collectors.toList())).stream().collect(Collectors.toMap(ShopDO::getId, a -> a));
        Map<Long, ErpWarehouseDO> warehouseDOMap = warehouseService.getWarehouseMap(pageResult.getList().stream().map(ShipmentPlanDO::getWarehouseId).toList());


        PageResult<StockShipmentPlanRespVO> respPage = BeanUtils.toBean(pageResult, StockShipmentPlanRespVO.class);
        // 2. 查询货件计划商品
        respPage.getList().forEach(shipmentPlan -> {
            shipmentPlan.setItems(BeanUtils.toBean(shipmentItemMapper.selectListByPlanId(shipmentPlan.getId()), ShipmentItemVO.class));
            // 3. 查询仓库名称
            if (warehouseDOMap.containsKey(shipmentPlan.getWarehouseId())) {
                shipmentPlan.setWarehouseName(warehouseDOMap.get(shipmentPlan.getWarehouseId()).getName());
            }

            // 4. 查询店铺名称
            if (shopMap.containsKey(shipmentPlan.getShopId())) {
                shipmentPlan.setShopName(shopMap.get(shipmentPlan.getShopId()).getName());
            }

            shipmentPlan.setPlatformId(shipmentPlan.getPlatformId());
        });

        return respPage;
    }

    @Override
    public StockShipmentPlanRespVO getShipmentPlan(Integer id) {
        ShipmentPlanDO shipmentPlan = shipmentPlanMapper.selectById(id);
        if (shipmentPlan == null) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }

        StockShipmentPlanRespVO respVO = BeanUtils.toBean(shipmentPlan, StockShipmentPlanRespVO.class, (item) -> {
            ShopDO shop = shopService.getShop(shipmentPlan.getShopId());
            item.setShopName(shop.getName());

            ErpWarehouseDO warehouse = warehouseService.getWarehouse(shipmentPlan.getWarehouseId());
            item.setWarehouseName(warehouse.getName());

        });
        // 2. 查询货件计划商品
        respVO.setItems(BeanUtils.toBean(shipmentItemMapper.selectListByPlanId(shipmentPlan.getId()), ShipmentItemVO.class));
        respVO.setAddressDetail(addressService.getAddressById(respVO.getFromAddress()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveShipmentPlan(StockShipmentPlanReqVO reqVO) {
        boolean save = true;
        // 1. 校验货件计划是否存在
        ShipmentPlanDO shipmentPlan = shipmentPlanMapper.selectById(reqVO.getId());
        if (shipmentPlan == null) {
            save = false;
            shipmentPlan = new ShipmentPlanDO();
        }
        // 2. 转换为 DO 并保存
        ShipmentConvert.INSTANCE.convert(shipmentPlan, reqVO);
        shipmentPlan.setStatus(ShipmentStatusEnum.INIT.getStatus());
        shipmentPlan.setAddress(JsonUtils.toJsonString(addressService.getAddressById(reqVO.getFromAddress())));
        shipmentPlan.setPlatformId(shopService.getShop(reqVO.getShopId()).getPlatform());
        // 校验商品是否存在

        // 计算商品总量、商品数、总重量

        // 3. 保存货件计划
        shipmentPlanMapper.insertOrUpdate(shipmentPlan);
        Integer shipmentPlanId = shipmentPlan.getId();
        // 4. 保存货件计划商品
        shipmentItemMapper.deleteBatchByPlanId(shipmentPlan.getId());
        shipmentItemMapper.insertBatch(reqVO.getItems().stream().map(item -> {
            ShipmentItemDO shipmentItem = ShipmentConvert.INSTANCE.convert(item);
            shipmentItem.setShipmentId(shipmentPlanId);
            shipmentItem.setId(null);
            return shipmentItem;
        }).collect(Collectors.toList()));


        // 5. 保存货件计划状态历史
        if (save) {
            saveShipmentHis(shipmentPlanId, ShipmentStatusEnum.INIT, "创建货件计划");
        }

        return shipmentPlan.getId();
    }


    @Override
    public Integer submitShipmentPlan(StockShipmentPlanReqVO reqVO) {

        Integer shipmentPlanId = this.saveShipmentPlan(reqVO);

        // 1. 校验货件计划是否存在
        ShipmentPlanDO shipmentPlan = shipmentPlanMapper.selectById(shipmentPlanId);
        if (shipmentPlan == null) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        shipmentPlan.setStatus(ShipmentStatusEnum.AUDITING.getStatus());
        // 2. 保存货件计划
        shipmentPlanMapper.updateById(shipmentPlan);

        // 3. 保存货件计划状态历史
        saveShipmentHis(shipmentPlanId, ShipmentStatusEnum.AUDITING, "提交货件计划");

        return shipmentPlanId;
    }


    @Override
    @Transactional
    public void auditShipment(ShipmentAuditReqVO reqVO) {
        // 1. 校验货件计划是否存在
        ShipmentPlanDO shipmentPlan = shipmentPlanMapper.selectById(reqVO.getShipmentId());
        if (shipmentPlan == null) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        // 2. 校验货件计划状态是否正确
        if (!shipmentPlan.getStatus().equals(ShipmentStatusEnum.AUDITING.getStatus())) {
            throw exception(SHIPMENT_STATUS_INVALID);
        }
        ShipmentStatusEnum newStatus = null;
        // 3. 更新货件计划状态
        shipmentPlan.setAuditStatus(reqVO.getAction());
        shipmentPlan.setAuditRemark(reqVO.getRemark());
        if (AuditAction.APPROVE.getValue().equals(reqVO.getAction())) {
            newStatus = ShipmentStatusEnum.PENDING_BOXING;
            shipmentPlan.setStatus(newStatus.getStatus());
        } else {
            newStatus = ShipmentStatusEnum.INIT;
            shipmentPlan.setStatus(newStatus.getStatus());
        }
        // 4. 更新货件计划
        shipmentPlanMapper.updateById(shipmentPlan);
        // 5. 保存货件计划状态历史
        saveShipmentHis(reqVO.getShipmentId(), newStatus, reqVO.getRemark());

        // 6.创建对一个平台的货件计划
        if (reqVO.getAction().equals(AuditAction.APPROVE.getValue())) {
            createPlatformShipment(shipmentPlan);
        }

    }

    private void createPlatformShipment(ShipmentPlanDO shipmentPlan) {
        StockShipmentPlanRespVO respVO = getShipmentPlan(shipmentPlan.getId());

        SellPlatformDO platform = platformService.getSellPlatform(shipmentPlan.getPlatformId());
        String planId = null;
        // TODO- 消息路由
        if ("Amazon".equalsIgnoreCase(platform.getCode())) {
            // 创建 Amazon 发货计划
            planId = amzFulfillmentService.createInboundPlan(ShipmentConvert.INSTANCE.convert(respVO));
        }

        if (StringUtils.isNotEmpty(planId)) {
            shipmentPlan.setExtralId(planId);
            shipmentPlanMapper.updateById(shipmentPlan);
        }

    }

    @Override
    public void updateShipmentStatus(Integer shipmentId, Integer status) {
        // 1. 校验货件计划是否存在
        ShipmentPlanDO shipmentPlan = shipmentPlanMapper.selectById(shipmentId);
        if (shipmentPlan == null) {
            throw exception(SHIPMENT_NOT_EXISTS);
        }
        if (ShipmentStatusEnum.CANCELED.getStatus().equals(status)) {
            // 取消对应平台的发货计划
            cancelPlatformShipment(shipmentPlan);
        }
        // 2. 更新货件计划状态
        shipmentPlan.setStatus(status);
        shipmentPlanMapper.updateById(shipmentPlan);
        // 3. 保存货件计划状态历史
        saveShipmentHis(shipmentId, ShipmentStatusEnum.of(status), "更新货件计划状态");


    }

    private void cancelPlatformShipment(ShipmentPlanDO shipmentPlan) {
        if (shipmentPlan == null || StringUtils.isEmpty(shipmentPlan.getExtralId())) {
            return;
        }

        SellPlatformDO platform = platformService.getSellPlatform(shipmentPlan.getPlatformId());
        if ("Amazon".equalsIgnoreCase(platform.getCode())) {
            // 取消 Amazon 发货计划
            amzFulfillmentService.cancelInboundPlan(new AmzCancelInboundPlanRequest().setInboundPlanId(shipmentPlan.getExtralId()));
        }
    }


    private Integer saveShipmentHis(Integer shipmentId, ShipmentStatusEnum status, String remark) {
        ShipmentHisDO shipmentHisDO = new ShipmentHisDO();
        shipmentHisDO.setShipmentId(shipmentId);
        shipmentHisDO.setStatus(status.getStatus());
        shipmentHisDO.setStatusTime(LocalDateTime.now());
        shipmentHisDO.setRemark(remark);
        shipmentHisMapper.insertOrUpdate(shipmentHisDO);
        return shipmentHisDO.getId();
    }


}
