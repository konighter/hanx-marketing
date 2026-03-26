package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告计划属性 DO
 *
 * @author 翰展科技
 */
@TableName("ads_campaign_attribute")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignAttributeDO extends BaseDO {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 广告活动ID
     */
    private Long campaignId;

    /**
     * 属性类型 (SYSTEM/PLATFORM)
     */
    private String attrType;

    /**
     * 属性键
     */
    private String attrKey;

    /**
     * 属性值 (JSON 格式)
     */
    private String attrValue;

    /**
     * 属性值序列化类名
     */
    private String attrValueClass;

}
