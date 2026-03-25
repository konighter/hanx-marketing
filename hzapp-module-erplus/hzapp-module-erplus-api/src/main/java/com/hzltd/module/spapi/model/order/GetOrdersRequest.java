package com.hzltd.module.spapi.model.order;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.spapi.enums.CrossOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetOrdersRequest extends PageParam {

    /**
     * 分页token
     */
    private String nextToken;

    /**
     * 订单ID列表
     */
    private List<String> orderIds;

    /**
     * 交付类型
     */
    private FulfillTypeEnum fulfillType;

    /**
     * 订单状态列表
     */
    private List<CrossOrderStatus> statuses;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

     /**
      * 创建时间结束
      */
     private LocalDateTime createTimeEnd;

}
