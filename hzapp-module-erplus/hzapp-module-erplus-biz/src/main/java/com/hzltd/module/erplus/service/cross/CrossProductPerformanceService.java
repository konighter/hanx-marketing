package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.ListingDiagnosisDTO;
import com.hzltd.module.erplus.controller.admin.cross.vo.ListingPerformanceDTO;
import com.hzltd.module.erplus.controller.admin.cross.vo.ListingVariantDTO;

import java.util.List;

/**
 * 跨境产品表现服务接口
 * 提供商品健康诊断、销售表现、变体预览等数据
 */
public interface CrossProductPerformanceService {

    /**
     * 获取商品销售表现
     * @param productId 跨境商品ID
     * @param sellerSku 卖家SKU
     * @return 销售表现数据
     */
    ListingPerformanceDTO getPerformance(Long productId, String sellerSku);

    /**
     * 批量获取商品销售表现
     * @param productIds 跨境商品ID列表
     * @return 销售表现数据映射 (productId -> Performance)
     */
    java.util.Map<Long, ListingPerformanceDTO> getBatchPerformance(java.util.Collection<Long> productIds);
}
