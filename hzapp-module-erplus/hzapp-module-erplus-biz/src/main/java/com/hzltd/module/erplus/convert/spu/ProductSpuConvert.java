package com.hzltd.module.erplus.convert.spu;

import com.hzltd.framework.common.util.collection.CollectionUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erpls.api.model.product.CreateProductRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSkuRespVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.util.collection.CollectionUtils.convertMultiMap;

/**
 * 商品 SPU Convert
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);


    default ProductSpuRespVO convert(ProductSpuDO spu, List<ProductSkuDO> skus) {
        ProductSpuRespVO spuVO = BeanUtils.toBean(spu, ProductSpuRespVO.class);
        spuVO.setSkus(BeanUtils.toBean(skus, ProductSkuRespVO.class));
        return spuVO;
    }

    default List<ProductSpuRespVO> convertForSpuDetailRespListVO(List<ProductSpuDO> spus, List<ProductSkuDO> skus) {
        Map<Long, List<ProductSkuDO>> skuMultiMap = convertMultiMap(skus, ProductSkuDO::getSpuId);
        return CollectionUtils.convertList(spus, spu -> convert(spu, skuMultiMap.get(spu.getId())));
    }

    default ProductSpuSaveReqVO convert(ProductPublishRequest productRequest) {
        return null;
    }

}
