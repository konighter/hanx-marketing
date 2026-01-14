package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

import java.util.List;

@Data
public class AmzTransportOptionGenerateRequest {
        /**
         * 入仓计划ID
         */
        private String planId;

        /**
         * 店铺ID
         */
        private Integer shopId;

        /**
         * 入仓配置选项ID
         */
        private String placementOptionId;

        /**
         * 发货日期
         */
        private String shipmentDate;

        /**
         * ID列表
         */
        private List<String> shipmentIds;

}
