package com.hzltd.module.erplus.dal.dataobject.shop;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 店铺信息 DO
 *
 * @author hzadd
 */
@TableName(value = "ov_shop", autoResultMap = true)
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
    private Integer region;
    /**
     * 授权信息
     */
    private String authorInfo;
    /**
     * 授权刷新
     */
    private String authRefreshInfo;
    /**
     * 授权开始时间
     */
    private LocalDateTime authStartTime;
    /**
     * 授权失效时间
     */
    private LocalDateTime authExpTime;
    /**
     * 状态
     *
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Integer status;

}