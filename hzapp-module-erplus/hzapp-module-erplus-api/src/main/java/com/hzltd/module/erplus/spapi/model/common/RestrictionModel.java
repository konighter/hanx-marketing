package com.hzltd.module.erplus.spapi.model.common;

import lombok.Data;

@Data
public class RestrictionModel<T> {

    private String filed;

    private Boolean required;

    private T defaultVal;
}
