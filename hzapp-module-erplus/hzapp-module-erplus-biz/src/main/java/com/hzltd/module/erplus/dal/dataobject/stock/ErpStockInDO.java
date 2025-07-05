package com.hzltd.module.erplus.dal.dataobject.stock;

import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.purchase.ErpSupplierDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 其它入库单 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_stock_in")
@KeySequence("erplus_stock_in_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpStockInDO extends BaseDO {

    /**
     * 入库编号
     */
    @TableId
    private Long id;
    /**
     * 入库单号
     */
    private String no;
    /**
     * 供应商编号
     *
     * 关联 {@link ErpSupplierDO#getId()}
     */
    private Long supplierId;
    /**
     * 入库时间
     */
    private LocalDateTime inTime;
    /**
     * 合计数量
     */
    private BigDecimal totalCount;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 状态
     *
     * 枚举 {@link com.hzltd.module.erplus.enums.ErpAuditStatus}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件 URL
     */
    private String fileUrl;

}