package com.hzltd.module.erpls.api.model.common;

import lombok.Data;

@Data
public class RestrictionModel<T> {

    private String filed;

    private Boolean required;

    private T dftVal;
}
