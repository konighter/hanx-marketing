package com.hzltd.module.erplus.spapi.model.product;

import com.hzltd.module.erplus.spapi.model.common.SkuModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductResponse {

    private String productId;

    private SkuModel skuModel;

    private List<IssueModel> issues;

}
