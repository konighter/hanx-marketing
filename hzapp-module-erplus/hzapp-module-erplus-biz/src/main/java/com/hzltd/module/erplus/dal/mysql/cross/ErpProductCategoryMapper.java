package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategoryListReqVO;
import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ERP 产品分类 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpProductCategoryMapper extends BaseMapperX<ProductCategoryDO> {

    default List<ProductCategoryDO> selectList(ProductCategoryListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ProductCategoryDO>()
                .likeIfPresent(ProductCategoryDO::getName, reqVO.getName())
                .eqIfPresent(ProductCategoryDO::getStatus, reqVO.getStatus())
                .orderByDesc(ProductCategoryDO::getId));
    }

	default ProductCategoryDO selectByParentIdAndName(Long parentId, String name) {
	    return selectOne(ProductCategoryDO::getParentId, parentId, ProductCategoryDO::getName, name);
	}

    default Long selectCountByParentId(Long parentId) {
        return selectCount(ProductCategoryDO::getParentId, parentId);
    }

}