package com.hzltd.module.erplus.sys;

import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.sys.model.AppModel;

public interface SystemAppService {

    /**
     * 根据平台获取对应的App信息
     * @param platform
     * @return
     */
    AppModel getApp(CrossPlatformEnum platform);

}
