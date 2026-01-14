package com.hzltd.module.erplus.model.pricing;

import com.hzltd.module.erplus.enums.CrossOperationStatus;
import com.hzltd.module.erplus.model.product.IssueModel;
import lombok.Data;

import java.util.List;

@Data
public class ChangeProductPriceResponse {

    /**
     * 操作状态
     */
    private CrossOperationStatus status;

    /**
     * 问题列表
     */
    private List<IssueModel> issues;

}
