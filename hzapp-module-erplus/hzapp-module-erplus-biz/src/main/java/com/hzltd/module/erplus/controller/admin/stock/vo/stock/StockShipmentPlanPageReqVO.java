package com.hzltd.module.erplus.controller.admin.stock.vo.stock;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class StockShipmentPlanPageReqVO extends PageParam {

    private Integer shopId;

    private String[] createDateRange;

    private Integer status;

}
