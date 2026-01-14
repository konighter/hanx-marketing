package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.warehouse.ErpWarehouseInventoryPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseInventoryDO;

import java.util.List;

public interface ErplusStockService {



    /**
     * 基于产品 + 仓库，获得仓库可用库存数量
     *
     * @param productId 产品编号
     * @param warehouseId 仓库编号
     * @return 仓库可用库存数量
     */
    List<ErpTransferAvailableRespVO> getTransferAvailableStock(ErpTransferAvailableReqVO reqVO);


     /**
      * 基于产品 + 仓库，获得仓库库存分页
      *
      * @param reqVO 分页查询参数
      * @return 仓库库存分页
      */
      PageResult<ErpWarehouseInventoryDO> getWarehouseInventoryPage(ErpWarehouseInventoryPageReqVO reqVO);
}
