package com.hzltd.module.erplus.dal.mysql.product;


import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductAttrsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品属性 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossProductAttrsMapper extends BaseMapperX<ErpCrossProductAttrsDO> {

    default List<ErpCrossProductAttrsDO> selectByProductId(Long productId) {
        return selectList(new LambdaQueryWrapperX<ErpCrossProductAttrsDO>().eq(ErpCrossProductAttrsDO::getProductId, productId));
    }
}
