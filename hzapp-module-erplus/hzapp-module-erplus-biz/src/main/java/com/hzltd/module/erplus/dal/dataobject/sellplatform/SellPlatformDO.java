package com.hzltd.module.erplus.dal.dataobject.sellplatform;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import lombok.*;

import java.util.List;

/**
 * 销售平台 DO
 *
 * @author hzadd
 */
@TableName(value = "erplus_sell_platform", autoResultMap = true)
@KeySequence("erplus_sell_platform_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class SellPlatformDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 平台名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 配送模式
     */
    @TableField(typeHandler = ServiceMode.ServiceModeHandler.class)
    private List<ServiceMode> serviceModes;

    /**
     * 配置信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private PlatformConfig config;


}