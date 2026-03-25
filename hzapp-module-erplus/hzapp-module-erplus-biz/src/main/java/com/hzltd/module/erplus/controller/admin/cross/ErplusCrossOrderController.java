package com.hzltd.module.erplus.controller.admin.cross;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderResp;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderSyncRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.erplus.service.cross.ErplusCrossOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/erplus/order/cross-order")
public class ErplusCrossOrderController {

    @Resource
    private ErplusCrossOrderService crossOrderService;


     /**
      * 查询跨境订单
      * @param request
      * @return
      */
    @PostMapping("/page")
    public ApiResponse<PageResult<CrossOrderResp>> getCrossOrderPage(@RequestBody CrossOrderPageRequest request) {
        return ApiResponse.success(crossOrderService.getCrossOrderPage(request));
    }

    /**
     * 同步跨境订单
     * @param request
     * @return
     */
    @PostMapping("/sync")
    public ApiResponse<?> syncCrossOrder(@RequestBody CrossOrderSyncRequest request) {
        crossOrderService.syncCrossOrders(request);
        return ApiResponse.success(true);
    }






}
