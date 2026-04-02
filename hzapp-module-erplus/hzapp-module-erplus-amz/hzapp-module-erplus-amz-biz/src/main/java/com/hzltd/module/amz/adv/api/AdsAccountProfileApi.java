package com.hzltd.module.amz.adv.api;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.account.api.AccountApi;
import com.hzltd.module.amz.adv.client.account.model.AdsAccountWithMetaData;
import com.hzltd.module.amz.adv.client.account.model.GetAccountResponseContent;
import com.hzltd.module.amz.adv.client.account.model.ListAdsAccountsRequestContent;
import com.hzltd.module.amz.adv.client.account.model.ListAdsAccountsResponseContent;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.profiles.api.ProfilesApi;
import com.hzltd.module.amz.adv.client.profiles.model.Profile;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("null")
public class AdsAccountProfileApi extends AbstractAmazonAdsService {


    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    private AdsAccountMapper adsAccountMapper;

    public AdsResponse<List<Profile>> queryProfiles(AdsRequest<AuthorizationModel> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : request.getRequest();
        ProfilesApi profilesApi = new ProfilesApi(getApiClient(authorizationModel));

        try {
            List<Profile> profiles = profilesApi.listProfiles(authorizationModel.getAppKey(), null, null, "seller", null);
            return AdsResponse.success(profiles);
        } catch (ApiException e) {
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<Profile> queryProfile(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = getAuthorizationModel(request.getShopId());
        ProfilesApi profilesApi = new ProfilesApi(getApiClient(authorizationModel));
        try {
            Profile profile = profilesApi.getProfileById(authorizationModel.getAppKey(), Long.valueOf(request.getRequest()));
            return AdsResponse.success(profile);
        } catch (ApiException e) {
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<List<AdsAccountWithMetaData>> queryAccounts(AdsRequest<AuthorizationModel> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : request.getRequest();
        AccountApi accountApi = new AccountApi(getApiClient(authorizationModel));
        try {
            ListAdsAccountsResponseContent response = accountApi.listAdsAccounts(authorizationModel.getAppKey(), new ListAdsAccountsRequestContent());
            return AdsResponse.success(response.getAdsAccounts());
        } catch (ApiException e) {
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<AdsAccountWithMetaData> queryAccount(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = getAuthorizationModel(request.getShopId());
        AccountApi accountApi = new AccountApi(getApiClient(authorizationModel));
        try {
            GetAccountResponseContent response = accountApi.getAccount(request.getRequest(), authorizationModel.getAppKey());
            return AdsResponse.success(response.getAdsAccount());
        } catch (ApiException e) {
            return AdsResponse.error(e.getMessage());
        }
    }


    public AdsAmazonProfileDO saveOrUpdateProfile(AdsAmazonProfileDO profileDO) {
        AdsAmazonProfileDO existingProfile = adsAmazonProfileMapper.selectByProfileId(profileDO.getProfileId());
        if (existingProfile == null) {
            adsAmazonProfileMapper.insert(profileDO);
            return profileDO;
        } else {
            profileDO.setId(existingProfile.getId());
            adsAmazonProfileMapper.updateById(profileDO);
            return profileDO;
        }
    }


    public AdsAccountDO saveOrUpdateAdsAccount(AdsAccountDO accountDO) {
        AdsAccountDO account = adsAccountMapper.selectByPlatformAndExternalId(AdsPlatformEnum.AMAZON.getCode(), accountDO.getExternalAccountId());
        accountDO.setPlatform(AdsPlatformEnum.AMAZON.getCode());
        if (account == null) {
            adsAccountMapper.insert(accountDO);
            return accountDO;
        } else {
            accountDO.setId(account.getId());
            adsAccountMapper.updateById(accountDO);
            return accountDO;
        }
    }







}
