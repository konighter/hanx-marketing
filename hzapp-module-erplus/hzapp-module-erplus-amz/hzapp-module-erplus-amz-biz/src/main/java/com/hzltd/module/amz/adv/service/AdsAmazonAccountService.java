package com.hzltd.module.amz.adv.service;

import com.hzltd.module.erplus.adv.api.AdsServiceRegister;
import com.hzltd.module.erplus.adv.service.AdsAccountApi;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;

@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass=AdsAccountApi.class)
public class AdsAmazonAccountService implements AdsAccountApi {
}
