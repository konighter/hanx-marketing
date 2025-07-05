package com.hzltd.module.erpls.api.constant;

import com.hzltd.framework.common.core.IntArrayValuable;

public enum FulfillTypeEnum implements IntArrayValuable {
    /**
     * 商家自配送
     */
    FBM,
    /**
     * 平台配送
     */
    FBA,
    /**
     * 合作伙伴配送
     */
    FBP,

    /**
     * 全托管模式
     */
    FMA,
    ;


    @Override
    public int[] array() {
        return new int[0];
    }
}
