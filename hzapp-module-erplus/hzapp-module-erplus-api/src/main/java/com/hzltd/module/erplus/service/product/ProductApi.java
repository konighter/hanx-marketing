package com.hzltd.module.erplus.service.product;

import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.common.MediaModel;
import com.hzltd.module.erplus.model.product.CreateProductRequest;
import com.hzltd.module.erplus.model.product.CreateProductResponse;

public interface ProductApi {

    /**
     * 上传文件, 包括图片、视频、认证文件等
     * @param file
     * @return
     */
    ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file);


    ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request);


}
