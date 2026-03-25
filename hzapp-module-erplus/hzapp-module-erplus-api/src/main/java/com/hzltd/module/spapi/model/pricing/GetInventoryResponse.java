package com.hzltd.module.spapi.model.pricing;

import com.hzltd.module.spapi.model.common.InventoryModel;
import lombok.Data;

import java.util.List;

@Data
public class GetInventoryResponse {

    private List<InventoryModel> inventorySummaries;

}
