package com.hzltd.module.erplus.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * - DRAFT
 * - PENDING
 * - FAILED
 * - ACTIVATE
 * - SELLER_DEACTIVATED
 * - PLATFORM_DEACTIVATED
 * - FREEZE
 * - DELETED
 */
@Getter
@RequiredArgsConstructor
public enum CrossListingStatus implements IntArrayValuable {

    DRAFT(0, "草稿", "DRAFT"),
    PENDING(10, "进行中", "PENDING"),
    FAILED(11, "失败", "FAILED"),
    ACTIVATE(19, "在售", "ACTIVATE"),
    SELLER_DEACTIVATED(91, "卖家下架", "SELLER_DEACTIVATED"),
    PLATFORM_DEACTIVATED(92, "平台下架", "PLATFORM_DEACTIVATED"),
    FREEZE(93, "冻结", "FREEZE"),
    DELETED(99, "删除", "DELETED");


    private final Integer status;
    private final String name;
    private final String code;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossListingStatus::getStatus).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static CrossListingStatus of(String code) {
        return Arrays.stream(values()).filter(item -> item.getCode().equals(code)).findFirst().orElse(null);
    }

    public static CrossListingStatus of(Integer status) {
        return Arrays.stream(values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }


}
