package com.hzltd.module.erplus.dal.mysql.material;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.material.ErpProductMaterialDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ERP 商品耗材 BOM Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpProductMaterialMapper extends BaseMapperX<ErpProductMaterialDO> {

    default List<ErpProductMaterialDO> selectListBySkuId(Long skuId) {
        return selectList(ErpProductMaterialDO::getSkuId, skuId);
    }

    default int deleteBySkuId(Long skuId) {
        return delete(ErpProductMaterialDO::getSkuId, skuId);
    }

}
