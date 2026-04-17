package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.ListingDiagnosisDTO;

/**
 * 跨境产品诊断服务接口
 * 提供商品素材健康诊断等数据
 */
public interface CrossProductDiagnosisService {

    /**
     * 获取商品诊断信息
     * @param productId 跨境商品ID
     * @param sellerSku 卖家SKU
     * @return 诊断数据
     */
    ListingDiagnosisDTO getDiagnosis(Long productId, String sellerSku);

    /**
     * 批量获取商品诊断信息
     * @param productIds 跨境商品ID列表
     * @return 诊断数据映射 (productId -> Diagnosis)
     */
    java.util.Map<Long, ListingDiagnosisDTO> getBatchDiagnosis(java.util.Collection<Long> productIds);
}
