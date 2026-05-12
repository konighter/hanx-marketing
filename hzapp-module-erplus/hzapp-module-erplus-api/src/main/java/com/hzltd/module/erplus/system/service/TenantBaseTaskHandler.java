package com.hzltd.module.erplus.system.service;

import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.model.ShopModel;
import jakarta.annotation.Resource;

public abstract class TenantBaseTaskHandler implements ErpTaskHandler {

    @Resource
    private SystemShopService systemShopService;

    @Override
    public ErpTaskResult onStart(ErpTaskDTO task) {
        ShopModel shop = systemShopService.getShopByIdWithoutTenant(task.getShopId());
        return TenantUtils.execute(shop.getTenantId().longValue(), () -> doStart(task, shop));
    }

    public abstract ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop);

    @Override
    public ErpTaskResult onPoll(ErpTaskDTO task) {
        ShopModel shop = systemShopService.getShopByIdWithoutTenant(task.getShopId());
        return TenantUtils.execute(shop.getTenantId().longValue(), () -> doPoll(task, shop));
    }

    public abstract ErpTaskResult doPoll(ErpTaskDTO task, ShopModel shop);


}
