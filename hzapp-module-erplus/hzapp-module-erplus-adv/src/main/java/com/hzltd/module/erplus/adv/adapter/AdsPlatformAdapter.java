package com.hzltd.module.erplus.adv.adapter;

import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;

import java.util.List;

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
    List<AdsCampaignModel> queryCampaigns(Long accountId, AdsQueryRequest request);

    /**
     * 查询广告组列表
     */
    List<AdsAdGroupModel> queryGroups(Long accountId, AdsQueryRequest request);

    /**
     * 查询关键词和定向
     */
    List<AdsTargetModel> queryTargets(Long accountId, AdsQueryRequest request);

    /**
     * 查询广告列表
     */
    List<AdsAdModel> queryAds(Long accountId, AdsQueryRequest request);

    /**
     * 更新实体状态
     */
    Boolean updateStatus(Long accountId, AdsStatusUpdateRequest request);

    /**
     * 更新广告活动预算
     */
    default Boolean updateBudget(Long accountId, AdsBudgetUpdateRequest request) {
        throw new UnsupportedOperationException("Platform does not support budget update");
    }

    /**
     * 更新关键词/Target 出价
     */
    default Boolean updateBid(Long accountId, AdsBidUpdateRequest request) {
        throw new UnsupportedOperationException("Platform does not support bid update");
    }


    /**
     * 账号处理完成后的 Hook
     * 供各个适配器做个性化处理
     */
    default void postAccountAction(AdsAccountDO account) {
        // 默认不进行额外处理
    }

    /**
     * 广告计划更新后的平台同步 Hook
     * 本地数据已保存成功后调用，供各平台适配器将变更同步到远端 API
     *
     * @param account   广告账户
     * @param campaign  更新后的计划 DO（已持久化）
     * @param extData   本次提交的扩展数据（平台配置 JSON），可能为 null
     */
    default void postCampaignUpdate(AdsAccountDO account, AdsCampaignDO campaign, Object extData) {
        // 默认不进行额外处理，各平台适配器按需覆写
    }

    /**
     * 创建优化规则后的平台同步 Hook
     *
     * @param account    广告账户
     * @param profileId  站点ID (Amazon ProfileId)
     * @param saveReqVO  保存请求参数
     * @return 平台返回的原始响应 (JSON)
     */
    default String postOptimizationRuleCreate(AdsAccountDO account, String profileId, Object saveReqVO) {
        throw new UnsupportedOperationException("Platform does not support optimization rule creation");
    }

    /**
     * 关联优化规则后的平台同步 Hook
     *
     * @param account         广告账户
     * @param campaignId      平台原始计划ID
     * @param profileId       站点ID
     * @param associateReqVO  关联请求参数
     */
    default void postOptimizationRuleAssociate(AdsAccountDO account, String campaignId, String profileId, Object associateReqVO) {
        throw new UnsupportedOperationException("Platform does not support optimization rule association");
    }

    /**
     * 执行绩效数据同步任务
     * 由具体适配器决定是同步还是异步处理，并更新任务状态
     *
     * @param task 同步任务记录
     */
    default void executePerformanceSyncTask(AdsSyncTaskDO task) {
        throw new UnsupportedOperationException("Platform does not support performance sync task execution");
    }

}
