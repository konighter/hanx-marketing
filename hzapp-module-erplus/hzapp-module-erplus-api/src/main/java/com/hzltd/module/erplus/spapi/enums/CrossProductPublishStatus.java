package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CrossProductPublishStatus implements IntArrayValuable {
    INIT(0, "创建"),

    /**
     * 定时提交, 创建后更新为此状态
     */
    AUDITING(10, "待发布"),


    PUBLISHING(90, "发布中"),
    PUBLISH_FAIL(91, "发布失败"),
    PUBLISH_SUC(99, "发布成功");

    private final Integer status;
    private final String name;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossProductPublishStatus::getStatus).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static CrossProductPublishStatus of(Integer status) {
        return Arrays.stream(CrossProductPublishStatus.values()).filter(s -> s.getStatus().equals(status)).findFirst().get();



    }

}
