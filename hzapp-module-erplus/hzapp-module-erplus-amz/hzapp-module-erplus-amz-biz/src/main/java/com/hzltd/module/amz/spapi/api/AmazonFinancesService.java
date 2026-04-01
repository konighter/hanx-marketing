package com.hzltd.module.amz.spapi.api;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.order.FeeModel;
import com.hzltd.module.spapi.model.order.OrderFeeRequest;
import com.hzltd.module.spapi.model.order.ProductFeeEstimateRequest;
import com.hzltd.module.spapi.service.order.FinancesApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.productfees.v0.FeesApi;
import software.amazon.spapi.models.productfees.v0.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 费用相关服务
 */
@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = FinancesApi.class)
public class AmazonFinancesService extends AbsAmzPlatformApiService implements FinancesApi {

    @Override
    public ApiResponse<List<FeeModel>> getProductEstimatedFee(ApiRequest<List<ProductFeeEstimateRequest>> request) {
        FeesApi feesApi = getFeesApi(request);
        try {
            GetMyFeesEstimatesResponse response = feesApi.getMyFeesEstimates(request.getRequest().stream().map(reqItem -> {
                        FeesEstimateByIdRequest feesEstReqById = new FeesEstimateByIdRequest();
                        String identifier;
                        if (StringUtils.isNotEmpty(reqItem.getPlatformProductCode())) {
                            feesEstReqById.idType(IdType.ASIN).idValue(reqItem.getPlatformProductCode());
                            identifier = "ASIN_" + reqItem.getPlatformProductCode();
                        } else if (StringUtils.isNotEmpty(reqItem.getSellerSku())) {
                            feesEstReqById.idType(IdType.SELLERSKU).idValue(reqItem.getSellerSku());
                            identifier = "SELLERSKU_" + reqItem.getSellerSku();
                        } else {
                            throw new IllegalArgumentException("PlatformProductCode or SellerSku is required");
                        }

                        feesEstReqById.feesEstimateRequest(new FeesEstimateRequest()
                                .marketplaceId(request.getMarketId())
                                .isAmazonFulfilled(FulfillTypeEnum.FBA.equals(reqItem.getFulfillType()))
                                .identifier(identifier)
                                .priceToEstimateFees(new PriceToEstimateFees()
                                        .listingPrice(new MoneyType()
                                                .currencyCode(reqItem.getCurrency())
                                                .amount(BigDecimal.valueOf(reqItem.getPrice()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                                        )
                                )
                        );
                        return feesEstReqById;
                    }
            ).collect(Collectors.toList()));

           List<FeeModel> feeModels = response.stream().map(feesEstResp -> {

                        FeeModel feeModel = new FeeModel();
                        if (feesEstResp.getFeesEstimateIdentifier() != null) {
                            feeModel.setMarketId(feesEstResp.getFeesEstimateIdentifier().getMarketplaceId());
                            if (IdType.ASIN.equals(feesEstResp.getFeesEstimateIdentifier().getIdType())) {
                                feeModel.setPlatformProductCode(feesEstResp.getFeesEstimateIdentifier().getIdValue());
                            } else {
                                feeModel.setSellerSku(feesEstResp.getFeesEstimateIdentifier().getIdValue());
                            }
                            feeModel.setIdentifier(feesEstResp.getFeesEstimateIdentifier().getSellerInputIdentifier());
                        }

                        if (feesEstResp.getFeesEstimate() != null) {
                            FeesEstimate feesEstimate = feesEstResp.getFeesEstimate();

                            feeModel.setTotalFee(feesEstimate.getTotalFeesEstimate().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
                            feeModel.setCurrency(feesEstimate.getTotalFeesEstimate().getCurrencyCode());

                            feeModel.setFeeItems(feesEstimate.getFeeDetailList().stream().map(detail ->
                                 new FeeModel.FeeItem()
                                         .setFeeType(detail.getFeeType())
                                        .setCurrency(detail.getFeeAmount().getCurrencyCode())
                                        .setFeeAmount(detail.getFeeAmount() != null ? detail.getFeeAmount().getAmount().multiply(BigDecimal.valueOf(100)).intValue() : null)
                                        .setFeePromotion(detail.getFeePromotion() != null ? detail.getFeePromotion().getAmount().multiply(BigDecimal.valueOf(100)).intValue() : null)
                                        .setTaxAmount(detail.getTaxAmount() != null ? detail.getTaxAmount().getAmount().multiply(BigDecimal.valueOf(100)).intValue() : null)
                                        .setFinalFee(detail.getFinalFee() != null ? detail.getFinalFee().getAmount().multiply(BigDecimal.valueOf(100)).intValue() : null)
                                ).collect(Collectors.toList()));
                        }
                        return feeModel;
                    }
            ).collect(Collectors.toList());


            return ApiResponse.success(feeModels);
        } catch (ApiException | LWAException e) {
            log.error("Get My Fees Estimates failed, request: {}, error: {}", request, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse<?> getOrderFee(ApiRequest<List<OrderFeeRequest>> request) {
        // The defaultApi variable was unused and has been removed.
        // The commented-out line referring to defaultApi has also been removed.

        return null;
    }
}
