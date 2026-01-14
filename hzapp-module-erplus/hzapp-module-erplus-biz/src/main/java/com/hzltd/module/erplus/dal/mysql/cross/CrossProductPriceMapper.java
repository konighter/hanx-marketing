package com.hzltd.module.erplus.dal.mysql.crossproductprice;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.crossproductprice.CrossProductPriceDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.crossproductprice.vo.*;

/**
 * 跨境产品价格 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossProductPriceMapper extends BaseMapperX<CrossProductPriceDO> {

    default PageResult<CrossProductPriceDO> selectPage(CrossProductPricePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossProductPriceDO>()
                .eqIfPresent(CrossProductPriceDO::getProductId, reqVO.getProductId())
                .eqIfPresent(CrossProductPriceDO::getCostPrice, reqVO.getCostPrice())
                .eqIfPresent(CrossProductPriceDO::getSalePrice, reqVO.getSalePrice())
                .eqIfPresent(CrossProductPriceDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(CrossProductPriceDO::getEstimatedPlatformFee, reqVO.getEstimatedPlatformFee())
                .eqIfPresent(CrossProductPriceDO::getEstimatedPlatformFeeDetail, reqVO.getEstimatedPlatformFeeDetail())
                .betweenIfPresent(CrossProductPriceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrossProductPriceDO::getId));
    }

}