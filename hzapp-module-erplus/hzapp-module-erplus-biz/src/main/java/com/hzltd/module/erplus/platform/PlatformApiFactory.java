package com.hzltd.module.erplus.platform;

import com.google.common.collect.Maps;
import com.hzltd.framework.common.exception.ErrorCode;
import com.hzltd.framework.common.exception.ServiceException;

import java.util.Map;

public class PlatformApiFactory {

    private static Map<String, PlatformProductApi> platformApiMap = Maps.newConcurrentMap();

    public static PlatformProductApi getPlatformApi(String platform){
        PlatformProductApi api = platformApiMap.get(platform);
        if (api == null) {
            throw new ServiceException(new ErrorCode(100_00, "未找到对应平台接口"));
        }
        return api;
    }

    public static void register(String platform, PlatformProductApi platformApi){
        platformApiMap.putIfAbsent(platform, platformApi);
    }
}
