package com.hzltd.module.erplus.spapi.model.category;

import lombok.Data;

@Data
public class AttributeValueModel {


    private String value;

    private String valueName;

    public static AttributeValueModel of(String value, String valueName) {
        AttributeValueModel model = new AttributeValueModel();
        model.setValue(value);
        model.setValueName(valueName);
        return model;
    }
}
