package com.hzltd.module.erplus.dal.mysql.stock;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.check.ErpStockCheckPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockCheckDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockCheckItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 库存调拨单 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpStockCheckMapper extends BaseMapperX<ErpStockCheckDO> {

    default PageResult<ErpStockCheckDO> selectPage(ErpStockCheckPageReqVO reqVO) {
        MPJLambdaWrapperX<ErpStockCheckDO> query = new MPJLambdaWrapperX<ErpStockCheckDO>()
                .likeIfPresent(ErpStockCheckDO::getNo, reqVO.getNo())
                .betweenIfPresent(ErpStockCheckDO::getCheckTime, reqVO.getCheckTime())
                .eqIfPresent(ErpStockCheckDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ErpStockCheckDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ErpStockCheckDO::getCreator, reqVO.getCreator())
                .orderByDesc(ErpStockCheckDO::getId);
        if (reqVO.getWarehouseId() != null || reqVO.getProductId() != null) {
            query.leftJoin(ErpStockCheckItemDO.class, ErpStockCheckItemDO::getCheckId, ErpStockCheckDO::getId)
                    .eq(reqVO.getWarehouseId() != null, ErpStockCheckItemDO::getWarehouseId, reqVO.getWarehouseId())
                    .eq(reqVO.getProductId() != null, ErpStockCheckItemDO::getProductId, reqVO.getProductId())
                    .groupBy(ErpStockCheckDO::getId); // 避免 1 对多查询，产生相同的 1
        }
        return selectJoinPage(reqVO, ErpStockCheckDO.class, query);
    }

    default int updateByIdAndStatus(Long id, Integer status, ErpStockCheckDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpStockCheckDO>()
                .eq(ErpStockCheckDO::getId, id).eq(ErpStockCheckDO::getStatus, status));
    }

    default ErpStockCheckDO selectByNo(String no) {
        return selectOne(ErpStockCheckDO::getNo, no);
    }

}