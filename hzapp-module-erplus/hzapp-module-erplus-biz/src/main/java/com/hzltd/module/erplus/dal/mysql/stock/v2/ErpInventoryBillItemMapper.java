package com.hzltd.module.erplus.dal.mysql.stock.v2;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryBillItemPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ERP 库存账单明细表 (Detail) Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpInventoryBillItemMapper extends BaseMapperX<ErpInventoryBillItemDO> {

    default List<ErpInventoryBillItemDO> selectListByBillId(Long billId) {
        return selectList(ErpInventoryBillItemDO::getBillId, billId);
    }

    default PageResult<ErpInventoryBillItemDO> selectPage(ErpInventoryBillItemPageReqVO pageReqVO) {
        MPJLambdaWrapperX<ErpInventoryBillItemDO> query = new MPJLambdaWrapperX<ErpInventoryBillItemDO>()
                .selectAll(ErpInventoryBillItemDO.class)
                .selectAs(ErpInventoryBillDO::getBillCode, "billCode")
                .selectAs(ErpInventoryBillDO::getType, "type")
                .selectAs(ErpInventoryBillDO::getRefType, "refType")
                .selectAs(ErpInventoryBillDO::getRefCode, "refCode")
                .selectAs(ErpInventoryBillDO::getFromId, "fromId")
                .selectAs(ErpInventoryBillDO::getToId, "toId")
                .selectAs(ErpInventoryBillDO::getRemark, "remark")
                .selectAs(ErpInventoryBillDO::getCreateTime, "createTime")
                .leftJoin(ErpInventoryBillDO.class, ErpInventoryBillDO::getId, ErpInventoryBillItemDO::getBillId)
                .likeIfPresent(ErpInventoryBillItemDO::getSellerSku, pageReqVO.getSellerSku())
                .eqIfPresent(ErpInventoryBillDO::getType, pageReqVO.getType())
                .eqIfPresent(ErpInventoryBillDO::getRefType, pageReqVO.getRefType())
                .eqIfPresent(ErpInventoryBillDO::getRefCode, pageReqVO.getRefCode())
                .betweenIfPresent(ErpInventoryBillItemDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(ErpInventoryBillItemDO::getId);

        if (pageReqVO.getWarehouseId() != null) {
            query.and(w -> w.eq(ErpInventoryBillDO::getFromId, pageReqVO.getWarehouseId())
                    .or().eq(ErpInventoryBillDO::getToId, pageReqVO.getWarehouseId()));
        }

        return selectJoinPage(pageReqVO, ErpInventoryBillItemDO.class, query);
    }

}
