package com.hzltd.module.erplus.model.category;

import com.hzltd.module.erplus.model.common.ExtraParamRequest;
import com.hzltd.module.erplus.constant.LanguageEnum;
import lombok.Data;

@Data
public class GetCategoryAttributeRequest extends ExtraParamRequest {

    private Integer categoryId;

    private String attrId;

    private String attrName;

    private LanguageEnum language;

}
