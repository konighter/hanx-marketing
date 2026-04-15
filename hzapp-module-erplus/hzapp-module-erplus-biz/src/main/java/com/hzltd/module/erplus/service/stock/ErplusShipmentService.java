package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentAuditReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.StockShipmentPlanReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanRespVO;

public interface ErplusShipmentService {

    /**
     * 保存货件计划
     *
     * @param reqVO 货件计划
     * @return 货件计划编号
     */
    Integer saveShipmentPlan(StockShipmentPlanReqVO reqVO);

    /**
     * 提交货件计划
     *
     * @param reqVO 货件计划
     * @return 货件计划编号
     */
    Integer submitShipmentPlan(StockShipmentPlanReqVO reqVO);

     /**
     * 审核货件计划
     *
     * @param reqVO 货件计划
     */
    void auditShipment(ShipmentAuditReqVO reqVO);

    /**
     * 更新货件计划状态
     *
     * @param shipmentId 货件计划编号
     * @param status     状态
     */
    void updateShipmentStatus(Integer shipmentId, Integer status);

    /**
     * 获取货件计划分页
     *
     * @param reqVO 分页查询
     * @return 货件计划分页
     */
    PageResult<StockShipmentPlanRespVO> getShipmentPlanPage(StockShipmentPlanPageReqVO reqVO);

     /**
     * 获取货件计划详情
     *
     * @param id 货件计划编号
     * @return 货件计划详情
     */
    StockShipmentPlanRespVO getShipmentPlan(Integer id);
}
