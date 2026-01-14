package com.hzltd.module.erplus.dal.dataobject.stock;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 货件 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_shipment_plan")
@KeySequence("erplus_shipment_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPlanDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 计划名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 平台
     */
    private Integer platformId;
    /**
     * 店铺
     */
    private Integer shopId;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 发货仓库
     */
    private Long warehouseId;
    /**
     * 地址
     */
    private String address;
    /**
     * 备注
     */
    private String remark;
    /**
     * extra_id
     */
    private String extralId;
    /**
     * 状态
     */
    private Integer status;

    private Integer auditStatus;

    private String auditRemark;
    /**
     * 商品总量
     */
    private Integer quantity;
    /**
     * 商品数
     */
    private Integer skuCount;
    /**
     * 总重量
     */
    private Long totalWeight;


}