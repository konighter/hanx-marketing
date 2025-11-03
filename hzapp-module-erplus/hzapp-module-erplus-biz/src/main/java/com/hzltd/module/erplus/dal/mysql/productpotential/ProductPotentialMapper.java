package com.hzltd.module.erplus.dal.mysql.productpotential;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.productpotential.ProductPotentialDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.*;

/**
 * 选品提案 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductPotentialMapper extends BaseMapperX<ProductPotentialDO> {

    default PageResult<ProductPotentialDO> selectPage(ProductPotentialPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductPotentialDO>()
                .likeIfPresent(ProductPotentialDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ProductPotentialDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(ProductPotentialDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductPotentialDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ProductPotentialDO::getCreator, reqVO.getCreator())
                .select(ProductPotentialDO::getId, ProductPotentialDO::getTitle, ProductPotentialDO::getPlatformId, ProductPotentialDO::getStatus, ProductPotentialDO::getCreateTime, ProductPotentialDO::getCreator)
                .orderByDesc(ProductPotentialDO::getId));
    }

    default ProductPotentialDO selectByIdSimple(Integer id) {
        return selectOne(new LambdaQueryWrapperX<ProductPotentialDO>().eq(ProductPotentialDO::getId, id)
                .select(ProductPotentialDO::getId, ProductPotentialDO::getTitle, ProductPotentialDO::getPlatformId, ProductPotentialDO::getStatus, ProductPotentialDO::getCreateTime, ProductPotentialDO::getCreator)
        );
    }

}