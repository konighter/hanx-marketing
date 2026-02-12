package com.hzltd.module.erplus.amzadv.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 亚马逊广告关键词 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_adv_keyword")
@KeySequence("erplus_amz_adv_keyword_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzAdvKeywordDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 关键词ID (Amazon侧)
     */
    private String keywordId;

    /**
     * 广告活动ID
     */
    private String campaignId;

    /**
     * 广告组ID
     */
    private String adGroupId;

    /**
     * 关键词文本
     */
    private String keywordText;

    /**
     * 关键词匹配类型
     */
    private String matchType; // exact, phrase, broad

    /**
     * 关键词状态
     */
    private String state; // enabled, paused, archived

    /**
     * 出价
     */
    private Double bid;

    /**
     * 关键词建议状态
     */
    private String suggested; // true, false

    /**
     * 关键词优先级
     */
    private Integer priority;

    /**
     * 关键词描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}