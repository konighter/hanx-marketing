package com.hzltd.module.erplus.dal.mysql.brand;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.brand.vo.ProductBrandListReqVO;
import com.hzltd.module.erplus.controller.admin.brand.vo.ProductBrandPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.brand.ProductBrandDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpProductBrandMapper extends BaseMapperX<ProductBrandDO> {

    default PageResult<ProductBrandDO> selectPage(ProductBrandPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductBrandDO>()
                .likeIfPresent(ProductBrandDO::getName, reqVO.getName())
                .eqIfPresent(ProductBrandDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductBrandDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProductBrandDO::getId));
    }


    default List<ProductBrandDO> selectList(ProductBrandListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ProductBrandDO>()
                .likeIfPresent(ProductBrandDO::getName, reqVO.getName()));
    }

    default ProductBrandDO selectByName(String name) {
        return selectOne(ProductBrandDO::getName, name);
    }

    default List<ProductBrandDO> selectListByStatus(Integer status) {
        return selectList(ProductBrandDO::getStatus, status);
    }
}
