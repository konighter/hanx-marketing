package com.hzltd.module.amz.api.spapi.model;

import lombok.Data;

import java.util.List;

@Data
public class AmzListingPatchBody {

    private OperationEnum op;

    private String path;

    private List<Object> value;




}
