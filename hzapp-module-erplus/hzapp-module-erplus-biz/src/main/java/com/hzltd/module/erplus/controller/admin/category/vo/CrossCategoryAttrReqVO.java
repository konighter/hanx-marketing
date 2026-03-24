package com.hzltd.module.erplus.controller.admin.category.vo;

import com.hzltd.module.spapi.model.system.BaseCrossRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class CrossCategoryAttrReqVO extends BaseCrossRequest {

    private String categoryId;

    private List<String> categoryIds;

    private Long spuId;

}
