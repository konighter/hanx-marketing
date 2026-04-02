package com.hzltd.module.erplus.service.stock.v2.event;

import com.google.common.eventbus.Subscribe;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillSaveReqVO;
import com.hzltd.module.erplus.service.event.ErpEventListener;
import com.hzltd.module.erplus.service.stock.v2.ErpInventoryBillService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ERP 库存账目监听器
 * 负责监听 {@link ErpStockUpdateEvent} 并生成库存账单及更新实物库存
 *
 * @author 翰展科技
 */
@Component
@Slf4j
@ErpEventListener(theme = "STOCK")
public class ErpInventoryBillListener {

    @Resource
    private ErpInventoryBillService inventoryBillService;

    @Subscribe
    public void onStockUpdateEvent(ErpStockUpdateEvent event) {
        log.info("[onStockUpdateEvent][收到库存变动事件: {}]", event);
        try {
            // 将事件转换为创建请求 VO
            ErpInventoryBillSaveReqVO saveReqVO = BeanUtils.toBean(event, ErpInventoryBillSaveReqVO.class);

            // 执行账单创建（内部包含库存锁定与更新逻辑）
            inventoryBillService.createInventoryBill(saveReqVO);

            log.info("[onStockUpdateEvent][库存变动处理成功: {}]", event.getRefCode());
        } catch (Exception e) {
            log.error("[onStockUpdateEvent][库存变动处理失败: {}]", event.getRefCode(), e);
            // 抛出异常以确保事务回滚（如果监听器在事务内）
            throw e;
        }
    }

}
