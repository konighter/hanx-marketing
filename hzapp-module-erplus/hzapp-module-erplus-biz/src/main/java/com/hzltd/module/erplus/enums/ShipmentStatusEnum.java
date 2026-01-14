package com.hzltd.module.erplus.enums;

public enum ShipmentStatusEnum {

    /**
     * 初始化
     */
    INIT(0),
    /**
     * 审核中
     */
    AUDITING(10),
    /**
     * 待装箱
     */
    PENDING_BOXING(20),

    /**
     * 物流配货中
     */
    PENDING_SHIPMENT(30),
    /**
     * 待贴标
     */
    PENDING_LABEL(40),

    /**
     * 待发货
     */
    PENDING_DELIVERY(50),
    /**
     * 已发货
     */
    SHIPPED(90),
    /**
     * 已取消
     */
    CANCELED(99);

    private final Integer status;

    ShipmentStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public static ShipmentStatusEnum of(Integer status) {
        for (ShipmentStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant with status " + status);
    }
}
