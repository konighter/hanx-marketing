package com.hzltd.module.erplus.dal.mysql.stock.v2;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 库存账单主表 (Header) Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpInventoryBillMapper extends BaseMapperX<ErpInventoryBillDO> {

    default PageResult<ErpInventoryBillDO> selectPage(ErpInventoryBillPageReqVO pageReqVO) {
        MPJLambdaWrapperX<ErpInventoryBillDO> query = new MPJLambdaWrapperX<ErpInventoryBillDO>()
                .likeIfPresent(ErpInventoryBillDO::getBillCode, pageReqVO.getBillCode())
                .eqIfPresent(ErpInventoryBillDO::getType, pageReqVO.getType())
                .eqIfPresent(ErpInventoryBillDO::getRefType, pageReqVO.getRefType())
                .eqIfPresent(ErpInventoryBillDO::getRefCode, pageReqVO.getRefCode())
                .eqIfPresent(ErpInventoryBillDO::getStatus, pageReqVO.getStatus())
                .eqIfPresent(ErpInventoryBillDO::getFromId, pageReqVO.getFromId())
                .eqIfPresent(ErpInventoryBillDO::getToId, pageReqVO.getToId())
                .betweenIfPresent(ErpInventoryBillDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(ErpInventoryBillDO::getId);
        if (pageReqVO.getWarehouseId() != null) {
            query.and(w -> w.eq(ErpInventoryBillDO::getFromId, pageReqVO.getWarehouseId())
                    .or().eq(ErpInventoryBillDO::getToId, pageReqVO.getWarehouseId()));
        }
        return this.selectJoinPage(pageReqVO, ErpInventoryBillDO.class, query);
    }
}
