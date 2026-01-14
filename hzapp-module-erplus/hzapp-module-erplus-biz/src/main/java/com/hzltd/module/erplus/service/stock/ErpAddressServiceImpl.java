package com.hzltd.module.erplus.service.stock;

import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ErpAddressRespVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAddressDO;
import com.hzltd.module.erplus.dal.mysql.stock.ErpAddressMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpAddressServiceImpl implements ErpAddressService {

    @Resource
    private ErpAddressMapper addressMapper;


    @Override
    public List<ErpAddressRespVO> getShipmentAddress() {
        return List.of();
    }

    @Override
    public ErpAddressDO getAddressById(Integer id) {
        return addressMapper.selectById(id);
    }
}
