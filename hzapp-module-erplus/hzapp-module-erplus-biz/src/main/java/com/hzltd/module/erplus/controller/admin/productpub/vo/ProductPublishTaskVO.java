package com.hzltd.module.erplus.controller.admin.productpub.vo;

import com.hzltd.module.erplus.spapi.enums.CrossProductPublishStatus;
import lombok.Data;


@Data
public class ProductPublishTaskVO {
    private Long productId;
    private Long taskId;
    private CrossProductPublishStatus status;

}
