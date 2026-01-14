package com.hzltd.module.erplus.service.crossproduct;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.service.cross.ErplusCrossPriceInventoryService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

public class CrossProductPricingServiceTest extends BaseDbUnitTest {

    @Resource
    private ErplusCrossPriceInventoryService crossProductPricingService;

    @Test
    public void test_getCrossPlatformProductPrice() {
        crossProductPricingService.getCrossProductOffer(1L);
    }

}
