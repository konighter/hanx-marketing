package com.hzltd.module.erplus.adv.metadata.service.rule;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleRelationDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsOptimizationRuleMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsOptimizationRuleRelationMapper;
import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleAssociateReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告优化规则 Service 实现类
 */
@Service
@Slf4j
public class AdsOptimizationRuleServiceImpl implements AdsOptimizationRuleService {

    @Resource
    private AdsOptimizationRuleMapper adsOptimizationRuleMapper;
    @Resource
    private AdsOptimizationRuleRelationMapper adsOptimizationRuleRelationMapper;
    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAuthService adsAuthService;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOptimizationRule(AdsOptimizationRuleSaveReqVO createReqVO) {
        // 1. 获取账户信息
        AdsAccountDO account = adsAccountMapper.selectById(createReqVO.getAccountId());
        if (account == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_004, "账户不存在"));
        }

        // 2. 调用适配器同步到平台
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(AdsPlatformEnum.valueOf(account.getPlatform()));
        String responseBody = adapter.postOptimizationRuleCreate(account, createReqVO.getProfileId(), createReqVO);

        // 3. 解析响应并保存到本地
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = JsonUtils.parseObject(responseBody, Map.class);
        if (responseMap != null && responseMap.containsKey("optimizationRules")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rules = (List<Map<String, Object>>) responseMap.get("optimizationRules");
            for (Map<String, Object> ruleMap : rules) {
                AdsOptimizationRuleDO ruleDO = AdsOptimizationRuleDO.builder()
                        .ruleId((String) ruleMap.get("ruleId"))
                        .name((String) ruleMap.get("ruleName"))
                        .category(ruleMap.get("ruleCategory") != null ? ruleMap.get("ruleCategory").toString() : null)
                        .subCategory((String) ruleMap.get("ruleSubCategory"))
                        .status((String) ruleMap.get("status"))
                        .recurrence(ruleMap.get("recurrence"))
                        .action(ruleMap.get("action"))
                        .build();

                AdsOptimizationRuleDO existing = adsOptimizationRuleMapper.selectByRuleId(ruleDO.getRuleId());
                if (existing == null) {
                    adsOptimizationRuleMapper.insert(ruleDO);
                } else {
                    ruleDO.setId(existing.getId());
                    adsOptimizationRuleMapper.updateById(ruleDO);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void associateOptimizationRules(Long campaignId, AdsOptimizationRuleAssociateReqVO associateReqVO) {
        // 1. 校验广告计划
        AdsCampaignDO campaign = adsCampaignMapper.selectById(campaignId);
        if (campaign == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_001, "广告计划不存在"));
        }

        // 2. 获取账户和站点信息
        String profileId = "";
        Map<String, Object> extData = JsonUtils.parseObject(JsonUtils.toJsonString(campaign.getExtData()), Map.class);
        if (extData != null && extData.containsKey("profileId")) {
            profileId = extData.get("profileId").toString();
        }

        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(AdsPlatformEnum.valueOf(account.getPlatform()));

        // 3. 调用平台 API 关联规则
        adapter.postOptimizationRuleAssociate(account, campaign.getExternalId(), profileId, associateReqVO);

        // 4. 保存本地关联关系
        for (String ruleExternalId : associateReqVO.getOptimizationRuleIds()) {
            AdsOptimizationRuleDO ruleDO = adsOptimizationRuleMapper.selectByRuleId(ruleExternalId);
            if (ruleDO != null) {
                AdsOptimizationRuleRelationDO relation = AdsOptimizationRuleRelationDO.builder()
                        .ruleId(ruleDO.getId())
                        .entityId(campaignId)
                        .entityType("CAMPAIGN")
                        .entityExternalId(campaign.getExternalId())
                        .build();
                adsOptimizationRuleRelationMapper.insert(relation);
            }
        }
    }

    @Override
    public List<AdsOptimizationRuleDO> getOptimizationRuleList() {
        return adsOptimizationRuleMapper.selectList();
    }
}
