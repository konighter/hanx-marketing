package com.hzltd.module.erplus.dal.mysql.spu;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuAttrDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductSpuAttrMapper extends BaseMapperX<ProductSpuAttrDO> {

    default List<ProductSpuAttrDO> selectListBySpuId(Long spuId) {
        return selectList(new LambdaQueryWrapperX<ProductSpuAttrDO>()
                .eq(ProductSpuAttrDO::getSpuId, spuId));
    }

    default void deleteBySpuId(Long spuId) {
        delete(new LambdaQueryWrapperX<ProductSpuAttrDO>()
                .eq(ProductSpuAttrDO::getSpuId, spuId));
    }
}
