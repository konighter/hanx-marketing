package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import com.hzltd.module.amz.adv.controller.admin.vo.AmzBaseRequest;
import lombok.Data;

import java.util.List;

@Data
public class AmzLabelsRequest extends AmzBaseRequest {

    private List<String> shipments;

    private String shipmentId;

    private String pageType;

    private String labelType;

    private List<String> boxIds;

    private Integer pageStartIndex = 1;
}
