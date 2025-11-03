package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.service.PlatformIdentity;

public interface CrossApiServiceRegister {

    <T> void registerApiService(PlatformIdentity platform, T service);
    
}
