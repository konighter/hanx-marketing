package com.hzltd.module.erplus.dal.dataobject.shop;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 平台账号 DO
 *
 * @author hzadd
 */
@TableName("erplus_platform_account")
@KeySequence("erplus_platform_account_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAccountDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
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
