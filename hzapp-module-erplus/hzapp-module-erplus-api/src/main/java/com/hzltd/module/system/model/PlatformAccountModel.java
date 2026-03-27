package com.hzltd.module.system.model;

import lombok.*;

import java.io.Serializable;

/**
 * 平台账号模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAccountModel implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 账号名称
     */
    private String name;
    /**
     * 平台类型: AMAZON, TIKTOK
     */
    private String platform;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 注册号/统一社会信用代码
     */
    private String registrationNumber;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人邮箱
     */
    private String contactEmail;
    /**
     * 地址
     */
    private String address;
    /**
     * 备注
     */
    private String remark;

}
