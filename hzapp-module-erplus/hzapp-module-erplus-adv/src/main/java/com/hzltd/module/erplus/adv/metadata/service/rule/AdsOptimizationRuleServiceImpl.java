package com.hzltd.module.erplus.adv.metadata.service.rule;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsOptimizationRuleMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsOptimizationRuleRelationMapper;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告优化规则 Service 实现类
 */
@Service
@Slf4j
@SuppressWarnings("deprecation")
public class AdsOptimizationRuleServiceImpl implements AdsOptimizationRuleService {

    @Resource
    private AdsOptimizationRuleMapper adsOptimizationRuleMapper;
    @Resource
    private AdsOptimizationRuleRelationMapper adsOptimizationRuleRelationMapper;
    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOptimizationRule(AdsOptimizationRuleSaveReqVO createReqVO) {
        // 1. 获取账户信息
        AdsAccountDO account = adsAccountMapper.selectById(createReqVO.getAccountId());
        if (account == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_004, "账户不存在"));
        }

        // 2. 调用适配器同步到平台 (MOCK)
        // AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(AdsPlatformEnum.valueOf(account.getPlatform()));
        // String responseBody = adapter.postOptimizationRuleCreate(account, createReqVO.getProfileId(), createReqVO);
        
        // Mock success response
        String ruleId = "mock-rule-id-" + System.currentTimeMillis();

        // 3. 保存到本地数据库
        AdsOptimizationRuleSaveReqVO.OptimizationRule ruleVO = createReqVO.getOptimizationRule();
        if (ruleVO != null) {
            AdsOptimizationRuleDO ruleDO = new AdsOptimizationRuleDO();
            ruleDO.setShopId(account.getShopId());
            ruleDO.setAccountId(account.getId());
            ruleDO.setRuleId(ruleId); // 使用 mocked ruleId
            ruleDO.setName(ruleVO.getRuleName());
            ruleDO.setCategory(ruleVO.getRuleCategory());
            ruleDO.setSubCategory(ruleVO.getRuleSubCategory());
            ruleDO.setStatus(ruleVO.getStatus());
            ruleDO.setRecurrence(JsonUtils.toJsonString(ruleVO.getRecurrence()));
            ruleDO.setAction(JsonUtils.toJsonString(ruleVO.getAction()));
            adsOptimizationRuleMapper.insert(ruleDO);
        }
        
        return ruleId;
    }


    @Override
    public List<AdsOptimizationRuleDO> getOptimizationRuleList(Long shopId) {
        return adsOptimizationRuleMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsOptimizationRuleDO>()
                .eq(shopId != null, AdsOptimizationRuleDO::getShopId, shopId)
        );
    }
}
