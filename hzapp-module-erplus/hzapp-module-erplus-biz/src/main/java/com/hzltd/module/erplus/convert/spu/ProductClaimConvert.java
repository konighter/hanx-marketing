package com.hzltd.module.erplus.convert.spu;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimBatchReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductClaimDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductClaimConvert {
    ProductClaimConvert INSTANCE = Mappers.getMapper(ProductClaimConvert.class);

    default ProductClaimDO convert(ProductClaimBatchReqVO reqVO) {
        ProductClaimDO productClaimDO = new ProductClaimDO();
        productClaimDO.setSpuId(reqVO.getSpuId());
        productClaimDO.setPlatform(reqVO.getPlatform());
        productClaimDO.setSellZone(reqVO.getSellZone());
        productClaimDO.setLanguage(reqVO.getLanguage());
        productClaimDO.setCurrency(reqVO.getCurrency());
        productClaimDO.setBrandId(reqVO.getBrandId());
        productClaimDO.setCategory(reqVO.getCategory());
        productClaimDO.setSpecType(reqVO.getSpecType());
        productClaimDO.setSkuInfo(JsonUtils.toJsonString(reqVO.getSkus()));
        productClaimDO.setExtra(JsonUtils.toJsonString(reqVO.getExtra()));

        return productClaimDO;
    }





}
