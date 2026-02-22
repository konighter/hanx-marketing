package com.hzltd.module.erplus.adv.adapter;

import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告平台适配器工厂
 */
@Component
public class AdsPlatformAdapterFactory {

    private final Map<AdsPlatformEnum, AdsPlatformAdapter> adapters = new ConcurrentHashMap<>();

    @Resource
    public void init(List<AdsPlatformAdapter> adapterList) {
        adapterList.forEach(adapter -> adapters.put(adapter.getPlatform(), adapter));
    }

    public AdsPlatformAdapter getAdapter(String platformCode) {
        AdsPlatformEnum platform = AdsPlatformEnum.getByCode(platformCode);
        if (platform == null) {
            throw new IllegalArgumentException("Unknown platform: " + platformCode);
        }
        return getAdapter(platform);
    }

    public AdsPlatformAdapter getAdapter(AdsPlatformEnum platform) {
        AdsPlatformAdapter adapter = adapters.get(platform);
        if (adapter == null) {
            throw new UnsupportedOperationException("Platform not supported yet: " + platform.getName());
        }
        return adapter;
    }
}
