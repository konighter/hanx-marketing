package com.hzltd.module.erplus.service.cross;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderResp;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderSyncRequest;

import java.util.List;

public interface ErplusCrossOrderService {

    void syncCrossOrders(CrossOrderSyncRequest request);

    PageResult<CrossOrderResp> getCrossOrderPage(CrossOrderPageRequest request);

    List<CrossOrderResp> getCrossOrders(List<Long> orderIds);
}
