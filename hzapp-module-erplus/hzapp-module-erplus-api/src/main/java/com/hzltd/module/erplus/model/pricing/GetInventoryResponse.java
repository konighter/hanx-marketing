package com.hzltd.module.erplus.model.pricing;

import com.hzltd.module.erplus.model.common.InventoryModel;
import lombok.Data;

import java.util.List;

@Data
public class GetInventoryResponse {

    private List<InventoryModel> inventorySummaries;

}
