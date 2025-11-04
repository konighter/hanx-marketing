package com.hzltd.module.erplus.dal.dataobject.productMonitor;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 产品监控 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_monitor")
@KeySequence("erplus_product_monitor_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMonitorDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 平台
     */
    private Integer platformId;
    /**
     * 产品链接
     */
    private String productLink;

    /**
     * 商品主图
     */
    private String imageLink;
    /**
     * 品类ID
     */
    private Integer categoryId;
    /**
     * 周期(默认Day)
     */
    private String crone;

    private String croneType;

    /**
     * 下一次采集时间
     */
    private Long nextCrawlTime;

    /**
     * 上一次采集时间
     */
    private Long lastCrawlTime;
    /**
     * 状态
     *
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Integer status;

}