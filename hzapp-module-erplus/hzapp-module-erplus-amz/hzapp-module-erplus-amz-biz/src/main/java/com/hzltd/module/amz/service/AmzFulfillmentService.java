package com.hzltd.module.amz.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.spapi.AmzCancelInboundPlanRequest;
import com.hzltd.module.amz.controller.admin.vo.*;
import com.hzltd.module.amz.dal.dataobject.AmzInboundPlanDO;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Box;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.InboundPlan;

import java.util.List;

public interface AmzFulfillmentService {

    /**
     * 创建入仓计划
     * @param request
     * @return
     */
    String createInboundPlan(AmzInboundPlanCreateRequest request);

    /**
     * 取消入仓计划
     * @param request
     * @return
     */
    String cancelInboundPlan(AmzCancelInboundPlanRequest request);

    /**
     * 列出所有入仓计划
     * @return 入仓计划列表
     */
    List<InboundPlan> listInboundPlans(AmzListInboundPlansRequest request);

    /**
     * 分页列出所有入仓计划
     * @return 入仓计划列表
     */
    PageResult<AmzInboundPlanDO> listInboundPlansPage(AmzListInboundPlansRequest request);

    //========= 打包信息 start ===========

    /**
     * 生成打包选项
     * @param request
     * @return packing options
     */
    List<AmzPackingOption> generatePackingOptions(AmzPackingOptionRequest request);

    /**
     * 列出所有打包选项
     * @param request
     * @return packing options
     */
    List<AmzPackingOption> listPackingOptions(AmzPackingOptionRequest request);


    /**
     * 确认打包选项
     * @param request
     * @return
     */
    String confirmPackingOption(AmzConfirmPackingOptionRequest request);

    /**
     * 设置打包信息
     * @param request
     * @return
     */
    String setPackingInfo(AmzSetPackingInfoRequest request);

     /**
     * 获取打包信息
     * @param request
     * @return
     */
    AmzPackingOption getPackingInfo(AmzSetPackingInfoRequest request);


    //========= 打包信息 end ===========

    //========= 配置 placement options start ===========

    /**
     * 生成 placement options
     * @param inboundPlanId 入仓计划 ID
     * @return placement options
     */
    List<AmzPlacementOption> generatePlacementOptions(AmzPlacementOptionRequest request);

    /**
     * 列出所有 placement options
     * @param request
     * @return placement options
     */
    List<AmzPlacementOption> listPlacementOptions(AmzPlacementOptionRequest request);


    /**
     * 确认 placement options
     * @param request
     * @return
     */
    String confirmPlacementOptions(AmzConfirmPlacementOptionsRequest request);

    /**
     * 设置 placement option
     * @param request
     * @return
     */
    String setPlacementOption(AmzSetPlacementOptionRequest request);
    /**
     * 获取 placement option
     * @param request
     * @return placement option
     */
    AmzPlacementDetailResp getPlacementOption(AmzPlacementOptionRequest request);


     /**
     * 生成运输选项
     * @param request
     * @return transport options
     */
    List<AmzTransportationOption> generateTransportOptions(AmzTransportOptionGenerateRequest request);

    /**
     * 确认运输选项
     * @param request
     * @return
     */
    void confirmTransportOption(AmzConfirmTransportOptionRequest request);

    /**
     * 列出所有运输选项
     * @param request
     * @return
     */
    List<AmzTransportationOption> listTransportOptions(AmzListTransportOptionsRequest request);



    //========= 配置 placement options end ===========


    //========= 运输选项 start ===========




    //========= 运输选项 start ===========



    // ========= 获取标签 ==========
    List<Box> getShipmentBox(AmzListShipmentBoxRequest request);

    public List<AmzShipment> getShipmentLabel(AmzLabelsRequest request);

    List<AmzShipment> generateShipmentLabel(AmzLabelsRequest request);

}
