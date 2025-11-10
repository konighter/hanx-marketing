package com.hzltd.module.erplus.dal.mysql.productassets;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.productassets.ProductAssetsDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.productassets.vo.*;

/**
 * 商品素材 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductAssetsMapper extends BaseMapperX<ProductAssetsDO> {

    default PageResult<ProductAssetsDO> selectPage(ProductAssetsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductAssetsDO>()
                .likeIfPresent(ProductAssetsDO::getProductName, reqVO.getProductName())
                .eqIfPresent(ProductAssetsDO::getTags, reqVO.getTags())
                .eqIfPresent(ProductAssetsDO::getType, reqVO.getType())
                .eqIfPresent(ProductAssetsDO::getAssetInfo, reqVO.getAssetInfo())
                .eqIfPresent(ProductAssetsDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductAssetsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProductAssetsDO::getId));
    }

}