package com.hzltd.module.amz.adv;

import com.hzltd.module.adv.api.AdsServiceRegister;
import com.hzltd.module.adv.service.AdsAccountApi;
import com.hzltd.module.system.enums.AdsPlatformEnum;

@AdsServiceRegister(platform = AdsPlatformEnum.AMAZON, serviceClass=AdsAccountApi.class)
public class AdsAmazonAccountService implements AdsAccountApi {
}
