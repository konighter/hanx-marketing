package com.hzltd.module.erplus.dal.dataobject.shipmentitem;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 货件详情 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_shipment_item")
@KeySequence("erplus_shipment_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItemDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 货件ID
     */
    private Long shipmentId;
    /**
     * SKU
     */
    private String sellerSku;
    /**
     * 发货数量
     */
    private Integer quantity;
    /**
     * 长度
     */
    private Long length;
    /**
     * 宽度
     */
    private Long width;
    /**
     * 高度
     */
    private Long heigth;
    /**
     * 长度单位
     */
    private Integer unit;
    /**
     * 重量
     */
    private Long weight;
    /**
     * 重量单位
     */
    private Integer weightUnit;


}