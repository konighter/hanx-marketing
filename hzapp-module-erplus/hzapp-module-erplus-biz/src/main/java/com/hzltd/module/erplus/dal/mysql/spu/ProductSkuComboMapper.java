package com.hzltd.module.erplus.dal.mysql.spu;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuComboDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品 SKU 组合关联 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductSkuComboMapper extends BaseMapperX<ProductSkuComboDO> {

    default List<ProductSkuComboDO> selectListByParentSkuId(Long parentSkuId) {
        return selectList(new LambdaQueryWrapperX<ProductSkuComboDO>()
                .eq(ProductSkuComboDO::getParentSkuId, parentSkuId));
    }

    default void deleteByParentSkuId(Long parentSkuId) {
        delete(new LambdaQueryWrapperX<ProductSkuComboDO>()
                .eq(ProductSkuComboDO::getParentSkuId, parentSkuId));
    }

}
