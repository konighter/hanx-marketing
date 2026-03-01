package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 广告小时绩效报表 DO
 */
@TableName(value = "ads_report_hourly", autoResultMap = true)
@KeySequence("ads_report_hourly_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportHourlyDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联广告账户ID */
    private Long accountId;

    /** 实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD */
    private String entityType;

    /** 内部实体ID */
    private Long entityId;

    /** 报表小时点 */
    private LocalDateTime reportHour;

    /** 
     * 指标池 (JSON)
     * 包含 spend, clicks, impressions, conversions, conversion_value 等
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metrics;

    /** 
     * 花费 (从 JSON 虚拟生成的列，仅用于读取和排序)
     */
    @TableField(exist = true, insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private BigDecimal spend;

    /** 
     * 点击量 (从 JSON 虚拟生成的列，仅用于读取和排序)
     */
    @TableField(exist = true, insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private Long clicks;
}
