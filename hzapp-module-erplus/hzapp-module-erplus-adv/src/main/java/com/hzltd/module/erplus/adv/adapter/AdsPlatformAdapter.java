package com.hzltd.module.erplus.adv.adapter;

import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsTokenResult;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;

import java.util.List;

import com.hzltd.module.erplus.adv.metadata.vo.AdsCampaignVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdGroupVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsKeywordVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdVO;

/**
 * 广告平台统一适配器接口
 */
public interface AdsPlatformAdapter {

    /**
     * 获取对应的平台枚举
     */
    AdsPlatformEnum getPlatform();

    // ==================== 认证相关 (Auth) ====================

    /**
     * 获取 OAuth2 授权链接
     */
    String getAuthorizeUrl(String state);

    /**
     * 通过授权 code 交换 Token
     */
    AdsTokenResult exchangeToken(String code);

    /**
     * 刷新 Access Token
     */
    AdsTokenResult refreshToken(AdsAccountCredentialDO credential);

    // ==================== 账号相关 ====================

    /**
     * Initialize platform accounts using credentials.
     *
     * @param credential The credential to use for discovery.
     * @param shopId     The shop ID associated with this authorization.
     */
    List<AdsAccountVO> fetchAdsAccounts(AdsAccountCredentialDO credential, Long shopId);


    // ==================== 业务 API (Metadata) ====================

    /**
     * 查询广告计划列表
     */
    List<AdsCampaignVO> queryCampaigns(Long accountId, AdsQueryRequest request);

    /**
     * 查询广告组列表
     */
    List<AdsAdGroupVO> queryGroups(Long accountId, AdsQueryRequest request);

    /**
     * 查询关键词和定向
     */
    List<AdsKeywordVO> queryTargets(Long accountId, AdsQueryRequest request);

    /**
     * 查询广告列表
     */
    List<AdsAdVO> queryAds(Long accountId, AdsQueryRequest request);

    /**
     * 更新实体状态
     */
    Boolean updateStatus(Long accountId, AdsStatusUpdateRequest request);


    /**
     * 账号处理完成后的 Hook
     * 供各个适配器做个性化处理
     */
    default void postAccountAction(AdsAccountDO account) {
        // 默认不进行额外处理
    }

}
