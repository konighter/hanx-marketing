package com.hzltd.module.erplus.spapi.model.pricing;

import com.hzltd.module.erplus.spapi.enums.CrossOperationStatus;
import com.hzltd.module.erplus.spapi.model.product.IssueModel;
import lombok.Data;

import java.util.List;

@Data
public class ChangePriceResponse {

    /**
     * 操作状态
     */
    private CrossOperationStatus status;

    /**
     * 问题列表
     */
    private List<IssueModel> issues;

}
