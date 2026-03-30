package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDate;

/**
 * 广告每日绩效报表 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_report_daily", autoResultMap = true)
@Data
@EqualsAndHashCode()
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportDailyDO {

    /** 报表日期 */
    @TableField("date")
    private LocalDate reportDate;

    /** 店铺ID */
    private Long shopId;

    /** 关联广告账户ID  */
    private Long accountId;

    /** 聚合维度 */
    private String groupColumn;

    /** 广告活动ID */
    private Long campaignId;

    /** 广告组ID */
    private Long adGroupId;

    /** 投放关键词/对象 */
    private String targeting;

    /** 搜索词 */
    private String searchTerm;

    /** 指标明细 (JSON) */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private java.util.Map<String, Object> payload;


}
