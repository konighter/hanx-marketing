package com.hzltd.module.erplus.api.adapter;

import com.hzltd.module.spapi.service.PlatformIdentity;

public interface CrossApiServiceRegister {

    <T>  void registerApiService(PlatformIdentity platform, T service);


    
}
