package com.hzltd.module.erplus.dal.dataobject.spu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品认证信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCertification implements Serializable {
    @Schema(description = "认证名称", example = "CE认证")
    private String name;
    @Schema(description = "认证值", example = "CE123456")
    private String value;
}
