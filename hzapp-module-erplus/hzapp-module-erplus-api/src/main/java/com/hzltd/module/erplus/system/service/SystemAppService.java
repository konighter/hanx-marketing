package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.AppModel;

public interface SystemAppService {

    /**
     * 根据平台获取对应的App信息
     * @param platform
     * @return
     */
    AppModel getApp(CrossPlatformEnum platform);

}
