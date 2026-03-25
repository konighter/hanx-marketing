package com.hzltd.module.amz.api.spapi.model;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmzPurchasableOfferModel extends HashMap<String, Object> {
    public static final String ATTR_PATH = "/attributes/purchasable_offer";

    public AmzPurchasableOfferModel addPrice(Double price) {
        this.put("our_price", List.of(Map.of("schedule", List.of(Map.of("value_with_tax", price)))));
        return this;
    }

    public Double getPrice() {
        return (Double) ((Map) ((List) ((Map) ((List) this.get("our_price")).get(0)).get("schedule")).get(0)).get("value_with_tax");
    }


    public AmzPurchasableOfferModel setCurrency(String currency) {
        this.put("currency", currency);
        return this;
    }

    public String getCurrency() {
        return (String) this.get("currency");
    }

    public AmzPurchasableOfferModel setStartAt(LocalDateTime startAt) {
        if (startAt == null) {
            return this;
        }
        this.put("start_at", Map.of("value", DateUtil.format(startAt, DatePattern.UTC_PATTERN)));
        return this;
    }

    public AmzPurchasableOfferModel setStartAt(String startAt) {
        if (startAt == null) {
            return this;
        }
        this.put("start_at", Map.of("value", startAt));
        return this;
    }



    //todo -- 生效开始时间， 验证时间格式
    public String getStartAt() {
        if (this.get("start_at") == null) {
            return null;
        }
        return (String) ((Map) this.get("start_at")).get("value");
    }

    public AmzPurchasableOfferModel setEndAt(LocalDateTime endAt) {
        if (endAt == null) {
            return this;
        }
        this.put("end_at", Map.of("value", DateUtil.format(endAt, DatePattern.UTC_PATTERN)));
        return this;
    }

    public AmzPurchasableOfferModel setEndAt(String endAt) {
        if (endAt == null) {
            return this;
        }
        this.put("end_at", Map.of("value", endAt));
        return this;
    }



    //todo -- 生效结束时间， 验证时间格式
    public String getEndAt() {
        return (String) ((Map) this.get("end_at")).get("value");
    }

    public AmzPurchasableOfferModel setAudience(String audience) {
        if (audience == null) {
            audience = "ALL";
        }
        this.put("audience", audience);
        return this;
    }

    public AmzPurchasableOfferModel setMarketplaceId(String marketplaceId) {
        this.put("marketplace_id", marketplaceId);
        return this;
    }
    public String getMarketplaceId() {
        return (String) this.get("marketplace_id");
    }
}
