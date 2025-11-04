package com.hzltd.module.erplus.dal.dataobject.sellzone;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import lombok.*;

/**
 * 销售区域 DO
 *
 * @author hzadd
 */
@TableName("erplus_sell_zone")
@KeySequence("erplus_sell_zone_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class SellZoneDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 销售平台
     */
    private Integer platformId;
    /**
     * 区域名称
     */
    private String zoneName;
    /**
     * 区域编码
     */
    private String zoneCode;
    /**
     * 区域Locale
     */
    private String locale;
    /**
     * 语言
     */
    private String language;

}