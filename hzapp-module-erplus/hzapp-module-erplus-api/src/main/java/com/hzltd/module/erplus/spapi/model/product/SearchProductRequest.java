package com.hzltd.module.erplus.spapi.model.product;

import cn.hutool.core.date.DateTime;
import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class SearchProductRequest extends PageParam {

    private String cursor;

    private boolean ifAllContent = false;

    private DateTime createTimeStart;

    private DateTime createTimeEnd;

    private DateTime updateTimeStart;

    private DateTime updateTimeEnd;

    private List<String> sellerSkus;

    private List<String> productCodes;


}
