package com.hzltd.module.erplus.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - ERP 产品分类列表 Request VO")
@Data
public class ProductCategoryListReqVO {

    @Schema(description = "分类名称", example = "芋艿")
    private String name;

    @Schema(description = "开启状态", example = "1")
    private Integer status;

}