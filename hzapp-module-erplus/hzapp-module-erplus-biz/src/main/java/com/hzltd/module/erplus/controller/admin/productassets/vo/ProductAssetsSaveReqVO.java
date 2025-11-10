package com.hzltd.module.erplus.controller.admin.productassets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 商品素材新增/修改 Request VO")
@Data
public class ProductAssetsSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22568")
    private Integer id;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2540")
    @NotNull(message = "产品ID不能为空")
    private Integer productId;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotNull(message = "产品名称不能为空")
    private Integer productName;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "素材类型不能为空")
    private Integer type;

    @Schema(description = "素材链接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "素材链接不能为空")
    private String assetLink;

    @Schema(description = "素材信息")
    private String assetInfo;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}