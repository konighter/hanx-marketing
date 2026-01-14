package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * 商品spu Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossProductMapper extends BaseMapperX<CrossProductDO> {

    default List<CrossProductDO> selectByShopId(Integer shopId) {
        return selectList(new LambdaQueryWrapperX<CrossProductDO>().eqIfPresent(CrossProductDO::getShopId, shopId));
    }

    default List<CrossProductDO> selectByPlatformId(Integer platformId) {
        return selectList(new LambdaQueryWrapperX<CrossProductDO>().eqIfPresent(CrossProductDO::getPlatformId, platformId));
    }

    default List<CrossProductDO> selectByParam(CrossProductDO req) {
        return selectList(new LambdaQueryWrapperX<CrossProductDO>()
                .eqIfPresent(CrossProductDO::getPlatformId, req.getPlatformId())
                .eqIfPresent(CrossProductDO::getShopId, req.getShopId())
                .eqIfPresent(CrossProductDO::getSellerSkuCode, req.getPlatformProductCode())
                .eqIfPresent(CrossProductDO::getSellerSkuCode, req.getSellerSkuCode())
//                .eqIfPresent(ErpCrossProductDO::getPublishStatus, req.getPublishStatus())
                .eqIfPresent(CrossProductDO::getStatus, req.getStatus())
                // 从请求时间到现在
                .betweenIfPresent(CrossProductDO::getUpdateTime, req.getUpdateTime(), new Date())
        );
    }



}