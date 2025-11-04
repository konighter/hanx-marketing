package com.hzltd.module.erplus.dal.dataobject.app;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import lombok.*;

/**
 * 应用注册信息 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_app")
@KeySequence("erplus_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AppDO extends BaseDO {

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
     * 平台ID
     */
    private Long platformId;
    /**
     * AppId
     */
    private String appId;
    /**
     * 应用Key
     */
    private String appKey;
    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 状态
     */
    private Integer status;

}