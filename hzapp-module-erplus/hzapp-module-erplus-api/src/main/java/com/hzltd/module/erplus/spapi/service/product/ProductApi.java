package com.hzltd.module.erplus.spapi.service.product;

import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.common.FeeModel;
import com.hzltd.module.erplus.spapi.model.common.MediaModel;
import com.hzltd.module.erplus.spapi.model.product.*;

import java.util.List;

public interface ProductApi {

    /**
     * 上传文件, 包括图片、视频、认证文件等
     * @param file
     * @return
     */
    ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file);


    ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request);

    ApiResponse<List<MultiMarketProductModel>> searchProduct(ApiRequest<SearchProductRequest> request);


    ApiResponse<MultiMarketProductModel> getProduct(ApiRequest<GetProductRequest> request);

    default ApiResponse<FeeModel> getProductFee(ApiRequest<ProductFeeRequest> request) {
        return ApiResponse.error("not support");
    }
}
