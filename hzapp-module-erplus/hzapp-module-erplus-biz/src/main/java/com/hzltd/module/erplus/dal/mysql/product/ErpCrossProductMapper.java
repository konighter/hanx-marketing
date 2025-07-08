package com.hzltd.module.erplus.dal.mysql.product;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * 商品spu Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossProductMapper extends BaseMapperX<ErpCrossProductDO> {

    default List<ErpCrossProductDO> selectByShopId(Integer shopId) {
        return selectList(new LambdaQueryWrapperX<ErpCrossProductDO>().eqIfPresent(ErpCrossProductDO::getShopId, shopId));
    }

    default List<ErpCrossProductDO> selectByPlatformId(Integer platformId) {
        return selectList(new LambdaQueryWrapperX<ErpCrossProductDO>().eqIfPresent(ErpCrossProductDO::getPlatformId, platformId));
    }

    default List<ErpCrossProductDO> selectByParam(ErpCrossProductDO req) {
        return selectList(new LambdaQueryWrapperX<ErpCrossProductDO>()
                .eqIfPresent(ErpCrossProductDO::getPlatformId, req.getPlatformId())
                .eqIfPresent(ErpCrossProductDO::getShopId, req.getShopId())
                .eqIfPresent(ErpCrossProductDO::getPlatformProductId, req.getPlatformProductId())
                .eqIfPresent(ErpCrossProductDO::getRelateProductId, req.getRelateProductId())
                .eqIfPresent(ErpCrossProductDO::getPublishStatus, req.getPublishStatus())
                .eqIfPresent(ErpCrossProductDO::getStatus, req.getStatus())
                // 从请求时间到现在
                .betweenIfPresent(ErpCrossProductDO::getUpdateTime, req.getUpdateTime(), new Date())
        );
    }



}