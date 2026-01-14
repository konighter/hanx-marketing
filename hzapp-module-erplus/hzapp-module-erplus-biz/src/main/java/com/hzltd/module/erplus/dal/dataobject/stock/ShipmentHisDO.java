package com.hzltd.module.erplus.dal.dataobject.stock;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 货件状态历史 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_shipment_his")
@KeySequence("erplus_shipment_his_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentHisDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 货件ID
     */
    private Integer shipmentId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态时间
     */
    private LocalDateTime statusTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 操作人
     */
    private String operator;


}