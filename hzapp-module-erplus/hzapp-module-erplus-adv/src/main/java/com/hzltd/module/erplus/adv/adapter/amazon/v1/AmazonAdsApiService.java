package com.hzltd.module.erplus.adv.adapter.amazon.v1;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.model.AmzCampaign;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.model.AmzCampaignListResponse;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Amazon Ads V1 API 服务层
 *
 * 职责：
 * - 封装 Campaign 管理的业务操作（CRUD）
 * - 使用 AmazonAdsApiClient 进行 HTTP 调用
 * - 处理请求/响应模型的转换
 *
 * 使用 V1 通用 Campaign 模型，适用于所有广告产品（SP/SB/SD/DSP）
 */
@Slf4j
@Service
public class AmazonAdsApiService {

    private static final String CAMPAIGNS_PATH = "/campaigns";

    @Resource
    private AdsApiClient apiClient;

    // ==================== Campaign 管理 ====================

    /**
     * 查询 Campaign 列表
     *
     * @param credential Amazon 凭证
     * @param accountId  Amazon 广告账户 ID
     * @param profileId  Profile ID (站点维度)
     * @param baseUrl    区域 API 端点 (如 https://advertising-api.amazon.com)
     * @param request    查询请求（含过滤条件和分页）
     * @return Campaign 列表响应（含分页 Token）
     */
    public AmzCampaignListResponse listCampaigns(AdsAccountCredentialDO credential, String accountId,
                                                  String profileId, String baseUrl,
                                                  Object request) {

        String url = baseUrl + "/adsApi/v1/query/campaigns";
        log.info("[listCampaigns] accountId={}, profileId={}, url={}", accountId, profileId, url);

        String resp = apiClient.post(credential, accountId, profileId, url, request);
        return JsonUtils.parseObject(resp, AmzCampaignListResponse.class);
    }

    /**
     * 获取单个 Campaign 详情
     */
    public AmzCampaign getCampaign(AdsAccountCredentialDO credential, String accountId,
                                    String profileId, String baseUrl, String campaignId) {
        String url = baseUrl + CAMPAIGNS_PATH + "/" + campaignId;
        log.info("[getCampaign] accountId={}, campaignId={}", accountId, campaignId);

        String resp = apiClient.get(credential, accountId, profileId, url);
        return JsonUtils.parseObject(resp, AmzCampaign.class);
    }

    /**
     * 创建 Campaign（批量）
     */
    public String createCampaigns(AdsAccountCredentialDO credential, String accountId,
                                   String profileId, String baseUrl, List<AmzCampaign> campaigns) {
        String url = baseUrl + CAMPAIGNS_PATH;
        log.info("[createCampaigns] accountId={}, count={}", accountId, campaigns.size());

        Map<String, Object> body = Map.of("campaigns", campaigns);
        return apiClient.post(credential, accountId, profileId, url, body);
    }

    /**
     * 更新 Campaign（批量，需包含 campaignId）
     */
    public String updateCampaigns(AdsAccountCredentialDO credential, String accountId,
                                   String profileId, String baseUrl, List<AmzCampaign> campaigns) {
        String url = baseUrl + CAMPAIGNS_PATH;
        log.info("[updateCampaigns] accountId={}, count={}", accountId, campaigns.size());

        Map<String, Object> body = Map.of("campaigns", campaigns);
        return apiClient.put(credential, accountId, profileId, url, body);
    }

    /**
     * 删除（归档）Campaign
     */
    public String deleteCampaign(AdsAccountCredentialDO credential, String accountId,
                                  String profileId, String baseUrl, String campaignId) {
        String url = baseUrl + CAMPAIGNS_PATH + "/" + campaignId;
        log.info("[deleteCampaign] accountId={}, campaignId={}", accountId, campaignId);

        return apiClient.delete(credential, accountId, profileId, url);
    }

    /**
     * 便捷方法：更新单个 Campaign
     */
    public String updateCampaign(AdsAccountCredentialDO credential, String accountId,
                                  String profileId, String baseUrl, AmzCampaign campaign) {
        return updateCampaigns(credential, accountId, profileId, baseUrl, Collections.singletonList(campaign));
    }
}
