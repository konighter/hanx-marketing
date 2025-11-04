package com.hzltd.module.erplus.dal.dataobject.productpotential;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 选品提案 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_potential")
@KeySequence("erplus_product_potential_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPotentialDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 标题
     */
    private String title;
    /**
     * 售卖平台
     */
    private Integer platformId;
    /**
     * 报告详情
     */
    private String detail;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注: 多个状态变更后的聚合
     */
    private String marks;

}