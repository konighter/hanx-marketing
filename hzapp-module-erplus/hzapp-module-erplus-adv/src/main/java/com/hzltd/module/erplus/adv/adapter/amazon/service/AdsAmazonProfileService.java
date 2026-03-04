package com.hzltd.module.erplus.adv.adapter.amazon.service;

import com.hzltd.module.erplus.adv.adapter.amazon.AbstractAmazonAdsAdapter;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.enums.amz.AmazonRegionEnum;

import java.util.List;


/**
 * 亚马逊广告 Profile Service 接口
 */
public interface AdsAmazonProfileService {

    /**
     * 同步账号下的站点 (Profiles)
     * 
     * @param account 广告账号
     * @param credential 凭证
     */
    void syncProfiles(AdsAccountDO account, AdsAccountCredentialDO credential);

    /**
     * 为单个 Profile 创建或同步 Stream 订阅
     *
     * @param profile Profile 对象
     */
    void syncStreamSubscription(AdsAmazonProfileDO profile);

    /**
     * 为账号下的所有已开启站点创建或同步 Stream 订阅
     *
     * @param accountId 账号编号
     */
    void syncStreamSubscriptionByAccountId(Long accountId);

    /**
     * 获得亚马逊广告 Profile 列表
     *
     * @param accountId 账号编号
     * @return Profile 列表
     */
    List<AdsAmazonProfileDO> getAmazonProfileList(Long accountId);

    /**
     * 从亚马逊广告 API 获取 Profile 列表
     *
     * @param credential 凭证
     * @param region 站点
     * @return Profile 列表
     */
    List<AbstractAmazonAdsAdapter.AmzProfileVO> fetchProfiles(AdsAccountDO account, AdsAccountCredentialDO credential, AmazonRegionEnum region);

}
