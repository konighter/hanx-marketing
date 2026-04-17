package com.hzltd.module.erplus.dal.mysql.spu;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuAttrDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductSkuAttrMapper extends BaseMapperX<ProductSkuAttrDO> {

    default List<ProductSkuAttrDO> selectListBySkuId(Long skuId) {
        return selectList(new LambdaQueryWrapperX<ProductSkuAttrDO>()
                .eq(ProductSkuAttrDO::getSkuId, skuId));
    }

    default void deleteBySkuId(Long skuId) {
        delete(new LambdaQueryWrapperX<ProductSkuAttrDO>()
                .eq(ProductSkuAttrDO::getSkuId, skuId));
    }
}
