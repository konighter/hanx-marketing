package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentItemVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAddressDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentPlanDO;
import lombok.Data;

import java.util.List;

@Data
public class StockShipmentPlanRespVO extends ShipmentPlanDO {

    private String warehouseName;

    private String shopName;

    private ErpAddressDO addressDetail;

    private List<ShipmentItemVO> items;

    public Integer getSkuCount() {
        return items.size();
    }

    public Integer getTotalCount() {
        return items.stream().map(ShipmentItemVO::getQuantity).reduce(Integer::sum).orElse(0);
    }

    public Integer getFromAddress() {
        ErpAddressDO addressDO = JsonUtils.parseObject(this.getAddress(), ErpAddressDO.class);
        if (addressDO == null) {
            return null;
        }
        return addressDO.getId();
    }


}
