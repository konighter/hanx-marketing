package com.hzltd.module.spapi.model.system;

import java.util.List;
import java.util.Map;

public class SellPlatformModel {
    /**
     * ID
     */
    private Integer id;
    /**
     * 平台名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 配送模式
     */
    private List<String> fullFillModels;

    // =========== 配置信息 =============
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
