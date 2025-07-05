package com.hzltd.module.erplus.controller.admin.spu.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ProductClaimSyncReqVO {

    @NotEmpty(message = "商品ID不能为空")
    private List<Integer> claimIds;
}
