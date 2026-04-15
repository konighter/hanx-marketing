package com.hzltd.module.erplus.framework.aop;

import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.system.annotation.CrossplatformApiLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 跨平台 API 日志切面
 */
@Slf4j
@Aspect
@Component
public class CrossplatformApiLogAspect {

    @Around("@annotation(crossplatformApiLog)")
    public Object around(ProceedingJoinPoint joinPoint, CrossplatformApiLog crossplatformApiLog) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String platform = crossplatformApiLog.platform();
        if (platform.isEmpty()) {
            // 自动从类名中识别平台
            String className = joinPoint.getTarget().getClass().getSimpleName();
            platform = className.contains("Ads") ? "AmazonAds" : "SP-API";
        }

        String shopId = "unknown";
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            Object firstArg = args[0];
            if (firstArg instanceof ApiRequest<?> apiRequest) {
                shopId = apiRequest.getShopId();
            } else if (firstArg instanceof AdsRequest<?> adsRequest) {
                shopId = String.valueOf(adsRequest.getShopId());
            }
        }

        long startTime = System.currentTimeMillis();
        boolean success = false;
        Object result = null;
        try {
            result = joinPoint.proceed();
            
            // 进一步判断返回对象中的成功状态
            if (result instanceof ApiResponse<?> apiResponse) {
                success = apiResponse.success();
            } else if (result instanceof AdsResponse<?> adsResponse) {
                success = adsResponse.isSuccess();
            } else {
                success = true;
            }
            
            return result;
        } catch (Throwable e) {
            log.error("[API Log] Call Error - Platform: {}, ShopId: {}, Method: {}, Error: {}", 
                platform, shopId, methodName, e.getMessage());
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[API Log] {} - Platform: {}, ShopId: {}, Method: {}, Success: {}, Duration: {}ms", 
                success ? "Call Success" : "Call Failed", platform, shopId, methodName, success, duration);
        }
    }
}
