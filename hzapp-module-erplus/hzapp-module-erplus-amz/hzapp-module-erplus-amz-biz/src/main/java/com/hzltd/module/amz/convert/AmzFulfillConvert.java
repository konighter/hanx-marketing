package com.hzltd.module.amz.convert;

import com.hzltd.module.amz.controller.admin.vo.AmzInboundPlanCreateRequest;
import com.hzltd.module.amz.dal.dataobject.AmzInboundPlanDO;
import com.hzltd.module.spapi.model.common.AddressModel;
import com.hzltd.module.spapi.model.inventory.InventoryItemModel;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 亚马逊入仓计划 Convert
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzFulfillConvert {
    AmzFulfillConvert INSTANCE = Mappers.getMapper(AmzFulfillConvert.class);

    /**
     * 转换入仓计划创建请求为入仓计划请求
     *
     * @param createRequest 入仓计划创建请求
     * @return 入仓计划请求
     */
    default CreateInboundPlanRequest convert(AmzInboundPlanCreateRequest createRequest) {
        CreateInboundPlanRequest request = new CreateInboundPlanRequest();
        request.setName(createRequest.getName());
        request.setDestinationMarketplaces(List.of(createRequest.getMarketId()));
        request.setSourceAddress(convert(createRequest.getAddress()));

        request.setItems(createRequest.getItems().stream().map(this::convert).collect(Collectors.toList()));
        return request;
    }

    /**
     * 转换地址模型为地址输入
     *
     * @param address 地址模型
     * @return 地址输入
     */
    AddressInput convert(AddressModel address);

    /**
     * 转换库存项模型为项输入
     *
     * @param item 库存项模型
     * @return 项输入
     */
    default ItemInput convert(InventoryItemModel item) {
        ItemInput input = new ItemInput();
        input.setMsku(item.getSellerSku());
        input.setQuantity(item.getQuantity());
        input.setLabelOwner(LabelOwner.SELLER);
        input.setPrepOwner(PrepOwner.SELLER);
        return input;
    }

    ;

    default AmzInboundPlanDO convert(InboundPlan plan) {
        return new AmzInboundPlanDO()
                .setName(plan.getName())
                .setPlanId(plan.getInboundPlanId())
                .setMarketId(plan.getMarketplaceIds().get(0))
                .setAddress(plan.getSourceAddress().toString())
                ;

    }


}
