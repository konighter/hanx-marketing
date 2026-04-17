package com.hzltd.module.erplus.system.enums;

import com.hzltd.module.erplus.system.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品属性 Key 枚举
 * 固化属性 Key 与 DTO 的映射关系
 *
 * @author 翰展科技
 */
@Getter
@AllArgsConstructor
public enum ProductAttrKeyEnum {

    /**
     * 物流信息
     */
    LOGISTICS("logistics", "物流信息", ProductLogisticsAttrDTO.class),
    
    /**
     * 采购信息
     */
    PURCHASE("purchase", "采购信息", ProductPurchaseAttrDTO.class),
    
    /**
     * 海关报关
     */
    CUSTOMS("customs", "海关报关", ProductCustomsAttrDTO.class),
    
    /**
     * 合规资质
     */
    COMPLIANCE("compliance", "合规资质", ProductComplianceAttrDTO.class),

    /**
     * 图片信息
     */
    MEDIA("media", "图片信息", ProductMediaAttrDTO.class);

    /**
     * 属性 Key
     */
    private final String key;
    /**
     * 属性名称
     */
    private final String name;
    /**
     * 对应的 DTO 类型
     */
    private final Class<?> clazz;

    public static ProductAttrKeyEnum findByKey(String key) {
        for (ProductAttrKeyEnum value : values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }
}
