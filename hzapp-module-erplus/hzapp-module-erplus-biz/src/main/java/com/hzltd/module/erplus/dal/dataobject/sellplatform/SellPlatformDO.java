package com.hzltd.module.erplus.dal.dataobject.sellplatform;

import lombok.*;
import java.util.*;

import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 销售平台 DO
 *
 * @author hzadd
 */
@TableName(value = "erp_sell_platform", autoResultMap = true)
@KeySequence("erplus_sell_platform_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 头像
     */
    private String avatar;
    /**
     * 配送模式
     */
    @TableField(typeHandler = ServiceMode.ServiceModeHandler.class)
    private List<ServiceMode> serviceModes;




}