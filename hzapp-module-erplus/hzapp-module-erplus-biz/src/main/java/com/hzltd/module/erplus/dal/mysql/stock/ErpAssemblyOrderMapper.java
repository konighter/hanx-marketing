package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 装配件订单 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpAssemblyOrderMapper extends BaseMapperX<ErpAssemblyOrderDO> {

    default PageResult<ErpAssemblyOrderDO> selectPage(ErpAssemblyOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpAssemblyOrderDO>()
                .likeIfPresent(ErpAssemblyOrderDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpAssemblyOrderDO::getSkuId, reqVO.getSkuId())
                .eqIfPresent(ErpAssemblyOrderDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(ErpAssemblyOrderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpAssemblyOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpAssemblyOrderDO::getId));
    }

    default ErpAssemblyOrderDO selectByNo(String no) {
        return selectOne(ErpAssemblyOrderDO::getNo, no);
    }

}
