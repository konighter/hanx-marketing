package com.hzltd.module.erplus.controller.admin.productpotential.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProductpotentialStatusEnum implements IntArrayValuable {
    CREATED(0, "创建"),
    SUBMITTED(10, "已提交"),
    APPROVED(20, "已审批"),
    REJECTED(30, "已拒绝");

    private final Integer code;
    private final String name;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ProductpotentialStatusEnum::getCode).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static ProductpotentialStatusEnum getByCode(Integer code) {
        for (ProductpotentialStatusEnum productpotentialStatusEnum : ProductpotentialStatusEnum.values()) {
            if (productpotentialStatusEnum.getCode().equals(code)) {
                return productpotentialStatusEnum;
            }
        }
        return null;
    }
}
