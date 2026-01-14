package com.hzltd.module.erplus.controller.admin.amz.vo;

import com.google.common.collect.Maps;
import lombok.Data;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Item;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.PackingOption;

import java.util.List;
import java.util.Map;

@Data
public class AmzPackingOption extends PackingOption {
    /**
     * 整装 or 散装
     */
    private Boolean packingType;

    /**
     * 每个打包组的物品列表
     */
    Map<String, List<AmzBoxItem>> groupItems = Maps.newHashMap();

    public void putGroupItems(String groupId, List<AmzBoxItem> items) {
        groupItems.put(groupId, items);
    }

}
