package com.hzltd.module.erplus.controller.admin.productassets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 商品素材新增/修改 Request VO")
@Data
public class ProductAssetsSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22568")
    private Integer id;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2540")
    private Integer productId;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private Integer productName;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer type;

    @Schema(description = "来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer source;

    @Schema(description = "素材链接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "素材链接不能为空")
    private String assetLink;

    @Schema(description = "素材信息")
    private String assetInfo;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

}