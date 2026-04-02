package com.hzltd.module.erplus.api.adapter;

import com.hzltd.module.system.model.ShopModel;
import com.hzltd.module.system.service.SystemShopService;
import jakarta.annotation.Resource;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class AbsPlatformService extends LocalAuthProvider {
    @Resource
    private SystemShopService systemShopService;


    public String getShopRegion(String shopId) {
        ShopModel shopModel = systemShopService.getShopByExtraId(shopId);
        return shopModel.getRegion();
    }

    public OffsetDateTime convert(java.time.OffsetDateTime javaTime) {
        return OffsetDateTime.of(javaTime.getYear(), javaTime.getMonth().getValue(), javaTime.getDayOfMonth(), 0, 0, 0, 0, ZoneOffset.UTC);
    }

    public String formatISO8601(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String formatISO8601(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.format(org.threeten.bp.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
