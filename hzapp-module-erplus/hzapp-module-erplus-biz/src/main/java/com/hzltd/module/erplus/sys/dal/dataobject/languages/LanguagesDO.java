package com.hzltd.module.erplus.sys.dal.dataobject.languages;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * [Erplus] 语言定义 DO
 *
 * @author hzadd
 */
@TableName("erplus_languages")
@KeySequence("erplus_languages_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguagesDO extends BaseDO {

    /**
     * 语言ID
     */
    @TableId
    private Integer id;
    /**
     * Language code (e.g., en-US, zh-CN, fr-FR)
     */
    private String code;
    /**
     * 语言名称 (e.g., English (US), 中文 (简体))
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean isActive;

}