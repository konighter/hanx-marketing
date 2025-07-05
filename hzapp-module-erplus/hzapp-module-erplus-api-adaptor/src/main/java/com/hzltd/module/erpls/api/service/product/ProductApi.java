package com.hzltd.module.erpls.api.service.product;

import com.hzltd.module.erpls.api.model.ApiRequest;
import com.hzltd.module.erpls.api.model.ApiResponse;
import com.hzltd.module.erpls.api.model.common.MediaModel;
import com.hzltd.module.erpls.api.model.product.CreateProductRequest;
import com.hzltd.module.erpls.api.model.product.CreateProductResponse;

public interface ProductApi {

    /**
     * 上传文件, 包括图片、视频、认证文件等
     * @param file
     * @return
     */
    ApiResponse<MediaModel> uploadFile(ApiRequest<MediaModel> file);


    ApiResponse<CreateProductResponse> createProduct(ApiRequest<CreateProductRequest> request);


}
