package com.hzltd.module.erplus.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    APPROVE(1),
    REJECT(2);


    private Integer value;

    AuditAction(Integer value) {
        this.value = value;
    }

    public static AuditAction of(Integer value) {
        for (AuditAction action : AuditAction.values()) {
            if (action.getValue().equals(value)) {
                return action;
            }
        }
        return null;
    }

}
