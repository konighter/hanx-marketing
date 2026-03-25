package com.hzltd.module.spapi.model.category;

import com.hzltd.module.spapi.enums.LanguageEnum;
import com.hzltd.module.spapi.model.common.ExtraParamRequest;
import lombok.Data;

import java.util.List;

@Data
public class GetCategoryAttributeRequest extends ExtraParamRequest {

    private String categoryId;

    private List<String> categoryIds;

    private String attrId;

    private String attrName;

    private LanguageEnum language;

}
