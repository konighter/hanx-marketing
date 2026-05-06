package com.hzltd.module.erplus.adv.automation.service;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.automation.domain.AdsAutomationPlanConfig;
import com.hzltd.module.erplus.adv.automation.domain.AdsAutomationPlanContext;
import com.hzltd.module.erplus.adv.automation.domain.AdsStructureConfig;
import com.hzltd.module.erplus.adv.automation.engine.AdsActionDispatcher;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanResourceDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationPlanResourceMapper;
import com.hzltd.module.erplus.adv.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 广告自动化任务流执行服务实现
 */
@Slf4j
@Service
public class AdsAutomationPlanTaskFlowServiceImpl implements AdsAutomationPlanTaskFlowService {

    @Resource
    private AdsAutomationPlanService adsAutomationPlanService;

    @Resource
    private AdsAutomationPlanResourceMapper resourceMapper;

    @Resource
    private AdsActionDispatcher actionDispatcher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initAdStructure(Long planId) {
        AdsAutomationPlanDO plan = adsAutomationPlanService.getPlanDO(planId);
        if (plan == null) return;

        AdsAutomationPlanContext context = plan.getContext();
        if (context == null || context.getConfig() == null) {
            log.error("Plan {} missing config", planId);
            return;
        }

        AdsAutomationPlanConfig config = context.getConfig();
        AdsStructureConfig structureConfig = config.getStructure();
        List<AdsStructureConfig.AdsCampaignRoleConfig> roles = structureConfig.getRoles();

        if (roles == null || roles.isEmpty()) {
            log.warn("Plan {} has no roles defined in configuration", planId);
            return;
        }

        log.info("[initAdStructure] 正在初始化广告结构, planId: {}, 角色数量: {}", planId, roles.size());

        for (AdsStructureConfig.AdsCampaignRoleConfig roleConfig : roles) {
            ensureRoleCampaignExists(plan, roleConfig, structureConfig);
        }
    }

    private void ensureRoleCampaignExists(AdsAutomationPlanDO plan,
                                         AdsStructureConfig.AdsCampaignRoleConfig roleConfig,
                                         AdsStructureConfig structureConfig) {
        // 1. 检查是否已经存在该角色的资源关联
        AdsAutomationPlanResourceDO resource = resourceMapper.selectOne(
                new LambdaQueryWrapperX<AdsAutomationPlanResourceDO>()
                        .eq(AdsAutomationPlanResourceDO::getPlanId, plan.getId())
                        .eq(AdsAutomationPlanResourceDO::getResourceRole, roleConfig.getRole())
                        .eq(AdsAutomationPlanResourceDO::getShopId, plan.getShopId())
        );

        if (resource != null) {
            log.info("Plan {} role {} already exists: {}", plan.getId(), roleConfig.getRole(), resource.getPlatformCampaignId());
            return;
        }

        // 2. 构造创建请求
        String template = roleConfig.getNameTemplate() != null ? roleConfig.getNameTemplate() : structureConfig.getCampaignNamingTemplate();
        String campaignName = template
                .replace("{sku}", plan.getSku() != null ? plan.getSku() : "AUTO")
                .replace("{campaignType}", "SP")
                .replace("{matchType}", roleConfig.getMatchType() != null ? roleConfig.getMatchType() : "NONE");

        AdsCampaignCreateRequest createReq = new AdsCampaignCreateRequest();
        createReq.setName(campaignName);
        createReq.setCampaignType(roleConfig.getCampaignType());
        createReq.setTargetingType(roleConfig.getTargetingType());
        createReq.setStatus("enabled");
        createReq.setDailyBudget(roleConfig.getInitialBudget() != null ? roleConfig.getInitialBudget() : new BigDecimal("10.0"));

        // 注入平台特有参数 (角色级别覆盖全局级别)
        Map<String, Object> globalPlatformConfig = structureConfig.getPlatformConfig();
        if (globalPlatformConfig != null) {
            globalPlatformConfig.forEach(createReq::add);
        }
        Map<String, Object> rolePlatformConfig = roleConfig.getPlatformConfig();
        if (rolePlatformConfig != null) {
            rolePlatformConfig.forEach(createReq::add);
        }

        // 处理出价策略等关键字段 (方案 A 透传)
        String biddingStrategy = null;
        if (rolePlatformConfig != null && rolePlatformConfig.containsKey("biddingStrategy")) {
            biddingStrategy = (String) rolePlatformConfig.get("biddingStrategy");
        } else if (globalPlatformConfig != null && globalPlatformConfig.containsKey("biddingStrategy")) {
            biddingStrategy = (String) globalPlatformConfig.get("biddingStrategy");
        }
        if (biddingStrategy != null) {
            createReq.setBiddingStrategy(biddingStrategy);
        }

        // 3. 调用分发器执行创建
        log.info("[initAdStructure] 正在创建角色 {} 的广告活动: {}", roleConfig.getRole(), campaignName);
        String campaignExternalId = actionDispatcher.createCampaign(plan.getId(), plan.getPlatform(), plan.getShopId(), createReq);

        // 4. 保存广告活动资源映射
        AdsAutomationPlanResourceDO campaignResource = new AdsAutomationPlanResourceDO();
        campaignResource.setPlanId(plan.getId());
        campaignResource.setPlatformCampaignId(campaignExternalId);
        campaignResource.setResourceRole(roleConfig.getRole());
        campaignResource.setShopId(plan.getShopId());
        resourceMapper.insert(campaignResource);

        // 5. 创建默认广告组
        String adGroupName = structureConfig.getAdGroupNamingTemplate()
                .replace("{asin}", plan.getSku() != null ? plan.getSku() : "AUTO")
                .replace("{productName}", structureConfig.getProductName() != null ? structureConfig.getProductName() : "AUTO")
                .replace("{campaignType}", "SP")
                .replace("{matchType}", roleConfig.getMatchType() != null ? roleConfig.getMatchType() : "NONE");

        AdsAdGroupCreateRequest adGroupReq = new AdsAdGroupCreateRequest();
        adGroupReq.setCampaignId(campaignExternalId);
        adGroupReq.setName(adGroupName);
        adGroupReq.setStatus("enabled");
        adGroupReq.setDefaultBid(1.0); // 默认初始出价

        String adGroupExternalId = actionDispatcher.createAdGroup(plan.getId(), plan.getPlatform(), plan.getShopId(), adGroupReq);

        // 6. 创建默认广告
        AdsAdCreateRequest adReq = new AdsAdCreateRequest();
        adReq.setCampaignId(campaignExternalId);
        adReq.setAdGroupId(adGroupExternalId);
        adReq.setStatus("enabled");
        // 如果是亚马逊 SP，通常需要 ASIN。可以通过 plan.getSku() 获取。
        if ("AMAZON".equalsIgnoreCase(plan.getPlatform())) {
            adReq.add("asin", plan.getSku());
            adReq.add("sku", plan.getSku()); // 有时也需要本地 SKU
        }

        actionDispatcher.createAds(plan.getId(), plan.getPlatform(), plan.getShopId(), List.of(adReq));

        log.info("Successfully initialized full structure for role {} for plan {} with campaignId {}", 
            roleConfig.getRole(), plan.getId(), campaignExternalId);
    }

    @Override
    public void executeAutomationCycle(Long planId) {
        AdsAutomationPlanDO plan = adsAutomationPlanService.getPlanDO(planId);
        if (plan == null) return;

        AdsAutomationPlanContext context = plan.getContext();
        
        // 1. 获取所有关联的 SOURCE 活动
        List<AdsAutomationPlanResourceDO> sources = resourceMapper.selectList(
                new LambdaQueryWrapperX<AdsAutomationPlanResourceDO>()
                        .eq(AdsAutomationPlanResourceDO::getPlanId, planId)
                        .eq(AdsAutomationPlanResourceDO::getResourceRole, "SOURCE")
        );

        if (sources.isEmpty()) {
            log.warn("Plan {} has no source campaigns, nothing to mine", planId);
            return;
        }

        log.info("Executing cycle for plan {} across {} source campaigns", planId, sources.size());

        // 2. 遍历 Source 活动，拉取报表数据并执行决策
        for (AdsAutomationPlanResourceDO source : sources) {
            processSourceCampaign(plan, context, source);
        }
    }

    private void processSourceCampaign(AdsAutomationPlanDO plan, AdsAutomationPlanContext context, AdsAutomationPlanResourceDO source) {
        // TODO: 
        // 1. 调用 Amazon API 拉取该活动的 Search Term Report (近7-14天)
        // 2. 遍历 Report 中的词:
        //    - 命中 WIN 规则 -> 执行关键词转移 (Promote)
        //    - 命中 TEST 规则 -> 调低出价
        //    - 命中 KILL 规则 -> 否定/暂停
    }
}
