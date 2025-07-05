package com.hzltd.module.erplus.controller.admin.productpub.vo;

import com.hzltd.module.erplus.enums.PublishTaskStatusEnum;
import lombok.Data;

@Data
public class ProductPublishTaskVO {
    private Long productId;
    private Long taskId;
    private PublishTaskStatusEnum status;

}
