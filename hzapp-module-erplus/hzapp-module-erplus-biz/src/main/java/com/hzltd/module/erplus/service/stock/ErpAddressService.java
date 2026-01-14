package com.hzltd.module.erplus.service.stock;

import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ErpAddressRespVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAddressDO;

import java.util.List;

public interface ErpAddressService {

    /**
     * 获得货件地址列表
     *
     * @return 货件地址列表
     */
    List<ErpAddressRespVO> getShipmentAddress();

    /**
     * 获得货件地址详情
     *
     * @param id 货件地址编号
     * @return 货件地址详情
     */
    ErpAddressDO getAddressById(Integer id);
}
