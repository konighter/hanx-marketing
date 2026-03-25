package com.hzltd.module.amz.controller.admin.vo;

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
