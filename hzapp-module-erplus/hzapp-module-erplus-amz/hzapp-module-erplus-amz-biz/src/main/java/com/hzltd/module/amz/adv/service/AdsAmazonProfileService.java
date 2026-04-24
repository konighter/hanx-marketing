package com.hzltd.module.amz.adv.service;

import com.hzltd.module.amz.api.adv.AbstractAmazonAdsAdapter;
import com.hzltd.module.amz.api.enums.AmazonRegionEnum;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;

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
     * 通过账号ID同步亚马逊广告 Profiles
     *
     * @param accountId 账号编号
     */
    void syncByAccountId(Long accountId);

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

    /**
     * 查询店铺绑定的profile
     * @param shopId
     * @return
     */
    AdsAmazonProfileDO getProfileByShopId(Long shopId);

    /**
     * 查询店铺绑定的profile列表
     * @param shopId
     * @return
     */
    List<AdsAmazonProfileDO> getProfileListByShopId(Long shopId);
}
