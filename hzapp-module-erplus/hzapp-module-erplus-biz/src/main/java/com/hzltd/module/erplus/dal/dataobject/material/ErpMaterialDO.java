package com.hzltd.module.erplus.dal.dataobject.material;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 耗材定义 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_material")
@KeySequence("erplus_material_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpMaterialDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 耗材名称
     */
    private String name;
    /**
     * 耗材编码
     */
    private String code;
    /**
     * 耗材类型
     */
    private String category;
    /**
     * 单位
     */
    private Long unit;
    /**
     * 长(cm)
     */
    private BigDecimal length;
    /**
     * 宽(cm)
     */
    private BigDecimal width;
    /**
     * 高(cm)
     */
    private BigDecimal height;
    /**
     * 重量(g)
     */
    private BigDecimal weight;
    /**
     * 体积(cm³)
     */
    private BigDecimal volume;
    /**
     * 容积
     */
    private BigDecimal capacity;
    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

}
