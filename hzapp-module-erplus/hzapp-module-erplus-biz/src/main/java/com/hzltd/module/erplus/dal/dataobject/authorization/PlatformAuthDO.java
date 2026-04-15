package com.hzltd.module.erplus.dal.dataobject.authorization;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 平台授权 DO
 *
 * @author hzadd
 */
@TableName("erplus_platform_auth")
@KeySequence("erplus_platform_auth_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAuthDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 平台类型: AMAZON, TIKTOK
     */
    private String platform;
    /**
     * 员工ID
     */
    private Long userId;
    /**
     * 授权类型: AMAZON_SP, AMAZON_ADV, TTS_SHOP
     */
    private String authType;
    /**
     * 授权范围
     */
    private String authScope;
    /**
     * 区域代码: NA(北美), EU(欧洲), FE(远东), GLOBAL(TTS全球)
     */
    private String region;
    /**
     * 卖家后台的唯一身份标识
     */
    private String sellerId;
    /**
     * 用于刷新access_token的长效令牌
     */
    private String refreshToken;
    /**
     * 当前有效的访问令牌
     */
    private String accessToken;
    /**
     * access_token的过期时间
     */
    private LocalDateTime expiryTime;
    /**
     * 平台应用ID
     */
    private Long appId;

    /**
     * 是否是默认授权
     */
    private Boolean isDefault;

}
