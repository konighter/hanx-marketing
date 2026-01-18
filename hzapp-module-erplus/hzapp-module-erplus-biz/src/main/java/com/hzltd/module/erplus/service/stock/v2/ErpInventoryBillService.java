package com.hzltd.module.erplus.service.stock.v2;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillItemPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillItemDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * ERP 库存账单 (V2) Service 接口
 *
 * @author 翰展科技
 */
public interface ErpInventoryBillService {

    /**
     * 创建库存账单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInventoryBill(@Valid ErpInventoryBillSaveReqVO createReqVO);

    /**
     * 获得库存账单
     *
     * @param id 编号
     * @return 库存账单
     */
    ErpInventoryBillDO getInventoryBill(Long id);

    /**
     * 获得库存账单分页
     *
     * @param pageReqVO 分页查询
     * @return 库存账单分页
     */
    PageResult<ErpInventoryBillDO> getInventoryBillPage(ErpInventoryBillPageReqVO pageReqVO);

    /**
     * 获得库存账单项列表
     *
     * @param billId 账单编号
     * @return 库存账单项列表
     */
    List<ErpInventoryBillItemDO> getInventoryBillItemListByBillId(Long billId);

    /**
     * 获得库存账单项分页
     *
     * @param pageReqVO 分页查询
     * @return 库存账单项分页
     */
    PageResult<ErpInventoryBillItemDO> getInventoryBillItemPage(ErpInventoryBillItemPageReqVO pageReqVO);

}
