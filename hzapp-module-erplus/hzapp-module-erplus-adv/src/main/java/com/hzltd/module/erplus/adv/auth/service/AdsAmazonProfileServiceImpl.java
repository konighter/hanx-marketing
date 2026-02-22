package com.hzltd.module.erplus.adv.auth.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 亚马逊广告 Profile Service 实现类
 */
@Service
@Validated
public class AdsAmazonProfileServiceImpl implements AdsAmazonProfileService {

    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Override
    public List<AdsAmazonProfileDO> getAmazonProfileList(Long accountId) {
        return adsAmazonProfileMapper.selectList(AdsAmazonProfileDO::getAccountId, accountId);
    }

}
