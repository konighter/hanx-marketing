package com.hzltd.module.spapi.model.common;

import lombok.Data;

import java.io.File;

@Data
public class MediaModel {

    private String id;

    private String fileName;

    /**
     * 图片-IMG，视频-VIDEO，文件-FILE等
     */
    private String mediaType;

    private String uri;

    private String url;

    private Integer height;

    private Integer width;

    /**
     * 单位k
     */
    private Integer size;

    private File file;

    /**
     * 使用场景
     */
    private String userCase;

}
