package com.hzltd.module.erpls.api.model.common;

import lombok.Data;

@Data
public class Image {
    /**
     * 上传到个平台生成的资源ID
     */
    private String resourceId;

    private String url;

    private String uri;

    private Integer width;

    private Integer length;

    /**
     * 单位KB
     */
    private Integer size;

}
