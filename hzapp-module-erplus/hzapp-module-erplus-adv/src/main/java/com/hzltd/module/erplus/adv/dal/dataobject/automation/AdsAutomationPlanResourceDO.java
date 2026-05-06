package com.hzltd.module.erplus.adv.dal.dataobject.automation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告自动化计划资源关联 DO
 */
@TableName("ads_automation_plan_resource")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAutomationPlanResourceDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 运营计划 ID
     */
    private Long planId;

    /**
     * 广告活动 ID (本地 ID)
     */
    private Long campaignId;

    /**
     * 平台广告活动 ID (Amazon ID)
     */
    private String platformCampaignId;

    /**
     * 资源角色: SOURCE, SINK_SHARED, SINK_ISOLATED
     */
    private String resourceRole;

    /**
     * 店铺 ID
     */
    private Long shopId;

}
