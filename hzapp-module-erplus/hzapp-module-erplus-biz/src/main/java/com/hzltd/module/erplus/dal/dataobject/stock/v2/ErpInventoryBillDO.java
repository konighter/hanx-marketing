package com.hzltd.module.erplus.dal.dataobject.stock.v2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * ERP 库存账单主表 (Header) DO
 *
 * @author 翰展科技
 */
@TableName("erplus_inventory_bill")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpInventoryBillDO extends BaseDO {

    /**
     * 账单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 单据大类 (10:入, 20:出, 30:调, 40:平)
     */
    private Integer type;

    /**
     * 来源类型 (WH, VENDOR, CUST, VIRTUAL)
     */
    private String fromType;
    /**
     * 来源ID (如 仓库ID / 合作伙伴ID)
     */
    private String fromId;

    /**
     * 去向类型 (WH, VENDOR, CUST, VIRTUAL)
     */
    private String toType;
    /**
     * 去向ID (如 仓库ID / 合作伙伴ID)
     */
    private String toId;

    /**
     * 业务前置单据类型
     */
    private String refType;
    /**
     * 关联单据号 (PO / SO / SHIP)
     */
    private String refCode;

    /**
     * 操作人ID
     */
    private Long operatorId;
    /**
     * 状态 (10:完成, 90:作废)
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
