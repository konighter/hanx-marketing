package com.hzltd.module.erplus.service.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 平台授权状态 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthStateDTO implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 租户 ID
     */
    private Long tenantId;

    /**
     * 授权范围
     */
    private String authType;

    /**
     * 应用 ID
     */
    private Long appId;

    /**
     * 区域
     */
    private String region;

    /**
     * 平台类型
     */
    private String platform;

}
