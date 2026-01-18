package com.hzltd.module.erplus.service.stock.v2;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckItemDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * ERP 库存盘点 (V2) Service 接口
 *
 * @author 翰展科技
 */
public interface ErpInventoryCheckService {

    /**
     * 创建库存盘点单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInventoryCheck(@Valid ErpInventoryCheckSaveReqVO createReqVO);

    /**
     * 更新库存盘点结果
     *
     * @param updateReqVO 更新信息
     */
    void updateInventoryCheck(@Valid ErpInventoryCheckSaveReqVO updateReqVO);

    /**
     * 审核库存盘点单
     *
     * @param id 编号
     */
    void approveInventoryCheck(Long id);

    /**
     * 获得库存盘点单
     *
     * @param id 编号
     * @return 库存盘点单
     */
    ErpInventoryCheckDO getInventoryCheck(Long id);

    /**
     * 获得库存盘点单分页
     *
     * @param pageReqVO 分页查询
     * @return 库存盘点单分页
     */
    PageResult<ErpInventoryCheckDO> getInventoryCheckPage(ErpInventoryCheckPageReqVO pageReqVO);

    /**
     * 获得库存盘点单项列表
     *
     * @param checkId 盘点编号
     * @return 库存盘点单项列表
     */
    List<ErpInventoryCheckItemDO> getInventoryCheckItemListByCheckId(Long checkId);

}
