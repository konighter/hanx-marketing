package com.hzltd.module.erplus.sys.dal.dataobject.hscodes;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * [Erplus] 海关编码(HS Code) DO
 *
 * @author hzadd
 */
@TableName("erplus_hs_codes")
//@KeySequence("erplus_hs_codes_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HsCodesDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * HS Code编码 (可能包含点)
     */
    private String code;
    /**
     * 编码描述
     */
    private String description;
    /**
     * 关联的内部产品分类ID (方便查找)
     */
    private Long categoryId;
    /**
     * 附加商品编码
     */
    private String extCode;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 低税率(优惠税率,最惠国)
     */
    private Double lowTax;
    /**
     * 高税率(普通税率)
     */
    private Double highTax;
    /**
     * 进口暂定税率
     */
    private Double importTax;
    /**
     * 进口消费税率
     */
    private Double importVatTax;
    /**
     * 增值税率
     */
    private Double vatTax;
    /**
     * 出口税率
     */
    private Double exportTax;
    /**
     * 出口暂定税率
     */
    private Double exportTaxProvision;
    /**
     * 法定第一计量单位
     */
    private String measureUnit1;
    /**
     * 法定第二计量单位
     */
    private String measureUnit2;
    /**
     * 监管代码
     */
    private String regulatoryCode;
    /**
     * 检验检疫代码
     */
    private String quarantineCode;
    /**
     * 申报要素
     */
    private String declarationElements;

}