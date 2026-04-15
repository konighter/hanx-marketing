package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.sellers.v1.SellersApi;
import software.amazon.spapi.models.sellers.v1.Account;
import software.amazon.spapi.models.sellers.v1.GetAccountResponse;
import software.amazon.spapi.models.sellers.v1.GetMarketplaceParticipationsResponse;
import software.amazon.spapi.models.sellers.v1.MarketplaceParticipationList;

@Service
public class AmazonAccountService extends AbsAmzPlatformApiService {


    public ApiResponse<Account> getAccount(ApiRequest<?> request) {
        SellersApi sellersApi = this.getSellersApi(request);

        try {
            GetAccountResponse resp = sellersApi.getAccount();
            return ApiResponse.success(resp.getPayload());
        } catch (ApiException|LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<MarketplaceParticipationList> getMarketplaceParticipations(ApiRequest<?> request) {
        try {
            SellersApi sellersApi = this.getSellersApi(request);
            GetMarketplaceParticipationsResponse resp = sellersApi.getMarketplaceParticipations();
            return ApiResponse.success(resp.getPayload());
        } catch (ApiException|LWAException e) {
            throw new RuntimeException(e);
        }
    }





}
