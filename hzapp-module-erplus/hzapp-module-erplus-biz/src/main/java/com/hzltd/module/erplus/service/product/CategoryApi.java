package com.hzltd.module.erplus.service.product;


import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Headers({"Client-Id: ","Api-Key: "})
@FeignClient(name = "CategoryApi", url = "https://api-seller.ozon.ru")
public interface CategoryApi {

    @GetMapping(value = "/v1/description-category/tree")
    public Object getCategoryTree(String language);
}
