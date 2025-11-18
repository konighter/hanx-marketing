package com.hzltd.module.erplus.sys.model;

import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import lombok.Data;

import java.time.LocalDateTime;



@Data
public class ShopModel {
    /**
     * ID
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 平台
     */
    private Integer platform;
    /**
     * 区域
     */
    private Integer region;
    /**
     * 授权信息
     */
    private AuthorizationModel authInfo;
    /**
     * 授权刷新
     */
//    private String authRefreshInfo;
    /**
     * 授权开始时间
     */
    private LocalDateTime authStartTime;
    /**
     * 授权失效时间
     */
    private LocalDateTime authExpTime;


}
