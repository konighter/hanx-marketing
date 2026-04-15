package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.Data;

import java.util.List;

@Data
public class CrossOrderSyncRequest extends CrossOrderPageRequest{
    private List<String> platformOrderIds;
}
