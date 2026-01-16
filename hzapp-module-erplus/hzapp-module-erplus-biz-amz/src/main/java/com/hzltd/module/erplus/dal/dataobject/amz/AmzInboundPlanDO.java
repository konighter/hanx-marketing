package com.hzltd.module.erplus.dal.dataobject.amz;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 平台货件信息 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_inbound_plan")
@KeySequence("erplus_amz_inbound_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzInboundPlanDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;

    private String name;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 市场
     */
    private String marketId;
    /**
     * 计划ID
     */
    private String planId;
    /**
     * 发货地址
     */
    private String address;
    /**
     * 发货SKU
     */
    private String items;

    /**
     * 事务ID, 同事务內的操作具备前后逻辑关联
     *
     */
    private String transactionId;
    /**
     * 打包信息
     */
    private String packingInfo;

    private String packingOptionId;

    private String packingOptions;
    /**
     * 配置项
     */
    private String placementId;
    /**
     * 配置项详情
     */
    private String placementDetail;

    /**
     * 配置项
     */
    private String placementOptions;


    /**
     * 运输项
     */
    private String transportOptionId;
    /**
     * 运输项详情
     */
    private String transportDetail;

    /**
     * 运输项
     */
    private String transportOptions;




    /**
     * 运输项
     */
    private String shipmentId;
    /**
     * 运输项详情
     */
    private String shipmentDetail;
    /**
     * 标签信息
     */
    private String lableInfo;
    /**
     * 状态
     */
    private Integer status;


}