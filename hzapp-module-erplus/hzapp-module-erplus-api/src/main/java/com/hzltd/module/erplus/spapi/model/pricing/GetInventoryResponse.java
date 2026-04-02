package com.hzltd.module.erplus.spapi.model.pricing;

import com.hzltd.module.erplus.spapi.model.common.InventoryModel;
import lombok.Data;

import java.util.List;

@Data
public class GetInventoryResponse {

    private List<InventoryModel> inventorySummaries;

}
