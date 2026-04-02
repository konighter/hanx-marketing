package com.hzltd.module.erplus.controller.admin.listing.vo;

import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductListingReqVO {

    @NotNull
    private Long spuId;

    @NotNull
    private Long platformId;

    @NotEmpty
    private List<Long> shopIds;

    @NotEmpty
    private List<String> categoryId;

    private Boolean translateText;

    @NotEmpty
    private Map<String, Object> attributes;


    private List<CategoryAttributeModel> variationAttributes;

    @NotEmpty
    private List<ListingSkuVO> skus;

}
