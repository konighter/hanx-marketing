package com.hzltd.module.erplus.dal.dataobject.product;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * ERP 产品单位 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_unit")
@KeySequence("erplus_product_unit_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductUnitDO extends BaseDO {

    /**
     * 单位编号
     */
    @TableId
    private Long id;
    /**
     * 单位名字
     */
    private String name;
    /**
     * 单位状态
     */
    private Integer status;

}