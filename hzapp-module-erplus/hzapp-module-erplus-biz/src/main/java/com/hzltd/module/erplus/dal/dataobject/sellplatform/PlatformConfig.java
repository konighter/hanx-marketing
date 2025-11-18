package com.hzltd.module.erplus.dal.dataobject.sellplatform;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlatformConfig {

    /**
     * 支持的认证方式
     */
    private List<Integer> authType;

    /**
     * 认证地址
     */
    private String authEndPoint;

    /**
     * 不同销售区域的api地址
     */
    private Map<String, String> apiEndPoint;

}
