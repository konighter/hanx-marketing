package com.hzltd.module.amz.adv.api;

import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.erplus.adv.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.adv.model.AdsTargetModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdsKeyworkManagerApi extends AbstractAmazonAdsService {

    public AdsResponse<List<AdsTargetModel>> queryTarget(AdsRequest<AdsQueryRequest> request) {



        return AdsResponse.success(List.of());
    }



}
