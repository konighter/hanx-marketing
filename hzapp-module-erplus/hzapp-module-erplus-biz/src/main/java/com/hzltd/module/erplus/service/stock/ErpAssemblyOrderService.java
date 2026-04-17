package com.hzltd.module.erplus.service.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyOrderDO;

import java.util.List;

/**
 * ERP 装配单 Service 接口
 *
 * @author 翰展科技
 */
public interface ErpAssemblyOrderService {

    /**
     * 创建装配单
     *
     * @param reqVO 创建信息
     * @return 编号
     */
    Long createAssemblyOrder(ErpAssemblyOrderSaveReqVO reqVO);

    /**
     * 更新装配单
     *
     * @param reqVO 更新信息
     */
    void updateAssemblyOrder(ErpAssemblyOrderSaveReqVO reqVO);

    /**
     * 删除装配单
     *
     * @param id 编号
     */
    void deleteAssemblyOrder(Long id);

    /**
     * 获得装配单
     *
     * @param id 编号
     * @return 装配单
     */
    ErpAssemblyOrderDO getAssemblyOrder(Long id);

    /**
     * 获得装配单分页
     *
     * @param pageReqVO 分页查询
     * @return 装配单分页
     */
    PageResult<ErpAssemblyOrderDO> getAssemblyOrderPage(ErpAssemblyOrderPageReqVO pageReqVO);

    /**
     * 启动装配单
     *
     * @param id 编号
     */
    void startAssemblyOrder(Long id);

    /**
     * 完成装配单
     *
     * @param id 编号
     */
    void completeAssemblyOrder(Long id);

    /**
     * 取消装配单
     *
     * @param id 编号
     */
    void cancelAssemblyOrder(Long id);

    /**
     * 获取装配单明细
     *
     * @param orderId 装配单编号
     * @return 明细列表
     */
    List<ErpAssemblyItemDO> getAssemblyItemList(Long orderId);

}
