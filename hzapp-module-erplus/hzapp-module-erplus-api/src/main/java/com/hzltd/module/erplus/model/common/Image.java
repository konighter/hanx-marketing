package com.hzltd.module.erplus.model.common;

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

    private Integer height;

    /**
     * 单位KB
     */
    private Integer size;

}
