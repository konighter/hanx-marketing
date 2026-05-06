package com.hzltd.module.erplus.adv.dal.dataobject.automation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告自动化模版 DO
 */
@TableName(value = "ads_automation_template", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAutomationTemplateDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 模版名称
     */
    private String name;

    /**
     * 模版类型 (如：KEYWORD_FLOW)
     */
    private String type;

    /**
     * 适用平台 (AMAZON/META)
     */
    private String platform;

    /**
     * 配置蓝图 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object config;

    /**
     * 状态 (1: 启用, 0: 禁用)
     */
    private Integer status;

}
