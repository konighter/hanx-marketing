package com.hzltd.module.erplus.adv.dal.dataobject.automation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告自动化日志 DO
 */
@TableName(value = "ads_automation_log", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAutomationLogDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 计划 ID
     */
    private Long planId;

    /**
     * 触发规则名称
     */
    private String ruleName;

    /**
     * 触发指标 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object triggerData;

    /**
     * 执行动作 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object actionTaken;

    /**
     * 记录时间
     */
    private LocalDateTime createTime;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
