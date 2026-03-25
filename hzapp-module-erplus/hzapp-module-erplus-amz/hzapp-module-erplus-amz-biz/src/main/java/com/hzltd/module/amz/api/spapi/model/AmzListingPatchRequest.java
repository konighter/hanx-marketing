package com.hzltd.module.amz.api.spapi.model;

import lombok.Data;

import java.util.List;

@Data
public class AmzListingPatchRequest {

    private String productType;

    private List<AmzListingPatchBody> patches;


}
