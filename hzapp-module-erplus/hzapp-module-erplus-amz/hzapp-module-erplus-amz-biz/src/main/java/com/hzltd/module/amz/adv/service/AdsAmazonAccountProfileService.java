package com.hzltd.module.amz.adv.service;

import com.hzltd.module.adv.model.AdsRequest;
import com.hzltd.module.adv.model.AdsResponse;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.api.client.ApiException;
import com.hzltd.module.amz.adv.api.profiles.api.ProfilesApi;
import com.hzltd.module.amz.adv.api.profiles.model.Profile;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;

import java.util.List;

@Service
public class AdsAmazonProfileNewService extends AbstractAmazonAdsService {


    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    public AdsResponse<List<Profile>> fetchProfiles(AdsRequest<AuthorizationModel> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : request.getRequest();
        ProfilesApi profilesApi = new ProfilesApi(getSpApiClient(authorizationModel));

        try {
            List<Profile> profiles = profilesApi.listProfiles(authorizationModel.getAppKey(), null, null, "seller", null);
            return AdsResponse.success(profiles);
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
}
