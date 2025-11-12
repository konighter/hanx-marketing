package com.hzltd.module.erplus.service.productassets;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.checksum.CRC16;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.zip.CRC32;

import com.hzltd.module.erplus.controller.admin.productassets.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productassets.ProductAssetsDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.util.object.BeanUtils;

import com.hzltd.module.erplus.dal.mysql.productassets.ProductAssetsMapper;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertList;
import static com.hzltd.framework.common.util.collection.CollectionUtils.diffList;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 商品素材 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductAssetsServiceImpl implements ProductAssetsService {

    @Resource
    private ProductAssetsMapper productAssetsMapper;

    @Override
    public Integer createProductAssets(ProductAssetsSaveReqVO createReqVO) {
        // 插入
        ProductAssetsDO productAssets = BeanUtils.toBean(createReqVO, ProductAssetsDO.class);
        productAssets.setStatus(CommonStatusEnum.ENABLE.getStatus());
        productAssets.setAssetInfoHash(SecureUtil.md5(productAssets.getAssetLink()));

        if (CollectionUtils.isNotEmpty(productAssetsMapper.selectList(new LambdaQueryWrapperX<ProductAssetsDO>()
                .eq(ProductAssetsDO::getAssetInfoHash, productAssets.getAssetInfoHash())))) {
            throw exception(PRODUCT_ASSETS_EXISTS);
        }

        productAssetsMapper.insert(productAssets);

        // 返回
        return productAssets.getId();
    }

    @Override
    public void updateProductAssets(ProductAssetsSaveReqVO updateReqVO) {
        // 校验存在
        validateProductAssetsExists(updateReqVO.getId());
        // 更新
        ProductAssetsDO updateObj = BeanUtils.toBean(updateReqVO, ProductAssetsDO.class);
        productAssetsMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductAssets(Integer id) {
        // 校验存在
        validateProductAssetsExists(id);
        // 删除
        productAssetsMapper.deleteById(id);
    }

    @Override
        public void deleteProductAssetsListByIds(List<Integer> ids) {
        // 删除
        productAssetsMapper.deleteByIds(ids);
        }


    private void validateProductAssetsExists(Integer id) {
        if (productAssetsMapper.selectById(id) == null) {
            throw exception(PRODUCT_ASSETS_NOT_EXISTS);
        }
    }

    @Override
    public ProductAssetsDO getProductAssets(Integer id) {
        return productAssetsMapper.selectById(id);
    }

    @Override
    public PageResult<ProductAssetsDO> getProductAssetsPage(ProductAssetsPageReqVO pageReqVO) {
        return productAssetsMapper.selectPage(pageReqVO);
    }

}