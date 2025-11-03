package com.hzltd.module.erplus.model.common;

import lombok.Data;

import java.util.List;

@Data
public class ProductAttributeModel {

    private String attrId;

    private String attrName;

    private List<AttrValue> attrValues;

    private List<String> buildInAttrValues;


    @Data
    public static class AttrValue {
        private String valueId;

        /**
         * 如果内置的属性值不满足要求, 可以自定义值
         */
        private String customerValue;

    }


}
