package com.hzltd.module.erplus.ozon.api.category;


import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@Headers({"Client-Id: ","Api-Key: "})
@FeignClient(name = "CategoryApi", url = "https://api-seller.ozon.ru")
public interface CategoryApi {

    @GetMapping(value = "/v1/description-category/tree")

    public Object getCategoryTree(String language);
}
