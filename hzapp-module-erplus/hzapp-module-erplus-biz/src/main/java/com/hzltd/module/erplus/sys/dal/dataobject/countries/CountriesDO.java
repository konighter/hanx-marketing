package com.hzltd.module.erplus.sys.dal.dataobject.countries;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * [Erplus] 国家/地区定义 DO
 *
 * @author hzadd
 */
@TableName("erplus_countries")
@KeySequence("erplus_countries_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountriesDO extends BaseDO {

    /**
     * 国家ID
     */
    @TableId
    private Integer id;
    /**
     * ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)
     */
    @TableField("iso_code_2")
    private String isoCode2;
    /**
     * ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)
     */
    @TableField("iso_code_3")
    private String isoCode3;
    /**
     * 国家英文名称
     */
    private String name;
    /**
     * 默认语言代码 (e.g., en-US, zh-CN)
     */
    private String defaultLanguageCode;
    /**
     * 默认货币代码 (e.g., USD, CNY, GBP)
     */
    private String defaultCurrencyCode;
    /**
     * 是否作为目标市场启用
     */
    private Boolean isActive;

}