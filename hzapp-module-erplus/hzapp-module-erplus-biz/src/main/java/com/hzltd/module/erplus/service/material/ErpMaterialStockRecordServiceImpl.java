package com.hzltd.module.erplus.service.material;

import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockRecordDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialStockRecordMapper;
import com.hzltd.module.erplus.service.material.bo.ErpMaterialStockRecordCreateReqBO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 耗材库存明细 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpMaterialStockRecordServiceImpl implements ErpMaterialStockRecordService {

    @Resource
    private ErpMaterialStockRecordMapper materialStockRecordMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private ErpMaterialStockService materialStockService;

    @Override
    public void createMaterialStockRecord(ErpMaterialStockRecordCreateReqBO createReqBO) {
        // 1. 更新库存
        BigDecimal newQuantity = materialStockService.updateMaterialStockCountIncrement(
                createReqBO.getMaterialId(), createReqBO.getWarehouseId(), createReqBO.getCount());

        // 2. 保存明细
        ErpMaterialStockRecordDO record = BeanUtils.toBean(createReqBO, ErpMaterialStockRecordDO.class);
        record.setTotalCount(newQuantity);
        materialStockRecordMapper.insert(record);
    }

    @Override
    public List<ErpMaterialStockRecordDO> getMaterialStockRecordListByMaterialId(Long materialId) {
        return materialStockRecordMapper.selectList(new LambdaQueryWrapperX<ErpMaterialStockRecordDO>()
                .eq(ErpMaterialStockRecordDO::getMaterialId, materialId)
                .orderByDesc(ErpMaterialStockRecordDO::getId));
    }

}
