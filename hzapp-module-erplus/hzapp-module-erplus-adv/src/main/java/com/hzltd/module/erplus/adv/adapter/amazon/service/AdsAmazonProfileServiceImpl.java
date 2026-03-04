package com.hzltd.module.erplus.adv.adapter.amazon.service;

import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.amazon.AbstractAmazonAdsAdapter;
import com.hzltd.module.erplus.adv.adapter.amazon.AmazonAdsAdapter;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.AmazonAdsApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.v3.AmazonAdsReportApiService;
import com.hzltd.module.erplus.adv.adapter.amazon.v3.model.AmzStreamSubscriptionRequest;
import com.hzltd.module.erplus.adv.adapter.amazon.v3.model.AmzStreamSubscriptionResponse;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.enums.amz.AmazonRegionEnum;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 亚马逊广告 Profile Service 实现类
 */
@Slf4j
@Service
@Validated
public class AdsAmazonProfileServiceImpl implements AdsAmazonProfileService {

    private static final String AMZ_ADS_STREAM_SQS_QUEUE_ARN = "amz_ads_stream_sqs_queue_arn";
    private static final String[] DATA_SET_IDS = {
            "sp-traffic", "sp-conversion",
            "sb-traffic", "sb-conversion",
            "sd-traffic", "sd-conversion"
    };

    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    @Lazy
    private AdsAuthService adsAuthService;

    @Resource
    private AmazonAdsReportApiService amazonAdsReportApiService;

    @Resource
    private ConfigService configService;

    @Resource
    private AmazonAdsAdapter amazonAdsAdapter;

    @Resource
    private AmazonAdsApiClient amazonAdsApiClient;

    @Override
    public void syncProfiles(AdsAccountDO account, AdsAccountCredentialDO credential) {
        log.info("[syncProfiles][Amazon] 开始同步账号 {} ({}) 的站点 (Profiles)", account.getName(),
                account.getExternalAccountId());
        Map<String, List<String>> profilesByCountryCode = getProfileGroupByCountry(account);
        Set<AmazonRegionEnum> regions = getAccountSupportRegion(account);

        for (AmazonRegionEnum regionEnum : regions) {
            log.info("[syncProfiles][Amazon] 正在从 Region: {} 拉取 Profiles", regionEnum.name());
            List<AbstractAmazonAdsAdapter.AmzProfileVO> profiles = this.fetchProfiles(account, credential, regionEnum);
            
            if (CollectionUtils.isEmpty(profiles)) {
                continue;
            }
            for (AbstractAmazonAdsAdapter.AmzProfileVO p : profiles) {
                if (CollectionUtils.isNotEmpty(profilesByCountryCode.get(p.getCountryCode())) 
                        && profilesByCountryCode.get(p.getCountryCode()).contains(p.getProfileId())) {
                    
                    AdsAmazonProfileDO savedProfile = saveProfile(account.getId(), p.getProfileId(), p.getCountryCode(), 
                            regionEnum.name(), p.getCurrencyCode(), p.getTimezone(), 
                            account.getExternalAccountId(), account.getName());
                    
                    // 自动同步 Stream 订阅
                    this.syncStreamSubscription(savedProfile);
                }
            }
        }
    }

    private Map<String, List<String>> getProfileGroupByCountry(AdsAccountDO account) {
        AbstractAmazonAdsAdapter.AmzAccountVO amzAccount = JsonUtils.parseObject(account.getExtConfig(), AbstractAmazonAdsAdapter.AmzAccountVO.class);
        if (amzAccount == null || CollectionUtils.isEmpty(amzAccount.getAlternateIds())) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> result = new HashMap<>();
        for (Map<String, String> item : amzAccount.getAlternateIds()) {
            String countryCode = item.get("countryCode");
            if (countryCode != null) {
                if (!result.containsKey(countryCode)) {
                    result.put(countryCode, new ArrayList<>());
                }
                result.get(countryCode).add(item.get("profileId"));
            }
        }
        return result;
    }

    private Set<AmazonRegionEnum> getAccountSupportRegion(AdsAccountDO account) {
        AbstractAmazonAdsAdapter.AmzAccountVO amzAccount = JsonUtils.parseObject(account.getExtConfig(), AbstractAmazonAdsAdapter.AmzAccountVO.class);
        if (amzAccount == null || amzAccount.getCountryCodes() == null) return Collections.emptySet();
        Set<AmazonRegionEnum> result = new HashSet<>();
        amzAccount.getCountryCodes().forEach(code -> {
            result.add(AmazonRegionEnum.ofCountryCode(code));
        });
        return result.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private AdsAmazonProfileDO saveProfile(Long accountId, String profileId, String countryCode, String region,
                                           String currency, String timezone, String entityId, String entityName) {
        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        if (profile == null) {
            profile = AdsAmazonProfileDO.builder()
                    .accountId(accountId)
                    .profileId(profileId)
                    .countryCode(countryCode)
                    .region(region)
                    .currencyCode(currency)
                    .timezone(timezone)
                    .entityId(entityId)
                    .entityName(entityName)
                    .status("ENABLED")
                    .build();
            adsAmazonProfileMapper.insert(profile);
        } else {
            profile.setAccountId(accountId);
            profile.setCountryCode(countryCode);
            profile.setRegion(region);
            profile.setCurrencyCode(currency);
            profile.setTimezone(timezone);
            profile.setEntityId(entityId);
            profile.setEntityName(entityName);
            profile.setStatus("ENABLED");
            adsAmazonProfileMapper.updateById(profile);
        }
        return profile;
    }

    @Override
    public void syncStreamSubscription(AdsAmazonProfileDO profile) {
        if (profile == null || !"ENABLED".equals(profile.getStatus())) {
            return;
        }

        // 1. 获取 SQS ARN 配置
        String queueArn = Optional.ofNullable(configService.getConfigByKey(AMZ_ADS_STREAM_SQS_QUEUE_ARN))
                .map(ConfigDO::getValue)
                .orElse(null);
        if (queueArn == null) {
            log.warn("[syncStreamSubscription] 缺失 SQS ARN 配置 {}, 跳过订阅", AMZ_ADS_STREAM_SQS_QUEUE_ARN);
            return;
        }

        // 2. 获取凭证
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(profile.getAccountId());
        if (credential == null) {
            log.warn("[syncStreamSubscription] 找不到账号 {} 的凭证, 跳过订阅", profile.getAccountId());
            return;
        }

        // 3. 准备请求信息
        String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
        AdsAmazonProfileDO.Config config = profile.getConfig();
        if (config == null) {
            config = new AdsAmazonProfileDO.Config();
            config.setStreamSubscriptions(new HashMap<>());
        }
        Map<String, String> subscriptions = config.getStreamSubscriptions();

        boolean updated = false;
        for (String dataSetId : DATA_SET_IDS) {
            // 如果已经订阅过，则跳过
            if (subscriptions.containsKey(dataSetId)) {
                continue;
            }

            try {
                log.info("[syncStreamSubscription] 开始为 Profile {} 订阅数据集 {}", profile.getProfileId(), dataSetId);
                AmzStreamSubscriptionRequest request = AmzStreamSubscriptionRequest.builder()
                        .clientRequestToken(java.util.UUID.randomUUID().toString())
                        .dataSetId(dataSetId)
                        .destination(AmzStreamSubscriptionRequest.Destination.builder()
                                .sqsDestination(AmzStreamSubscriptionRequest.SqsDestination.builder()
                                        .queueArn(queueArn)
                                        .build())
                                .build())
                        .notes("Auto subscribed by HanX Erplus for " + dataSetId)
                        .build();

                AmzStreamSubscriptionResponse response = amazonAdsReportApiService.createStreamSubscription(
                        credential, profile.getEntityId(), profile.getProfileId(), baseUrl, request);

                if (response != null && response.getSubscriptionId() != null) {
                    subscriptions.put(dataSetId, response.getSubscriptionId());
                    updated = true;
                    log.info("[syncStreamSubscription] Profile {} 订阅数据集 {} 成功, subscriptionId={}",
                            profile.getProfileId(), dataSetId, response.getSubscriptionId());
                }
            } catch (Exception e) {
                log.error("[syncStreamSubscription] Profile {} 订阅数据集 {} 失败", profile.getProfileId(), dataSetId, e);
            }
        }

        // 4. 保存订阅信息
        if (updated) {
            profile.setConfig(config);
            adsAmazonProfileMapper.updateById(profile);
        }
    }

    @Override
    public void syncStreamSubscriptionByAccountId(Long accountId) {
        List<AdsAmazonProfileDO> profiles = getAmazonProfileList(accountId);
        for (AdsAmazonProfileDO profile : profiles) {
            syncStreamSubscription(profile);
        }
    }

    @Override
    public List<AdsAmazonProfileDO> getAmazonProfileList(Long accountId) {
        return adsAmazonProfileMapper.selectList(AdsAmazonProfileDO::getAccountId, accountId);
    }


    /**
     * 从平台拉取 raw profiles
     */
    @Override
    public List<AbstractAmazonAdsAdapter.AmzProfileVO> fetchProfiles(AdsAccountDO account, AdsAccountCredentialDO credential, AmazonRegionEnum region) {
        String endpoint = region.getAdsEndpoint();
        String url = "https://" + endpoint + "/v2/profiles?profileTypeFilter=seller";

        String resp = amazonAdsApiClient.get(credential, null, null, url);
        return JSONUtil.toList(resp, AbstractAmazonAdsAdapter.AmzProfileVO.class);

//        String endpoint = region.getAdsEndpoint();
//        String url = "https://" + endpoint + "/v2/profiles";
//        return executeGetRequest(credential, null, url, null,
//                resp -> JSONUtil.toList(resp, AbstractAmazonAdsAdapter.AmzProfileVO.class));
    }

}
