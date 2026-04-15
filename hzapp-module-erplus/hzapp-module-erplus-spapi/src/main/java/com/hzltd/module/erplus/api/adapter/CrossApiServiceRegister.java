package com.hzltd.module.erplus.api.adapter;

import com.hzltd.module.erplus.spapi.service.PlatformIdentity;

public interface CrossApiServiceRegister {

    <T>  void registerApiService(PlatformIdentity platform, T service);


    
}
