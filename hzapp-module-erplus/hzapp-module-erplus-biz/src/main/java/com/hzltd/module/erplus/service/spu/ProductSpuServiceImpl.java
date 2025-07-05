package com.hzltd.module.erplus.service.spu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.CollectionUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.hzltd.module.erplus.controller.admin.spu.vo.*;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.enums.ProductSpuStatusEnum;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.dal.mysql.spu.ErpProductSpuMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.framework.common.util.collection.CollectionUtils.getMinValue;
import static com.hzltd.framework.common.util.collection.CollectionUtils.getSumValue;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 商品 SPU Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductSpuServiceImpl implements ProductSpuService {

    @Resource
    private ErpProductSpuMapper erpProductSpuMapper;

    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductSkuService productSkuService;
//    @Resource
//    private ProductBrandService brandService;
//    @Resource
//    private ProductCategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpu(ProductSpuSaveReqVO createReqVO) {
        // 校验分类、品牌
        validateCategory(createReqVO.getCategoryId());
//        brandService.validateProductBrand(createReqVO.getBrandId());
        // 校验 SKU
        List<ProductSkuSaveReqVO> skuSaveReqList = createReqVO.getSkus();
        productSkuService.validateSkuList(skuSaveReqList, createReqVO.getSpecType());

        ProductSpuDO spu = BeanUtils.toBean(createReqVO, ProductSpuDO.class);
        // 初始化 SPU 中 SKU 相关属性
        initSpuFromSkus(spu, skuSaveReqList);
        // 插入 SPU
        erpProductSpuMapper.insert(spu);
        // 插入 SKU
        productSkuService.createSkuList(spu.getId(), skuSaveReqList);
        // 返回
        return spu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpu(ProductSpuSaveReqVO updateReqVO) {
        // 校验 SPU 是否存在
        validateSpuExists(updateReqVO.getId());
        // 校验分类、品牌
        validateCategory(updateReqVO.getCategoryId());
//        brandService.validateProductBrand(updateReqVO.getBrandId());
        // 校验SKU
        List<ProductSkuSaveReqVO> skuSaveReqList = updateReqVO.getSkus();
        productSkuService.validateSkuList(skuSaveReqList, updateReqVO.getSpecType());

        // 更新 SPU
        ProductSpuDO updateObj = BeanUtils.toBean(updateReqVO, ProductSpuDO.class);
        initSpuFromSkus(updateObj, skuSaveReqList);
        erpProductSpuMapper.updateById(updateObj);
        // 批量更新 SKU
        productSkuService.updateSkuList(updateObj.getId(), updateReqVO.getSkus());
    }

    /**
     * 基于 SKU 的信息，初始化 SPU 的信息
     * 主要是计数相关的字段，例如说市场价、最大最小价、库存等等
     *
     * @param spu  商品 SPU
     * @param skus 商品 SKU 数组
     */
    private void initSpuFromSkus(ProductSpuDO spu, List<ProductSkuSaveReqVO> skus) {
        // sku 单价最低的商品的价格
        spu.setPrice(getMinValue(skus, ProductSkuSaveReqVO::getPrice));
        // sku 单价最低的商品的市场价格
        spu.setMarketPrice(getMinValue(skus, ProductSkuSaveReqVO::getMarketPrice));
        // sku 单价最低的商品的成本价格
        spu.setCostPrice(getMinValue(skus, ProductSkuSaveReqVO::getCostPrice));
        // skus 库存总数
        spu.setStock(getSumValue(skus, ProductSkuSaveReqVO::getStock, Integer::sum));
        // 若是 spu 已有状态则不处理
        if (spu.getStatus() == null) {
            spu.setStatus(ProductSpuStatusEnum.CLAIMED.getStatus()); // 默认状态为上架
            spu.setSalesCount(0); // 默认商品销量
            spu.setBrowseCount(0); // 默认商品浏览量
        }
    }

    /**
     * 校验商品分类是否合法
     *
     * @param id 商品分类编号
     */
    private void validateCategory(Long id) {
//        categoryService.validateCategory(id);
//        // 校验层级
//        if (categoryService.getCategoryLevel(id) < CATEGORY_LEVEL) {
//            throw exception(SPU_SAVE_FAIL_CATEGORY_LEVEL_ERROR);
//        }
    }

    @Override
    public List<ProductSpuDO> validateSpuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 获得商品信息
        List<ProductSpuDO> list = erpProductSpuMapper.selectBatchIds(ids);
        Map<Long, ProductSpuDO> spuMap = CollectionUtils.convertMap(list, ProductSpuDO::getId);
        // 校验
        ids.forEach(id -> {
            ProductSpuDO spu = spuMap.get(id);
            if (spu == null) {
                throw exception(SPU_NOT_EXISTS);
            }
            if (!ProductSpuStatusEnum.isEnable(spu.getStatus())) {
                throw exception(SPU_NOT_ENABLE, spu.getName());
            }
        });
        return list;
    }

    @Override
    public void updateBrowseCount(Long id, int incrCount) {
        erpProductSpuMapper.updateBrowseCount(id , incrCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpu(Long id) {
        // 校验存在
        validateSpuExists(id);
        // 校验商品状态不是回收站不能删除
        ProductSpuDO spuDO = erpProductSpuMapper.selectById(id);
        // 判断 SPU 状态是否为回收站
        if (ObjectUtil.notEqual(spuDO.getStatus(), ProductSpuStatusEnum.RECYCLE.getStatus())) {
            throw exception(SPU_NOT_RECYCLE);
        }
        // TODO 芋艿：【可选】参与活动中的商品，不允许删除？？？

        // 删除 SPU
        erpProductSpuMapper.deleteById(id);
        // 删除关联的 SKU
        productSkuService.deleteSkuBySpuId(id);
    }

    private void validateSpuExists(Long id) {
        if (erpProductSpuMapper.selectById(id) == null) {
            throw exception(SPU_NOT_EXISTS);
        }
    }

    @Override
    public ProductSpuDO getSpu(Long id) {
        return erpProductSpuMapper.selectById(id);
    }

    @Override
    public List<ProductSpuDO> getSpuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return erpProductSpuMapper.selectBatchIds(ids);
    }

    @Override
    public List<ProductSpuDO> getSpuListByStatus(Integer status) {
        return erpProductSpuMapper.selectList(ProductSpuDO::getStatus, status);
    }

    @Override
    public PageResult<ProductSpuDO> getSpuPage(ProductSpuPageReqVO pageReqVO) {
        return erpProductSpuMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<ProductSkuRespDetailVO> getSkuPageBySpu(ProductSpuPageReqVO pageReqVO) {

        PageResult<ProductSkuRespDetailVO> pageResult = erpProductSpuMapper.selectJoinPage(pageReqVO, ProductSkuRespDetailVO.class,
                new MPJLambdaWrapperX<ProductSpuDO>()
                        .selectAll(ProductSkuDO.class)
                        .selectAs(ProductSpuDO::getId, ProductSkuRespDetailVO::getSpuId)
                        .selectAs(ProductSpuDO::getCategoryId, ProductSkuRespDetailVO::getCategoryId)
                        .selectAs(ProductSpuDO::getName, ProductSkuRespDetailVO::getSpuName)
                        .innerJoin(ProductSkuDO.class, ProductSkuDO::getSpuId, ProductSpuDO::getId)
                        .eqIfPresent(ProductSpuDO::getId, pageReqVO.getSpuId())
                        .likeIfPresent(ProductSpuDO::getName, pageReqVO.getName())
                        .eqIfPresent(ProductSpuDO::getStatus, ProductSpuStatusEnum.CLAIMED.getStatus()));

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuStock(Map<Long, Integer> stockIncrCounts) {
        stockIncrCounts.forEach((id, incCount) -> erpProductSpuMapper.updateStock(id, incCount));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuStatus(ProductSpuUpdateStatusReqVO updateReqVO) {
        // 校验存在
        validateSpuExists(updateReqVO.getId());
        // TODO 芋艿：【可选】参与活动中的商品，不允许下架？？？

        // 更新状态
        ProductSpuDO productSpuDO = erpProductSpuMapper.selectById(updateReqVO.getId()).setStatus(updateReqVO.getStatus());
        erpProductSpuMapper.updateById(productSpuDO);
    }

    @Override
    public Map<Integer, Long> getTabsCount() {
        Map<Integer, Long> counts = Maps.newLinkedHashMapWithExpectedSize(5);
        // 查询销售中的商品数量
        counts.put(ProductSpuPageReqVO.FOR_SALE,
                erpProductSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.CLAIMED.getStatus()));
        // 查询仓库中的商品数量
        counts.put(ProductSpuPageReqVO.IN_WAREHOUSE,
                erpProductSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.COLLECTED.getStatus()));
        // 查询售空的商品数量
        counts.put(ProductSpuPageReqVO.SOLD_OUT,
                erpProductSpuMapper.selectCount(ProductSpuDO::getStock, 0));
        // 查询触发警戒库存的商品数量
        counts.put(ProductSpuPageReqVO.ALERT_STOCK,
                erpProductSpuMapper.selectCount());
        // 查询回收站中的商品数量
        counts.put(ProductSpuPageReqVO.RECYCLE_BIN,
                erpProductSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.RECYCLE.getStatus()));
//        // 查询已认领的商品数量
//        counts.put(ProductSpuPageReqVO.CLAIMED,
//                erpProductSpuMapper.selectCount(new LambdaQueryWrapperX<ProductSpuDO>()
//                        .eq(ProductSpuDO::getStatus, ProductSpuStatusEnum.CLAIMED.getStatus())
//                        .eq(ProductSpuDO::getClaimStatus, CommonStatusEnum.ENABLE.getStatus())));
        return counts;
    }

    @Override
    public Long getSpuCountByCategoryId(Long categoryId) {
        return erpProductSpuMapper.selectCount(ProductSpuDO::getCategoryId, categoryId);
    }

}
