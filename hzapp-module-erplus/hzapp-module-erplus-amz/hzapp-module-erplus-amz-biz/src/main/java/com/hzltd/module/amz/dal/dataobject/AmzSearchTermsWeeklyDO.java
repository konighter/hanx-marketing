package com.hzltd.module.amz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 亚马逊搜索词周报 DO
 * 映射 Doris 表 ads_amazon_search_terms_weekly
 */
@TableName("ads_amazon_search_terms_weekly")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzSearchTermsWeeklyDO {

    /**
     * 周起始日期 (作为主键之一)
     */
    private LocalDate date;

    /**
     * 站点ID
     */
    private String marketplaceId;

    /**
     * 搜索关键词
     */
    private String searchTerm;

    /**
     * 搜索频率排名
     */
    private Long searchFrequencyRank;

    // Top 1
    private String top1ClickedAsin;
    private String top1ProductTitle;
    private Double top1ClickShare;
    private Double top1ConversionShare;

    // Top 2
    private String top2ClickedAsin;
    private String top2ProductTitle;
    private Double top2ClickShare;
    private Double top2ConversionShare;

    // Top 3
    private String top3ClickedAsin;
    private String top3ProductTitle;
    private Double top3ClickShare;
    private Double top3ConversionShare;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
