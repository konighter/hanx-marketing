package com.hzltd.module.erplus.api.adptor.amz;

import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.common.MediaModel;
import com.hzltd.module.erplus.model.product.*;
import com.hzltd.module.erplus.service.product.ProductApi;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = ProductApi.class)
public class AmazonProductService implements ProductApi {
    @Override
    public ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file) {
        return null;
    }

    @Override
    public ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request) {
        return null;
    }
}
