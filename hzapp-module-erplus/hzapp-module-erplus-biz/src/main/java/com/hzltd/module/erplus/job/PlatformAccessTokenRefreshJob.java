package com.hzltd.module.erplus.job;

import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.module.erplus.service.shop.ShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlatformAccessTokenRefreshJob implements JobHandler {

    @Resource
    private ShopService shopService;


    @Override
    public String execute(String param) throws Exception {

        log.info("刷新所有店铺的平台 AccessToken 开始");
        shopService.refreshShopPlatformAccessToken();
        log.info("刷新所有店铺的平台 AccessToken 结束");

        return "";
    }
}
