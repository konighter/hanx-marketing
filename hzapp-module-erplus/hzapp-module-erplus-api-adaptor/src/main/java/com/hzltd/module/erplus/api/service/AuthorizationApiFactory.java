package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.config.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.service.authorization.AuthorizationApi;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationApiFactory extends AbsCrossApiServiceFactory<AuthorizationApi> {
    @Override
    public Class<AuthorizationApi> getCrossApiServiceClass() {
        return AuthorizationApi.class;
    }
}
