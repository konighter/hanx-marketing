package com.hzltd.module.erplus.api.config;

import com.hzltd.module.spapi.service.PlatformIdentity;

public interface CrossApiServiceRegister {

    <T>  void registerApiService(PlatformIdentity platform, T service);


    
}
