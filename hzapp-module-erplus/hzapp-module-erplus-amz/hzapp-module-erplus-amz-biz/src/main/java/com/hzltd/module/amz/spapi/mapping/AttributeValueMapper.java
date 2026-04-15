package com.hzltd.module.amz.spapi.mapping;

import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.system.model.ProductSpuModel;

public interface AttributeValueMapper {

    /**
     * 映射属性值到目标类型
     *
     * @param spuModel 商品SPU模型
     * @return 映射后的目标类型值
     */
    void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel);

    String getAttribute();
}
