package com.hzltd.module.erplus.dal.dataobject.spu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品详细材料信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaterial implements Serializable {
    @Schema(description = "材料名称", example = "棉")
    private String name;
    @Schema(description = "含量百分比", example = "80.0")
    private Double percentage;
    @Schema(description = "材料类型", example = "天然纤维")
    private String type;
}
