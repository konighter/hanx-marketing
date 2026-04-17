package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.ListingDiagnosisDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨境产品诊断服务实现类 (当前为 Mock 实现)
 */
@Service
public class CrossProductDiagnosisServiceImpl implements CrossProductDiagnosisService {

    @Override
    public ListingDiagnosisDTO getDiagnosis(Long productId, String sellerSku) {
        ListingDiagnosisDTO diagnosis = new ListingDiagnosisDTO();
        int score = 60 + (int) (Math.random() * 40); // 60-100
        diagnosis.setScore(score);
        
        List<ListingDiagnosisDTO.Issue> issues = new ArrayList<>();
        if (score < 80) {
            issues.add(new ListingDiagnosisDTO.Issue("warning", "主图非纯白背景", "Amazon 对主图背景有严格要求。"));
        }
        if (score < 90) {
            issues.add(new ListingDiagnosisDTO.Issue("info", "缺少 A+ 页面", "添加 A+ 页面可以提升 5%-10% 的转化率。"));
        }
        diagnosis.setIssues(issues);
        return diagnosis;
    }

    @Override
    public java.util.Map<Long, ListingDiagnosisDTO> getBatchDiagnosis(java.util.Collection<Long> productIds) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(productIds)) {
            return java.util.Collections.emptyMap();
        }
        return productIds.stream().collect(java.util.stream.Collectors.toMap(
                id -> id,
                id -> getDiagnosis(id, null)
        ));
    }
}
