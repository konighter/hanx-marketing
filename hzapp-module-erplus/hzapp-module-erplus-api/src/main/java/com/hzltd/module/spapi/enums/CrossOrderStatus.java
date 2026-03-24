package com.hzltd.module.spapi.enums;


import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 *         @SerializedName("Pending")
 *         PENDING(" Pending "),
 *         @SerializedName("Unshipped")
 *         UNSHIPPED(" Unshipped "),
 *         @SerializedName("PartiallyShipped")
 *         PARTIALLYSHIPPED(" PartiallyShipped "),
 *         @SerializedName("Shipped")
 *         SHIPPED(" Shipped "),
 *         @SerializedName("Canceled")
 *         CANCELED(" Canceled "),
 *         @SerializedName("Unfulfillable")
 *         UNFULFILLABLE(" Unfulfillable "),
 *         @SerializedName("InvoiceUnconfirmed")
 *         INVOICEUNCONFIRMED(" InvoiceUnconfirmed "),
 *         @SerializedName("PendingAvailability")
 *         PENDINGAVAILABILITY(" PendingAvailability ");
 */
@Getter
@RequiredArgsConstructor
public enum CrossOrderStatus implements IntArrayValuable  {
    PENDING(10, "进行中", "PENDING"),
    UNSHIPPED(20, "未发货", "UNSHIPPED"),
    PARTIALLYSHIPPED(30, "部分发货", "PARTIALLYSHIPPED"),
    SHIPPED(40, "已发货", "SHIPPED"),
    CANCELED(50, "已取消", "CANCELED"),
    UNFULFILLABLE(60, "不可配送", "UNFULFILLABLE"),
    INVOICEUNCONFIRMED(70, "发票未确认", "INVOICEUNCONFIRMED"),
    PENDINGAVAILABILITY(80, "待库存确认", "PENDINGAVAILABILITY"),
    ;
    private final Integer status;
    private final String name;
    private final String code;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossOrderStatus::getStatus).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static CrossOrderStatus of(String code) {
        return Arrays.stream(values()).filter(item -> item.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

    public static CrossOrderStatus of(Integer status) {
        return Arrays.stream(values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }

}
