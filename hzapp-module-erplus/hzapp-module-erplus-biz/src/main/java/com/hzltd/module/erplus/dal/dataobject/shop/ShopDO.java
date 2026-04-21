package com.hzltd.module.erplus.dal.dataobject.shop;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 店铺信息 DO
 *
 * @author hzadd
 */
@TableName(value = "erplus_platform_shop", autoResultMap = true)
@KeySequence("ov_shop_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 平台
     */
    private Integer platform;
    /**
     * 区域
     */
    private String region;
    /**
     * 授权信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private AuthorizationModelV0 authInfo;
    /**
     * 授权刷新
     */
//    private String authRefreshInfo;
    /**
     * 授权开始时间
     */
    private LocalDateTime authStartTime;
    /**
     * 授权失效时间
     */
    private LocalDateTime authExpTime;
    /**
     * 站点ID: 如ATVPDKIKX0DER(US)
     */
    private String marketplaceId;
    /**
     * 国家代码: US, GB, JP, MY
     */
    private String countryCode;
    /**
     * 币种
     */
    private String currency;
    /**
     * 卖家ID (用于广告和店铺的关联)
     */
    private String sellerId;

    /**
     * 平台账号ID
     */
    private Long accountId;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 状态
     *
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Integer tenantId;

}