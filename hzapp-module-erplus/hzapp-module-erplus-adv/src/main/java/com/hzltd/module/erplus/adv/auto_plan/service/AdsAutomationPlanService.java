package com.hzltd.module.erplus.adv.auto_plan.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanPageReqVO;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanResp;
import com.hzltd.module.erplus.adv.auto_plan.controller.admin.vo.AdsAutomationPlanSaveReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.automation.AdsAutomationPlanDO;
import com.hzltd.module.erplus.adv.dal.mysql.automation.AdsAutomationPlanMapper;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import com.hzltd.module.erplus.spapi.service.product.CrossProductService;
import com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
    private ErpTaskEngine erpTaskEngine;

    /**
     * 创建广告自动化计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    public Long createPlan(AdsAutomationPlanSaveReqVO createReqVO) {
        // 插入
        AdsAutomationPlanDO plan = new AdsAutomationPlanDO();
        plan.setName(createReqVO.getName());
        plan.setTemplateId(createReqVO.getTemplateId());
        plan.setShopId(createReqVO.getShopId());
        plan.setPlatform(createReqVO.getPlatform());
        plan.setContext(createReqVO.getContext());
        plan.setStatus(createReqVO.getStatus());
        
        adsAutomationPlanMapper.insert(plan);

        // 提交初始执行任务
        erpTaskEngine.submitTask(com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest.builder()
                .shopId(plan.getShopId())
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
        // 更新
        AdsAutomationPlanDO updateObj = new AdsAutomationPlanDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setName(updateReqVO.getName());
        updateObj.setContext(updateReqVO.getContext());
        updateObj.setStatus(updateReqVO.getStatus());
        
        adsAutomationPlanMapper.updateById(updateObj);
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

}
