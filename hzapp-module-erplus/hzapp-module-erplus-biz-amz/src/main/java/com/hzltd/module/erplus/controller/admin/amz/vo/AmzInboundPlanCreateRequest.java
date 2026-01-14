package com.hzltd.module.erplus.controller.admin.amz.vo;

import com.hzltd.module.erplus.model.common.AddressModel;
import com.hzltd.module.erplus.model.inventory.InventoryItemModel;
import lombok.Data;

import java.util.List;

@Data
public class AmzInboundPlanCreateRequest {
    private String name;

    private Integer platformId;

    private Integer shopId;

    private String marketId;

    private AddressModel address;

    private List<InventoryItemModel> items;

}
