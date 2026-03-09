package com.hzltd.module.erplus.adv.adapter.amazon.service;

import com.hzltd.module.erplus.adv.adapter.amazon.v1.AmzStreamSubscriptionService;

import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.amazon.AbstractAmazonAdsAdapter;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.AdsApiClient;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.enums.amz.AmazonRegionEnum;
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

    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    @Lazy
    private AdsAuthService adsAuthService;

    @Resource
    private AmzStreamSubscriptionService amzStreamSubscriptionService;

    @Resource
    private AdsApiClient adsApiClient;

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
                    amzStreamSubscriptionService.createStreamSubscription(savedProfile);
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

        String resp = adsApiClient.get(credential, null, null, url);
        return JSONUtil.toList(resp, AbstractAmazonAdsAdapter.AmzProfileVO.class);
    }

}
