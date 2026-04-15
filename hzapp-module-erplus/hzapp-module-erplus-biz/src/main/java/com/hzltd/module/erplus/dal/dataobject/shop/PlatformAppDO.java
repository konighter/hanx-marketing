package com.hzltd.module.erplus.dal.dataobject.shop;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 平台应用 DO
 *
 * @author hzadd
 */
@TableName(value = "erplus_platform_app", autoResultMap = true)
@KeySequence("erplus_platform_app_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAppDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 应用名称
     */
    private String name;
    /**
     * 平台类型: AMAZON, TIKTOK
     */
    private String platform;
    /**
     * 应用Key
     */
    private String appKey;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 回调地址
     */
    private String callbackUrl;

}
