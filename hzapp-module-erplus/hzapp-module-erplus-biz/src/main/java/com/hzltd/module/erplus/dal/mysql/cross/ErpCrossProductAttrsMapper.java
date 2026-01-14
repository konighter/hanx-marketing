package com.hzltd.module.erplus.dal.mysql.cross;


import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductAttrsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品属性 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossProductAttrsMapper extends BaseMapperX<CrossProductAttrsDO> {

    default List<CrossProductAttrsDO> selectByProductId(Long productId) {
        return selectList(new LambdaQueryWrapperX<CrossProductAttrsDO>().eq(CrossProductAttrsDO::getProductId, productId));
    }
}
