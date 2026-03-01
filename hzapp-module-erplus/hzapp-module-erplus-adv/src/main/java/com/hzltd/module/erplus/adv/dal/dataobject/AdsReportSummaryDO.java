package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 广告性能预计算汇总表 DO
 */
@TableName(value = "ads_report_summary", autoResultMap = true)
@KeySequence("ads_report_summary_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportSummaryDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联广告账户ID */
    private Long accountId;

    /** 实体类型: ACCOUNT / CAMPAIGN / ADGROUP */
    private String entityType;

    /** 内部实体ID */
    private Long entityId;

    /** 周期类型: DAY / WEEK / MONTH */
    private String periodType;

    /** 周期值 (YYYY-MM-DD / YYYY-WW / YYYY-MM) */
    private String periodValue;

    /** 
     * 聚合后的指标池 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metrics;

    /** 
     * 花费 (虚拟生成)
     */
    @TableField(exist = true, insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private BigDecimal spend;

    /** 
     * 转化数 (虚拟生成)
     */
    @TableField(exist = true, insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private Integer conversions;

    /** 
     * 销售额 (虚拟生成)
     */
    @TableField(exist = true, insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private BigDecimal sales;
}
