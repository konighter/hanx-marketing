package com.hzltd.module.erplus.controller.admin.spu.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProductClaimSyncReqVO {

    @NotEmpty(message = "商品ID不能为空")
    private List<Integer> claimIds;
}
