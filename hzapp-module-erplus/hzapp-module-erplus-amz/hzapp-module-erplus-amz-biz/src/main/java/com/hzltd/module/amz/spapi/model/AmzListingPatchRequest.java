package com.hzltd.module.amz.spapi.model;

import lombok.Data;

import java.util.List;

@Data
public class AmzListingPatchRequest {

    private String productType;

    private List<AmzListingPatchBody> patches;


}
