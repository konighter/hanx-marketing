package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.ListingDiagnosisDTO;
import com.hzltd.module.erplus.controller.admin.cross.vo.ListingPerformanceDTO;
import com.hzltd.module.erplus.controller.admin.cross.vo.ListingVariantDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 跨境产品表现服务实现类 (当前为 Mock 实现)
 */
@Service
public class CrossProductPerformanceServiceImpl implements CrossProductPerformanceService {

    @Override
    public ListingPerformanceDTO getPerformance(Long productId, String sellerSku) {
        ListingPerformanceDTO performance = new ListingPerformanceDTO();
        int s7 = (int) (Math.random() * 50);
        performance.setSales7d(s7);
        performance.setSales30d(s7 * 4 + (int) (Math.random() * 20));
        performance.setGmv30d(performance.getSales30d() * 1999L);
        
        List<BigDecimal> curve = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            curve.add(new BigDecimal(Math.random() * 10).setScale(2, RoundingMode.HALF_UP));
        }
        performance.setRevenueCurve(curve);
        return performance;
    }

    @Override
    public java.util.Map<Long, ListingPerformanceDTO> getBatchPerformance(java.util.Collection<Long> productIds) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(productIds)) {
            return java.util.Collections.emptyMap();
        }
        return productIds.stream().collect(java.util.stream.Collectors.toMap(
                id -> id,
                id -> getPerformance(id, null)
        ));
    }
}
