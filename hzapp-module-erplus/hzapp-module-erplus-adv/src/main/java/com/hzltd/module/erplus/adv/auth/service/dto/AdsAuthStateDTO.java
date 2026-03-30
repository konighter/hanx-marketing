package com.hzltd.module.erplus.adv.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 广告授权 State 元数据 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAuthStateDTO implements Serializable {

    /**
     * 广告平台
     */
    private String platform;

    /**
     * 店铺编号
     */
    private Long shopId;

    /**
     * 当前授权用户
     */
    private Long userId;

}
