package com.hzltd.module.erplus.service.cross.event;

import com.google.common.eventbus.Subscribe;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderSyncRequest;
import com.hzltd.module.erplus.event.ErpEventListener;
import com.hzltd.module.erplus.service.cross.ErplusCrossOrderService;
import com.hzltd.module.erplus.spapi.event.OrderChangeEvent;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * ERP 跨平台订单事件监听器
 * 负责接收订单变更事件并触发同步逻辑
 *
 * @author antigravity
 */
@Component
@Slf4j
@ErpEventListener
public class ErpCrossOrderEventListener {

    @Resource
    private ErplusCrossOrderService crossOrderService;

    /**
     * 监听亚马逊订单变更事件
     *
     * @param event 订单变更事件
     */
    @Subscribe
    public void onAmzOrderChangeEvent(OrderChangeEvent event) {
        log.info("[onOrderChangeEvent][收到{}订单变更事件: {}]", CrossPlatformEnum.valueOf(event.getPlatformId()).getName(), event.getPlatformOrderId());
        
        try {
            // 构造同步请求
            CrossOrderSyncRequest syncRequest = new CrossOrderSyncRequest();
            syncRequest.setShopId(event.getShopId());
            syncRequest.setPlatformId(event.getPlatformId());
            syncRequest.setPlatformOrderId(event.getPlatformOrderId());
            
            // 执行同步
            crossOrderService.syncCrossOrders(syncRequest);
            
            log.info("[onOrderChangeEvent][订单同步任务已触发: {}]", event.getPlatformOrderId());
        } catch (Exception e) {
            log.error("[onOrderChangeEvent][处理订单变更事件失败: {}]", event.getPlatformOrderId(), e);
        }
    }

}
