package com.hzltd.module.erplus.dal.dataobject.productMonitor;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 产品监控指标 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_metrics")
@KeySequence("erplus_product_metrics_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetricsDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 监控任务ID
     */
    private Long monitorId;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 日期(yyyyMMdd)
     */
    private Integer datekey;
    /**
     * 指标
     */
    private String metircs;

}