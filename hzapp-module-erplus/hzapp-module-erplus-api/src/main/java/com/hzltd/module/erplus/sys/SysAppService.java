package com.hzltd.module.erplus.sys;

import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.sys.domain.AppVO;

public interface SysAppService {

    /**
     * 根据平台获取对应的App信息
     * @param platform
     * @return
     */
    AppVO getApp(CrossPlatformEnum platform);

}
