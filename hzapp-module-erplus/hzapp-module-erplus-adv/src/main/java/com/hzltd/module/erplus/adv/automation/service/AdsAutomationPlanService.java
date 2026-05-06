package com.hzltd.module.erplus.adv.automation.service;

import com.hzltd.framework.common.exception.ErrorCode;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationPlanPageReqVO;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationPlanResp;
import com.hzltd.module.erplus.adv.automation.controller.admin.vo.AdsAutomationPlanSaveReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationPlanMapper;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import com.hzltd.module.erplus.spapi.service.product.CrossProductService;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.SystemShopService;
import com.hzltd.module.erplus.adv.automation.domain.*;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationTemplateDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationTemplateMapper;
import com.hzltd.framework.common.util.json.JsonUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 广告自动化计划 Service 实现类
 */
@Service
@Validated
public class AdsAutomationPlanService {

    @Resource
    private AdsAutomationPlanMapper adsAutomationPlanMapper;
    @Resource
    private SystemShopService systemShopService;
    @Resource
    private CrossProductService crossProductService;
    @Resource
    private AdsAutomationTemplateMapper adsAutomationTemplateMapper;
    @Resource
    private ErpTaskEngine erpTaskEngine;

    /**
     * 创建广告自动化计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    public Long createPlan(AdsAutomationPlanSaveReqVO createReqVO) {
        List<AdsAutomationPlanDO> plans = getPlanByTemplateAndSku(createReqVO.getTemplateId(), createReqVO.getSku());
        if (CollectionUtils.isNotEmpty(plans)) {
            throw exception(new ErrorCode(0, "商品已有运行中的计划, 请检查后重试"));
        }
        // 1. 获取模版配置
        AdsAutomationTemplateDO template = adsAutomationTemplateMapper.selectById(createReqVO.getTemplateId());
        if (template == null) {
            throw exception(ErplusErrorCodeConstants.ADS_AUTOMATION_TEMPLATE_NOT_EXISTS);
        }

        // 2. 解析模版中的完整配置 (结构+规则等)
        AdsAutomationPlanConfig templateConfig = JsonUtils.parseObject(JsonUtils.toJsonString(template.getConfig()), AdsAutomationPlanConfig.class);
        if (templateConfig == null) {
            templateConfig = new AdsAutomationPlanConfig();
        }

        // 3. 将前端传来的 Map 参数直接同步到 strategy.params 中
        if (createReqVO.getContext() != null) {
            if (templateConfig.getStrategy() == null) {
                templateConfig.setStrategy(new AdsStrategyConfig());
            }
            // 直接将前端传来的 Map 合并到策略参数中
            createReqVO.getContext().forEach(templateConfig.getStrategy()::addParam);
        }

        // 3.1 预处理规则：将规则表达式中的 {key} 替换为具体的数值 (固化规则)
        if (templateConfig.getRules() != null && templateConfig.getStrategy() != null 
                && templateConfig.getStrategy().getParams() != null) {
            
            Map<String, Object> params = templateConfig.getStrategy().getParams();
            for (AdsExecutionRuleConfig rule : templateConfig.getRules()) {
                String condition = rule.getCondition();
                if (condition != null) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        String placeholder = "{" + entry.getKey() + "}";
                        if (condition.contains(placeholder)) {
                            condition = condition.replace(placeholder, String.valueOf(entry.getValue()));
                        }
                    }
                    rule.setCondition(condition);
                }
            }
        }

        // 4. 构建最终的 Context 保存
        AdsAutomationPlanContext finalContext = new AdsAutomationPlanContext();
        finalContext.setConfig(templateConfig);
        finalContext.setState(new AdsAutomationPlanState()); // 初始化空状态

        // 插入
        AdsAutomationPlanDO plan = new AdsAutomationPlanDO();
        plan.setName(createReqVO.getName());
        plan.setTemplateId(createReqVO.getTemplateId());
        plan.setShopId(createReqVO.getShopId());
        plan.setPlatform(createReqVO.getPlatform());
        plan.setSku(createReqVO.getSku());
        plan.setContext(finalContext);
        plan.setStatus(createReqVO.getStatus());
        
        adsAutomationPlanMapper.insert(plan);

        // 提交初始执行任务
        erpTaskEngine.submitTask(ErpTaskSubmitRequest.builder()
                .shopId(plan.getShopId().intValue())
                .platform(plan.getPlatform())
                .taskType("ADV_AUTO_PLAN_EXEC")
                .taskUniqueId("AUTO_PLAN_" + plan.getId() + "_INIT")
                .context(java.util.Map.of("planId", plan.getId()))
                .scheduledAt(System.currentTimeMillis())
                .build());

        // 返回
        return plan.getId();
    }

    /**
     * 更新广告自动化计划
     *
     * @param updateReqVO 更新信息
     */
    public void updatePlan(AdsAutomationPlanSaveReqVO updateReqVO) {
        // 校验存在
        validatePlanExists(updateReqVO.getId());
        // 更新 (更新时暂不支持部分合并 context，前端应传完整 map)
        AdsAutomationPlanDO plan = adsAutomationPlanMapper.selectById(updateReqVO.getId());
        if (plan == null) {
            throw exception(ErplusErrorCodeConstants.ADS_AUTOMATION_PLAN_NOT_EXISTS);
        }
        
        // 更新
        AdsAutomationPlanDO updateObj = new AdsAutomationPlanDO();
        updateObj.setId(updateReqVO.getId());
        if (updateReqVO.getName() != null) {
            updateObj.setName(updateReqVO.getName());
        }

        // plan状态的变更, 需要联动任务引擎。
        if (updateReqVO.getStatus() != null && !updateReqVO.getStatus().equals(plan.getStatus())) {
            updateObj.setStatus(updateReqVO.getStatus());
            String taskUniqueId = "AUTO_PLAN_" + plan.getId() + "_INIT";
            String taskType = "ADV_AUTO_PLAN_EXEC";
            
            if ("PAUSED".equals(updateReqVO.getStatus())) {
                erpTaskEngine.pauseTask(taskUniqueId, taskType);
            } else if ("RUNNING".equals(updateReqVO.getStatus())) {
                erpTaskEngine.resumeTask(taskUniqueId, taskType);
            }
        }
        
        adsAutomationPlanMapper.updateById(updateObj);
    }

    public List<AdsAutomationPlanDO> getPlanByTemplateAndSku(Long tempId, String sku) {
        return adsAutomationPlanMapper.selectList(new LambdaQueryWrapperX<AdsAutomationPlanDO>()
                .eqIfPresent(AdsAutomationPlanDO::getSku, sku)
                .eqIfPresent(AdsAutomationPlanDO::getTemplateId, tempId)
                .eq(AdsAutomationPlanDO::getStatus, "RUNNING"));
    }


    /**
     * 删除广告自动化计划
     *
     * @param id 编号
     */
    public void deletePlan(Long id) {
        // 校验存在
        validatePlanExists(id);
        // 删除
        adsAutomationPlanMapper.deleteById(id);
    }

    private void validatePlanExists(Long id) {
        if (adsAutomationPlanMapper.selectById(id) == null) {
            throw exception(ErplusErrorCodeConstants.ADS_AUTOMATION_PLAN_NOT_EXISTS);
        }
    }

    /**
     * 获得广告自动化计划
     *
     * @param id 编号
     * @return 广告自动化计划
     */
    public AdsAutomationPlanResp getPlan(Long id) {
        AdsAutomationPlanDO plan = adsAutomationPlanMapper.selectById(id);
        return BeanUtils.toBean(plan, AdsAutomationPlanResp.class, this::additionalInfo);
    }

    /**
     * 获得广告自动化计划分页
     *
     * @param pageReqVO 分页查询
     * @return 广告自动化计划分页
     */
    public PageResult<AdsAutomationPlanResp> getPlanPage(AdsAutomationPlanPageReqVO pageReqVO) {
        PageResult<AdsAutomationPlanDO> pageResult = adsAutomationPlanMapper.selectPage(pageReqVO);

        return BeanUtils.toBean(pageResult, AdsAutomationPlanResp.class, this::additionalInfo);
    }

    private AdsAutomationPlanResp additionalInfo(AdsAutomationPlanResp plan) {
        List<ProductModel> productModels = crossProductService.getProductModel(plan.getShopId(), List.of(plan.getSku()));
        if (CollectionUtils.isNotEmpty(productModels)) {
            plan.setProductImage(productModels.get(0).getMainImage().getUrl());
            plan.setProductTitle(productModels.get(0).getProductName());
        }
        ShopModel shop = systemShopService.getShopById(plan.getShopId());
        plan.setShopName(shop.getName());
        return plan;
    }

    /**
     * 获得广告自动化计划列表, 用于 Job 执行
     * 
     * @param status 状态
     * @return 计划列表
     */
    public List<AdsAutomationPlanDO> getPlanListByStatus(String status) {
        return adsAutomationPlanMapper.selectList(AdsAutomationPlanDO::getStatus, status);
    }

    /**
     * 获得广告自动化计划 DO
     *
     * @param id 编号
     * @return 广告自动化计划
     */
    public AdsAutomationPlanDO getPlanDO(Long id) {
        return adsAutomationPlanMapper.selectById(id);
    }

    @TenantIgnore
    public AdsAutomationPlanDO getPlanDOWithoutTenant(Long id) {
        return getPlanDO(id);
    }


}
