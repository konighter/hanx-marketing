package com.hzltd.module.erplus.dal.mysql.spu;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductClaimDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品认领 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface ProductClaimMapper extends BaseMapperX<ProductClaimDO> {

    default PageResult<ProductClaimDO> selectPage(ProductClaimPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductClaimDO>()
                .eqIfPresent(ProductClaimDO::getSpuId, reqVO.getSpuId())
                .eqIfPresent(ProductClaimDO::getSkuInfo, reqVO.getSkuInfo())
                .eqIfPresent(ProductClaimDO::getSpecType, reqVO.getSpecType())
                .eqIfPresent(ProductClaimDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ProductClaimDO::getLanguage, reqVO.getLanguage())
                .eqIfPresent(ProductClaimDO::getSellZone, reqVO.getSellZone())
                .eqIfPresent(ProductClaimDO::getCategory, reqVO.getCategory())
                .eqIfPresent(ProductClaimDO::getBrandId, reqVO.getBrandId())
                .eqIfPresent(ProductClaimDO::getSellPrice, reqVO.getSellPrice())
                .eqIfPresent(ProductClaimDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(ProductClaimDO::getExtra, reqVO.getExtra())
                .eqIfPresent(ProductClaimDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductClaimDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProductClaimDO::getId));
    }

}