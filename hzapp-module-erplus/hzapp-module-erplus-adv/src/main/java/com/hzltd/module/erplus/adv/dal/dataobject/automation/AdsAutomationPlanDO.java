package com.hzltd.module.erplus.adv.dal.dataobject.automation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.adv.automation.domain.AdsAutomationPlanContext;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告自动化计划 DO
 */
@TableName(value = "ads_automation_plan", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAutomationPlanDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 计划名称
     */
    private String name;

    /**
     * 关联模版 ID
     */
    private Long templateId;

    /**
     * 店铺 ID
     */
    private Long shopId;

    /**
     * 平台 (AMAZON/META)
     */
    private String platform;

    /**
     * 运行时参数 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private AdsAutomationPlanContext context;

    /**
     * 计划状态 (RUNNING, PAUSED, COMPLETED)
     */
    private String status;

    /**
     * 商品 SKU/ASIN
     */
    private String sku;

    /**
     * 最近执行时间
     */
    private LocalDateTime lastRunAt;

    /**
     * 租户ID
     */
    private Long tenantId;

}
