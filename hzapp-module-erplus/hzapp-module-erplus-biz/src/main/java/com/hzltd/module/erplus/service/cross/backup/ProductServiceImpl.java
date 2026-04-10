package com.hzltd.module.erplus.service.cross.backup;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.product.vo.product.ProductSaveReqVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSpuMapper;
import com.hzltd.module.erplus.service.categoryattr.ProductCategoryService;
import com.hzltd.module.erplus.spapi.enums.ProductSpuStatusEnum;
import com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertMap;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_NOT_ENABLE;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_NOT_EXISTS;

/**
 * ERP 产品 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductSpuMapper productSpuMapper;

    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductUnitService productUnitService;

    @Override
    public Long createProduct(ProductSaveReqVO createReqVO) {
        throw exception(ErplusErrorCodeConstants.OPT_NOT_SUPPORT);
    }

    @Override
    public void updateProduct(ProductSaveReqVO updateReqVO) {
        throw exception(ErplusErrorCodeConstants.OPT_NOT_SUPPORT);
    }

    @Override
    public void deleteProduct(Long id) {
        throw exception(ErplusErrorCodeConstants.OPT_NOT_SUPPORT);
    }

    @Override
    public List<ProductSpuDO> validProductList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<ProductSpuDO> list = productSpuMapper.selectBatchIds(ids);
        Map<Long, ProductSpuDO> productMap = convertMap(list, ProductSpuDO::getId);
        for (Long id : ids) {
            ProductSpuDO product = productMap.get(id);
            if (productMap.get(id) == null) {
                throw exception(PRODUCT_NOT_EXISTS);
            }
            if (!ProductSpuStatusEnum.isEnable(product.getStatus())) {
                throw exception(PRODUCT_NOT_ENABLE, product.getName());
            }
        }
        return list;
    }

    private void validateProductExists(Long id) {
        if (productSpuMapper.selectById(id) == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
    }

    @Override
    public ProductSpuDO getProduct(Long id) {
        return productSpuMapper.selectById(id);
    }

    @Override
    public List<ProductSpuRespVO> getProductVOListByStatus(Integer status) {
        List<ProductSpuDO> list = productSpuMapper.selectList(new LambdaQueryWrapperX<ProductSpuDO>().eqIfPresent(ProductSpuDO::getStatus, status));
        return buildProductVOList(list);
    }

    @Override
    public List<ProductSpuRespVO> getProductVOList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<ProductSpuDO> list = productSpuMapper.selectBatchIds(ids);
        return buildProductVOList(list);
    }

    private List<ProductSpuRespVO> buildProductVOList(List<ProductSpuDO> list) {
                    return Collections.emptyList();
//        if (CollUtil.isEmpty(list)) {
//            return Collections.emptyList();
//        }
//        Map<Long, ProductCategoryDO> categoryMap = productCategoryService.getProductCategoryMap(
//                convertSet(list, ProductSpuDO::getCategoryId));
//        Map<Long, ErpProductUnitDO> unitMap = productUnitService.getProductUnitMap(
//                convertSet(list, ProductSpuDO::getUnitId));
//        return BeanUtils.toBean(list, ProductSpuRespVO.class, product -> {
//            MapUtils.findAndThen(categoryMap, product.getCategoryId(),
//                    category -> product.setCategoryName(category.getName()));
//            MapUtils.findAndThen(unitMap, product.getUnitId(),
//                    unit -> product.setUnitName(unit.getName()));
//        });
    }

    @Override
    public Long getProductCountByCategoryId(Long categoryId) {
        return productSpuMapper.selectCount(new LambdaQueryWrapperX<ProductSpuDO>().eq(ProductSpuDO::getCategoryId, categoryId));
    }



}