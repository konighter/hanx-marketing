package com.hzltd.module.amz.spapi.controller.admin.listing.vo;

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

    @Schema(description = "字段映射关系（原始名称 -> 打平后的ID）")
    private java.util.Map<String, String> fieldMapping;

}
