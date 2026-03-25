package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderDO;
import com.hzltd.module.spapi.enums.CrossOrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class CrossOrderResp extends CrossOrderDO {

    private List<CrossOrderItemResp> orderItemList;

    public String getTotalAmount() {
        if (getAmount() == null) {
            return "";
        }
        return "%s %s".formatted( BigDecimal.valueOf(getAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).toString(), getCurrency());
    }

    public String getStatusName() {
        return CrossOrderStatus.of(this.getStatus()).getName();
    }

    public String getFulfillTypeName() {
        return FulfillTypeEnum.of(this.getFulfillType()).getName();
    }

}
