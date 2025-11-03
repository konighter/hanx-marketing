package com.hzltd.module.erplus.service.product;

import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.model.common.ProductAttributeModel;
import com.hzltd.module.erplus.model.product.CreateProductRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.convert.productpub.CrossPlatformProductConvert;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductAttrsDO;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductMapper;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductAttrsMapper;
import com.hzltd.module.erplus.enums.CrossProductPublishStatus;
import com.hzltd.module.erplus.enums.CrossProductStatus;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static com.hzltd.module.erplus.enums.ErrorCodeConstants.PRODUCT_NOT_VALID;

@Service
public class ErpCrossProductServiceImpl implements ErpCrossProductService {

    @Resource
    private ErpCrossProductMapper crossProductMapper;

    @Resource
    private ErpCrossProductAttrsMapper crossProductAttrsMapper;


    @Override
    public Optional<CrossPlatformProductVO> getCrossPlatformProduct(Long productId) {
        ErpCrossProductDO crossPlatformProductDO = crossProductMapper.selectById(productId);
        if (crossPlatformProductDO == null) {
            return Optional.empty();
        }
        CrossPlatformProductVO platformProductVO = CrossPlatformProductConvert.INSTANCE.convert(crossPlatformProductDO);

        List<ErpCrossProductAttrsDO> productAttrsDOs = crossProductAttrsMapper.selectByProductId(productId);
        if (CollectionUtils.isNotEmpty(productAttrsDOs)) {
            List<ProductAttributeModel> attributeModels = CrossPlatformProductConvert.INSTANCE.convert(productAttrsDOs);
            platformProductVO.setProductAttributes(attributeModels);
        }

        return Optional.of(platformProductVO);
    }

    @Override
    public Optional<ErpCrossProductDO> getBasicCrossPlatformProduct(Long productId) {
        return Optional.ofNullable(crossProductMapper.selectOne(new LambdaQueryWrapperX<ErpCrossProductDO>().select(
                ErpCrossProductDO::getId,
                ErpCrossProductDO::getPlatformId,
                ErpCrossProductDO::getShopId,
                ErpCrossProductDO::getMarketId,
                ErpCrossProductDO::getPlatformProductId,
                ErpCrossProductDO::getRelateProductId,
                ErpCrossProductDO::getFulfillType,
                ErpCrossProductDO::getLanguage,
                ErpCrossProductDO::getTitle,
                ErpCrossProductDO::getCategoryId,
                ErpCrossProductDO::getBrandId,
                ErpCrossProductDO::getStatus,
                ErpCrossProductDO::getPublishStatus
        ).eq(ErpCrossProductDO::getId, productId)));
    }

    @Transactional
    @Override
    public Long saveCrossPlatformProduct(ProductPublishRequest product) {
        // 保存商品信息
        ErpCrossProductDO crossPlatformProduct = CrossPlatformProductConvert.INSTANCE.convert(product);
        crossPlatformProduct.setStatus(CrossProductStatus.INIT.getStatus());
        crossPlatformProduct.setPublishStatus(CrossProductPublishStatus.INIT.getStatus());
        crossProductMapper.insert(crossPlatformProduct);
        Long productId = crossPlatformProduct.getId();
        // 保存商品属性
        List<ErpCrossProductAttrsDO> crossProductAttrs = CrossPlatformProductConvert.INSTANCE.convertProperties(productId, product.getProductAttributes());
        crossProductAttrsMapper.insertBatch(crossProductAttrs);

        if (!this.validCrossPlatformProduct(productId)) {
            throw new ServiceException(PRODUCT_NOT_VALID);
        }

        return productId;
    }

    @Override
    public Boolean validCrossPlatformProduct(Long productId) {
        return true;
    }
}
