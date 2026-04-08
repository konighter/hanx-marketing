package com.hzltd.module.erplus.controller.admin.listing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "管理后台 - 亚马逊刊登表单配置 VO")
@Data
public class AmzListingFormConfigVO {

    @Schema(description = "产品类型", example = "ABIS_BOOK")
    private String productType;

    @Schema(description = "表单字段列表")
    private List<AmzListingFormFieldVO> fields;

}
