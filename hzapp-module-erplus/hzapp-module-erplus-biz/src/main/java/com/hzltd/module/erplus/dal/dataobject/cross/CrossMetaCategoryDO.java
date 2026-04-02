package com.hzltd.module.erplus.dal.dataobject.cross;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 跨境平台品类 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_meta_category")
@KeySequence("erplus_cross_meta_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossMetaCategoryDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 品类标识
     */
    private String categoryCode;
    /**
     * 品类名
     */
    private String categoryName;
    /**
     * 父品类
     */
    private String parentCategoryCode;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 是否叶子节点
     */
    private Boolean leaf;
    /**
     * 平台ID
     */
    private Integer platformId;


}