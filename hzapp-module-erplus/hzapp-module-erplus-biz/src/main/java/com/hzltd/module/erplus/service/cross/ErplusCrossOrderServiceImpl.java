package com.hzltd.module.erplus.service.cross;

import com.google.common.collect.Lists;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.FinancesApiFactory;
import com.hzltd.module.erplus.api.service.OrderApiFactory;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderItemResp;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderResp;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderSyncRequest;
import com.hzltd.module.erplus.convert.cross.CrossOrderConvert;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderItemDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.mysql.cross.ErpCrossOrderItemMapper;
import com.hzltd.module.erplus.dal.mysql.cross.ErpCrossOrderMapper;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.shop.ShopService;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.common.FeeModel;
import com.hzltd.module.erplus.spapi.model.order.GetOrdersRequest;
import com.hzltd.module.erplus.spapi.model.order.OrderFeeRequest;
import com.hzltd.module.erplus.spapi.model.order.OrderItemModel;
import com.hzltd.module.erplus.spapi.model.order.OrderModel;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ErplusCrossOrderServiceImpl implements ErplusCrossOrderService {

    @Resource
    private ErpCrossOrderMapper crossOrderMapper;

    @Resource
    private ErpCrossOrderItemMapper crossOrderItemMapper;

    @Resource
    private ErplusCrossProductService crossProductService;

    @Resource
    private OrderApiFactory orderApiFactory;

    @Resource
    private FinancesApiFactory financesApiFactory;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private ShopService shopService;

    @Resource
    private ErplusFinancesService financesService;


    @Async
    @Override
    public void syncCrossOrders(CrossOrderSyncRequest request) {
        Integer platformId = request.getPlatformId();
        if (platformId == null && request.getShopId() != null) {
            ShopDO shopDO = shopService.getShop(request.getShopId());
            platformId = shopDO.getPlatform();
        }

        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(request.getPlatformId());
        CrossPlatformEnum crossPlatform = CrossPlatformEnum.of(sellPlatform.getCode());

        boolean hasNext = false;
        List<String> orderIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(request.getOrderIds())) {
            List<CrossOrderDO> orders = crossOrderMapper.selectByIds(request.getOrderIds());
            if (CollectionUtils.isNotEmpty(orders)) {
                orderIds.addAll(orders.stream().map(CrossOrderDO::getPlatformOrderId).toList());
            }
//            orderIds = CollectionUtils.isNotEmpty(orders) ? orders.stream().map(CrossOrderDO::getPlatformOrderId).toList() : Collections.emptyList();
        }
        if (StringUtils.isNotEmpty(request.getPlatformOrderId())) {
            orderIds.add(request.getPlatformOrderId());
        }

        if (CollectionUtils.isNotEmpty(request.getPlatformOrderIds())) {
            orderIds.addAll(request.getPlatformOrderIds());
        }

        ApiRequest<GetOrdersRequest> apiRequest = new ApiRequest<GetOrdersRequest>()
                .setCrossPlatform(crossPlatform)
                .setShopIdInt(request.getShopId());
        GetOrdersRequest getOrdersRequest = new GetOrdersRequest()
                .setCreateTimeStart(request.getCreateTimeStart())
                .setCreateTimeEnd(request.getCreateTimeEnd())
                .setOrderIds(orderIds);
        List<OrderModel> allOrders = Lists.newArrayList();

        do {
            ApiResponse<List<OrderModel>> orderApiResponse = orderApiFactory.getCrossApiService(crossPlatform)
                    .searchOrders(apiRequest.setRequest(getOrdersRequest));

            if (orderApiResponse.success()) {
                allOrders.addAll(orderApiResponse.getData());
            }
            hasNext = orderApiResponse.hasNext();
            if (hasNext) {
                getOrdersRequest.setNextToken(orderApiResponse.getCursor());
                getOrdersRequest.setPageNo(getOrdersRequest.getPageNo() + 1);
            }
        } while (hasNext);

        // 调用销售平台的订单接口，同步订单
        allOrders.forEach(orderModel -> saveOrUpdateCrossOrder(orderModel, request.getPlatformId(), request.getShopId()));

        log.info("syncCrossOrders, platformId={}, shopId={}, orderCount={}", crossPlatform.getName(), request.getShopId(), allOrders.size());
    }


    private void saveOrUpdateCrossOrder(OrderModel orderModel, Integer platformId, Integer shopId) {
        CrossOrderDO crossOrderDO = crossOrderMapper.selectOne(
                new LambdaQueryWrapperX<CrossOrderDO>()
                        .eqIfPresent(CrossOrderDO::getPlatformId, platformId)
                        .eqIfPresent(CrossOrderDO::getPlatformOrderId, orderModel.getOrderId())
                        .eqIfPresent(CrossOrderDO::getShopId, shopId)
                        .eqIfPresent(CrossOrderDO::getMarketId, orderModel.getMarketId())
        );

        if (crossOrderDO == null) {
            crossOrderDO = CrossOrderConvert.INSTANCE.convert(orderModel);
            crossOrderDO.setPlatformId(platformId);
            crossOrderDO.setShopId(shopId);

        } else {
            crossOrderDO = CrossOrderConvert.INSTANCE.update(orderModel, crossOrderDO);
        }

        crossOrderMapper.insertOrUpdate(crossOrderDO);
        Long crossOrderId = crossOrderDO.getId();

        // 调用销售平台的订单接口，同步订单项
        orderModel.getOrderItems().forEach(orderItemModel -> saveOrUpdateCrossOrderItem(orderItemModel, platformId, shopId, crossOrderId));

    }

    private void saveOrUpdateCrossOrderItem(OrderItemModel orderItemModel, Integer platformId, Integer shopId, Long crossOrderId) {
        Optional<CrossProductDO> crossProductDOOptional = crossProductService.getBasicCrossPlatformProductByPlatformIdAndProductId(platformId, orderItemModel.getSellerSku(), orderItemModel.getPlatformProductCode());
        CrossOrderItemDO crossOrderItemDO = crossOrderItemMapper.selectOne(orderItemModel.getOrderId(), platformId);
        Long productId = crossProductDOOptional.map(CrossProductDO::getId).orElse(null);
        if (crossOrderItemDO == null) {
            crossOrderItemDO = CrossOrderConvert.INSTANCE.convert(orderItemModel);
            crossOrderItemDO.setPlatformId(platformId);
            crossOrderItemDO.setShopId(shopId);
            crossOrderItemDO.setOrderId(crossOrderId);
            crossOrderItemDO.setProductId(productId);

        } else {
            crossOrderItemDO = CrossOrderConvert.INSTANCE.update(orderItemModel, crossOrderItemDO);
        }

        dealOrderItemFee(crossOrderItemDO);
        crossOrderItemMapper.insertOrUpdate(crossOrderItemDO);

    }


    @Override
    public PageResult<CrossOrderResp> getCrossOrderPage(CrossOrderPageRequest request) {
        PageResult<CrossOrderDO> pageResult = crossOrderMapper.selectPage(request);
        List<Long> orderIds = pageResult.getList().stream().map(CrossOrderDO::getId).collect(Collectors.toList());
        List<CrossOrderItemDO> crossOrderItemDOList = crossOrderItemMapper.selectListByOrderIds(orderIds);
        Map<Long, List<CrossOrderItemDO>> orderItemMap = crossOrderItemDOList.stream().collect(Collectors.groupingBy(CrossOrderItemDO::getOrderId));

        List<CrossProductDO> crossProducts = crossProductService.getBasicCrossPlatformProduct(crossOrderItemDOList.stream().map(CrossOrderItemDO::getProductId).collect(Collectors.toList())).get();
        Map<Long, CrossProductDO> crossProductMap = crossProducts.stream().collect(Collectors.toMap(CrossProductDO::getId, Function.identity()));



        PageResult<CrossOrderResp> respPageResult = BeanUtils.toBean(pageResult, CrossOrderResp.class);
        respPageResult.getList().forEach(resp -> {
            List<CrossOrderItemResp> orderItemRespList = BeanUtils.toBean(orderItemMap.get(resp.getId()), CrossOrderItemResp.class, item -> {
                if (crossProductMap.get(item.getProductId()) != null) {
                    item.setMainImageUrl(crossProductMap.get(item.getProductId()).getMainImageUrl());
                }

            });
            resp.setOrderItemList(orderItemRespList);
        });

        return respPageResult;
    }


    @Override
    public List<CrossOrderResp> getCrossOrders(List<Long> orderIds) {
        List<CrossOrderDO> crossOrderDOList = crossOrderMapper.selectList(new LambdaQueryWrapperX<CrossOrderDO>().inIfPresent(CrossOrderDO::getId, orderIds));
        if (CollectionUtils.isEmpty(crossOrderDOList)) {
            return Collections.emptyList();
        }
        List<CrossOrderItemDO> crossOrderItemDOList = crossOrderItemMapper.selectListByOrderIds(orderIds);
        Map<Long, List<CrossOrderItemDO>> orderItemMap = crossOrderItemDOList.stream().collect(Collectors.groupingBy(CrossOrderItemDO::getOrderId));

        // 处理订单项
        List<CrossOrderResp> crossOrderList = BeanUtils.toBean(crossOrderDOList, CrossOrderResp.class);
        crossOrderList.forEach(resp -> {
            List<CrossOrderItemResp> orderItemRespList = BeanUtils.toBean(orderItemMap.get(resp.getId()), CrossOrderItemResp.class);
            resp.setOrderItemList(orderItemRespList);
        });

        return crossOrderList;
    }


    // todo -- 根据不同的订单状态, 查询预估还是实际结算
    private FeeModel dealOrderItemFee(CrossOrderItemDO crossOrderItemDO) {
        FeeModel feeModel = financesService.getProductEstimatedFee(crossOrderItemDO.getProductId());
        if (feeModel != null) {
            crossOrderItemDO.setEstimatedTotalFee(feeModel.getTotalFee());
            crossOrderItemDO.setEstimatedReferralFee(feeModel.getReferralFee());
            crossOrderItemDO.setEstimatedFulfillFee(feeModel.getShippingFee());
        }

        return feeModel;
    }



}
