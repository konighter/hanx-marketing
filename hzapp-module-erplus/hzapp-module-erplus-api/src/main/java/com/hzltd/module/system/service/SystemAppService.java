package com.hzltd.module.system.service;

import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.system.AppModel;

public interface SystemAppService {

    /**
     * 根据平台获取对应的App信息
     * @param platform
     * @return
     */
    AppModel getApp(CrossPlatformEnum platform);

}
