package com.hzltd.module.erplus.adv.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 广告账户 VO (用于平台发现)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAccountVO {

    /**
     * 平台原始账户ID
     */
    private String externalAccountId;

    /**
     * 账户显示名称
     */
    private String name;

    /**
     * 账户币种
     */
    private String currency;

    /**
     * 账户时区
     */
    private String timezone;

    /**
     * 平台特殊属性
     */
    private Object platformSpec;

}
