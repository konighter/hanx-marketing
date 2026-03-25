package com.hzltd.module.erplus.controller.admin.category.vo;

import com.hzltd.module.spapi.enums.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrossCategoryRespVO {

    private LanguageEnum language;

    private List<CategoryVO> categories;


}
