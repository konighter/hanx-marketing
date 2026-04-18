package com.hzltd.module.erplus.service.spu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.CollectionUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.spu.vo.*;
import com.hzltd.module.erplus.convert.spu.ProductSpuConvert;
import com.hzltd.module.erplus.dal.dataobject.brand.ProductBrandDO;
import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuAttrDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSpuAttrMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSpuMapper;
import com.hzltd.module.erplus.service.brand.ProductBrandService;
import com.hzltd.module.erplus.service.categoryattr.ProductCategoryService;
import com.hzltd.module.erplus.spapi.enums.ProductSpuSpecTypeEnum;
import com.hzltd.module.erplus.spapi.enums.ProductSpuStatusEnum;
import com.hzltd.module.erplus.spapi.model.category.BrandModel;
import com.hzltd.module.erplus.spapi.model.category.CategoryModel;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuMapper;
import com.hzltd.module.erplus.system.model.ProductSkuModel;
import com.hzltd.module.erplus.system.model.ProductSpuModel;
import com.hzltd.module.erplus.system.dto.ProductCertificationDTO;
import com.hzltd.module.erplus.system.dto.ProductDimensionDTO;
import com.hzltd.module.erplus.system.dto.ProductMaterialDTO;
import com.hzltd.module.erplus.system.dto.ProductPackingSchemeDTO;
import com.hzltd.module.erplus.system.dto.ProductQualityReportDTO;
import com.hzltd.module.erplus.system.service.SystemProductService;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.system.enums.ProductAttrKeyEnum;
import com.hzltd.module.erplus.service.spu.utils.ProductAttrUtils;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuAttrDO;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuAttrMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.hzltd.module.erplus.dal.dataobject.product.ErpProductUnitDO;
import com.hzltd.module.erplus.service.cross.backup.ProductUnitService;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.util.collection.CollectionUtils.*;
import static com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO.CATEGORY_LEVEL;
import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.*;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.*;
/**
 * 商品 SPU Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductSpuServiceImpl implements ProductSpuService, SystemProductService {

    @Resource
    private ProductSpuMapper productSpuMapper;
    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ProductSpuAttrMapper productSpuAttrMapper;
    @Resource
    private ProductSkuAttrMapper productSkuAttrMapper;

    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductSkuService productSkuService;
    @Resource
    private ProductBrandService brandService;
    @Resource
    private ProductCategoryService categoryService;
    @Resource
    private ProductCodeService productCodeService;
    @Resource
    private ProductUnitService unitService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpu(ProductSpuSaveReqVO createReqVO) {
        // 校验分类、品牌
        validateCategory(createReqVO.getCategoryId());
        brandService.validateProductBrand(createReqVO.getBrandId());
        // 校验 SKU
        List<ProductSkuSaveReqVO> skuSaveReqList = createReqVO.getSkus();
        productSkuService.validateSkuList(skuSaveReqList, createReqVO.getSpecType());

        ProductSpuDO spu = BeanUtils.toBean(createReqVO, ProductSpuDO.class);
        // 自动生成编码
        if (StringUtils.isBlank(spu.getCode())) {
            spu.setCode(productCodeService.generateSpuCode(createReqVO.getCategoryId()));
        }
        // 初始化 SPU 中 SKU 相关属性
        initSpuFromSkus(spu, skuSaveReqList);
        // 插入 SPU
        productSpuMapper.insert(spu);

        // 插入属性
        saveProductAttrs(spu.getId(), createReqVO);

        // 插入 SKU
        productSkuService.createSkuList(spu.getId(), skuSaveReqList);
        // 返回
        return spu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpu(ProductSpuSaveReqVO updateReqVO) {
        // 校验 SPU 是否存在
        ProductSpuDO spu = validateSpuExists(updateReqVO.getId());
        // 校验分类、品牌
        validateCategory(updateReqVO.getCategoryId());
        brandService.validateProductBrand(updateReqVO.getBrandId());
        // 校验SKU
        List<ProductSkuSaveReqVO> skuSaveReqList = updateReqVO.getSkus();
        productSkuService.validateSkuList(skuSaveReqList, updateReqVO.getSpecType());

        // 更新 SPU
        ProductSpuDO updateObj = BeanUtils.toBean(updateReqVO, ProductSpuDO.class);
        // 编码处理
        if (StringUtils.isBlank(updateObj.getCode())) {
            updateObj.setCode(spu.getCode());
        }
        initSpuFromSkus(updateObj, skuSaveReqList);
        productSpuMapper.updateById(updateObj);

        // 更新属性
        saveProductAttrs(updateObj.getId(), updateReqVO);

        // 批量更新 SKU
        productSkuService.updateSkuList(updateObj.getId(), updateReqVO.getSkus());
    }

    /**
     * 保存 SPU 属性 (差异更新)
     */
    private void saveProductAttrs(Long spuId, ProductSpuSaveReqVO reqVO) {
        Map<String, Object> attributes = reqVO.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        // 1. 获取现有属性
        List<ProductSpuAttrDO> existingAttrs = productSpuAttrMapper.selectListBySpuId(spuId);
        Map<String, ProductSpuAttrDO> existingMap = convertMap(existingAttrs, ProductSpuAttrDO::getAttrId);

        // 2. 差异对比并执行
        Set<String> processedKeys = new HashSet<>();
        for (ProductAttrKeyEnum keyEnum : ProductAttrKeyEnum.values()) {
            String key = keyEnum.getKey();
            Object newVal = attributes.get(key);
            processedKeys.add(key);
            
            ProductSpuAttrDO oldAttr = existingMap.get(key);
            if (newVal == null) {
                if (oldAttr != null) {
                    productSpuAttrMapper.deleteById(oldAttr.getId());
                }
                continue;
            }

            String newValJson = JsonUtils.toJsonString(newVal);
            if (oldAttr == null) {
                productSpuAttrMapper.insert(ProductSpuAttrDO.builder()
                        .spuId(spuId)
                        .attrId(key)
                        .attrName(keyEnum.getName())
                        .attrValue(newValJson)
                        .attrClass(keyEnum.getClazz().getName())
                        .build());
            } else if (!ObjectUtil.equal(oldAttr.getAttrValue(), newValJson)) {
                productSpuAttrMapper.updateById(ProductSpuAttrDO.builder()
                        .id(oldAttr.getId())
                        .attrValue(newValJson)
                        .build());
            }
        }
        
        // 处理未在枚举中定义的自定义属性 (可选保留)
        attributes.forEach((key, val) -> {
            if (processedKeys.contains(key)) return;
            // 处理自定义属性逻辑... (目前暂不处理，保持严谨)
        });

        // 3. 删除多余的属性
        existingAttrs.forEach(old -> {
            if (!processedKeys.contains(old.getAttrId())) {
                productSpuAttrMapper.deleteById(old.getId());
            }
        });
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
            spu.setStatus(ProductSpuStatusEnum.DRAFT.getStatus()); // 默认状态为草稿
        }
    }

    /**
     * 校验商品分类是否合法
     *
     * @param id 商品分类编号
     */
    private void validateCategory(Long id) {
        categoryService.validateCategory(id);
        // 校验层级
        if (categoryService.getCategoryLevel(id) < CATEGORY_LEVEL) {
            throw exception(SPU_SAVE_FAIL_CATEGORY_LEVEL_ERROR);
        }
    }

    @Override
    public List<ProductSpuDO> validateSpuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 获得商品信息
        List<ProductSpuDO> list = productSpuMapper.selectByIds(ids);
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
        productSpuMapper.updateBrowseCount(id , incrCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpu(Long id) {
        // 校验存在
        validateSpuExists(id);
        // 校验商品状态不是回收站不能删除
        ProductSpuDO spuDO = productSpuMapper.selectById(id);
        // 判断 SPU 状态是否为回收站
        if (ObjectUtil.notEqual(spuDO.getStatus(), ProductSpuStatusEnum.ARCHIVED.getStatus())) {
            throw exception(SPU_NOT_RECYCLE);
        }
        // TODO 芋艿：【可选】参与活动中的商品，不允许删除？？？

        // 删除 SPU
        productSpuMapper.deleteById(id);
        // 删除关联的 SKU
        productSkuService.deleteSkuBySpuId(id);
    }

    private ProductSpuDO validateSpuExists(Long id) {
        ProductSpuDO spuDO = productSpuMapper.selectById(id);
        if (spuDO == null) {
            throw exception(SPU_NOT_EXISTS);
        }
        return spuDO;
    }

    @Override
    public ProductSpuDO getSpu(Long id) {
        return productSpuMapper.selectById(id);
    }

    @Override
    public ProductSpuDO getSpu(Long id, boolean includeDeleted) {
        if (includeDeleted) {
            return productSpuMapper.selectByIdIncludeDeleted(id);
        }
        return getSpu(id);
    }

    @Override
    public List<ProductSpuDO> getSpuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        Map<Long, ProductSpuDO> spuMap = convertMap(productSpuMapper.selectByIds(ids), ProductSpuDO::getId);
        // 需要按照 ids 顺序返回。例如说：店铺装修选择了 [3, 1, 2] 三个商品，返回结果还是 [3, 1, 2]  这样的顺序
        return convertList(ids, spuMap::get);
    }

    @Override
    public List<ProductSpuDO> getSpuListByStatus(Integer status) {
        return productSpuMapper.selectList(ProductSpuDO::getStatus, status);
    }

    @Override
    public PageResult<ProductSpuDO> getSpuPage(ProductSpuPageReqVO pageReqVO) {
        return productSpuMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<ProductSpuRespVO> getSpuPageWithSku(ProductSpuPageReqVO pageReqVO) {
        PageResult<ProductSpuDO> spuPageResult = productSpuMapper.selectPage(pageReqVO);
        if (cn.hutool.core.collection.CollUtil.isEmpty(spuPageResult.getList())) {
            return PageResult.empty();
        }

        // 批量查询元数据：分类、品牌、单位
        Set<Long> categoryIds = convertSet(spuPageResult.getList(), ProductSpuDO::getCategoryId);
        Set<Long> brandIds = convertSet(spuPageResult.getList(), ProductSpuDO::getBrandId);
        Set<Long> unitIds = convertSet(spuPageResult.getList(), ProductSpuDO::getUnitId);
        
        Map<Long, ProductCategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap() :
                convertMap(categoryService.getProductCategoryList(categoryIds), ProductCategoryDO::getId);
        Map<Long, ProductBrandDO> brandMap = brandIds.isEmpty() ? Collections.emptyMap() :
                convertMap(brandService.getBrandList(brandIds), ProductBrandDO::getId);
        Map<Long, ErpProductUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap() :
                convertMap(unitService.getProductUnitList(unitIds), ErpProductUnitDO::getId);

        // 统一查询 SKU，避免 N+1
        Set<Long> spuIds = convertSet(spuPageResult.getList(), ProductSpuDO::getId);
        Map<Long, List<ProductSkuDO>> skuMap = convertMultiMap(productSkuService.getSkuListBySpuId(spuIds), ProductSkuDO::getSpuId);

        // 转换为响应 VO
        List<ProductSpuRespVO> respList = convertList(spuPageResult.getList(), spu -> {
            ProductSpuRespVO respVO = BeanUtils.toBean(spu, ProductSpuRespVO.class);
            // 填充元数据
            Optional.ofNullable(categoryMap.get(spu.getCategoryId())).ifPresent(c -> respVO.setCategoryName(c.getName()));
            Optional.ofNullable(brandMap.get(spu.getBrandId())).ifPresent(b -> respVO.setBrandName(b.getName()));
            Optional.ofNullable(unitMap.get(spu.getUnitId())).ifPresent(u -> respVO.setUnitName(u.getName()));
            // 填充 SKUs
            respVO.setSkus(BeanUtils.toBean(skuMap.get(spu.getId()), ProductSkuRespVO.class));
            return respVO;
        });

        return new PageResult<>(respList, spuPageResult.getTotal());
    }

    @Override
    public PageResult<ProductSkuRespVO> getSkuPage(ProductSpuPageReqVO pageVO) {
        // 1. 查询 SPU 编号列表（带过滤条件）
        List<ProductSpuDO> spus = productSpuMapper.selectList(pageVO);
        if (cn.hutool.core.collection.CollUtil.isEmpty(spus)) {
            return PageResult.empty();
        }
        Set<Long> spuIds = convertSet(spus, ProductSpuDO::getId);

        // 2. 查询 SKU 分页
        PageResult<ProductSkuDO> skuPage = productSkuMapper.selectPage(pageVO, new LambdaQueryWrapperX<ProductSkuDO>()
                .and(w -> w.in(ProductSkuDO::getSpuId, spuIds)
                    .or().like(ProductSkuDO::getCode, pageVO.getSearchValue())
                    .or().like(ProductSkuDO::getBarCode, pageVO.getSearchValue())
                )
        );

        // 3. 填充 SPU 冗余信息
        Map<Long, ProductSpuDO> spuMap = convertMap(spus, ProductSpuDO::getId);
        Set<Long> categoryIds = convertSet(spus, ProductSpuDO::getCategoryId);
        Set<Long> brandIds = convertSet(spus, ProductSpuDO::getBrandId);
        Set<Long> unitIds = convertSet(spus, ProductSpuDO::getUnitId);

        Map<Long, ProductCategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap() :
                convertMap(categoryService.getProductCategoryList(categoryIds), ProductCategoryDO::getId);
        Map<Long, ProductBrandDO> brandMap = brandIds.isEmpty() ? Collections.emptyMap() :
                convertMap(brandService.getBrandList(brandIds), ProductBrandDO::getId);
        Map<Long, ErpProductUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap() :
                convertMap(unitService.getProductUnitList(unitIds), ErpProductUnitDO::getId);

        return BeanUtils.toBean(skuPage, ProductSkuRespVO.class, vo -> {
            ProductSpuDO spu = spuMap.get(vo.getSpuId());
            if (spu != null) {
                vo.setSpuName(spu.getName());
                vo.setSpuCode(spu.getCode());
                vo.setStatus(spu.getStatus());
                vo.setCreateTime(spu.getCreateTime());
                Optional.ofNullable(categoryMap.get(spu.getCategoryId())).ifPresent(c -> vo.setCategoryName(c.getName()));
                Optional.ofNullable(brandMap.get(spu.getBrandId())).ifPresent(b -> vo.setBrandName(b.getName()));
                Optional.ofNullable(unitMap.get(spu.getUnitId())).ifPresent(u -> vo.setUnitName(u.getName()));
            }
        });
    }

    @Override
    public ProductSpuRespVO getSpuDetail(Long id) {
        ProductSpuDO spu = getSpu(id);
        if (spu == null) {
            return null;
        }
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
        ProductSpuRespVO respVO = ProductSpuConvert.INSTANCE.convert(spu, skus);
        
        // 补充元数据名称
        Optional.ofNullable(categoryService.getProductCategory(spu.getCategoryId())).ifPresent(c -> respVO.setCategoryName(c.getName()));
        Optional.ofNullable(brandService.getBrand(spu.getBrandId())).ifPresent(b -> respVO.setBrandName(b.getName()));
        Optional.ofNullable(unitService.getProductUnit(spu.getUnitId())).ifPresent(u -> respVO.setUnitName(u.getName()));

        hydrateSpuAttrs(respVO);
        return respVO;
    }

    @Override
    public List<ProductSpuRespVO> getSpuDetailList(Collection<Long> ids) {
        List<ProductSpuDO> spus = getSpuList(ids);
        if (CollUtil.isEmpty(spus)) {
            return Collections.emptyList();
        }
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(ids);
        return ProductSpuConvert.INSTANCE.convertForSpuDetailRespListVO(spus, skus);
    }

    private void hydrateSpuAttrs(ProductSpuRespVO respVO) {
        List<ProductSpuAttrDO> attrs = productSpuAttrMapper.selectListBySpuId(respVO.getId());
        Map<String, Object> attributeMap = new HashMap<>();
        for (ProductSpuAttrDO attr : attrs) {
            ProductAttrKeyEnum keyEnum = ProductAttrKeyEnum.findByKey(attr.getAttrId());
            if (keyEnum != null) {
                attributeMap.put(attr.getAttrId(), JsonUtils.parseObject(attr.getAttrValue(), keyEnum.getClazz()));
            } else {
                attributeMap.put(attr.getAttrId(), attr.getAttrValue());
            }
        }
        respVO.setAttributes(attributeMap);

        // 填充 SKU 属性和耗材
        if (CollUtil.isNotEmpty(respVO.getSkus())) {
            respVO.getSkus().forEach(sku -> {
                productSkuService.hydrateSkuAttrs(sku);
                productSkuService.hydrateSkuMaterials(sku);
                productSkuService.hydrateSkuCombo(sku);
            });
        }
    }

    private <T> Integer getSumValue(List<ProductSkuSaveReqVO> skus, Function<ProductSkuSaveReqVO, T> fieldGetter, BinaryOperator<Integer> sumOperator) {
        if (CollUtil.isEmpty(skus)) {
            return 0;
        }
        return skus.stream().map(fieldGetter)
                .filter(Objects::nonNull)
                .map(v -> {
                    if (v instanceof Integer) return (Integer) v;
                    if (v instanceof Long) return ((Long) v).intValue();
                    return 0;
                })
                .reduce(0, sumOperator);
    }

    private <T extends Comparable<T>> T getMinValue(List<ProductSkuSaveReqVO> skus, Function<ProductSkuSaveReqVO, T> fieldGetter) {
        if (CollUtil.isEmpty(skus)) {
            return null;
        }
        return skus.stream().map(fieldGetter)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        return json == null ? null : com.hzltd.framework.common.util.json.JsonUtils.parseObject(json, clazz);
    }

    private <T> List<T> fromJsonList(String json, Class<T> clazz) {
        return json == null ? null : com.hzltd.framework.common.util.json.JsonUtils.parseArray(json, clazz);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuStock(Map<Long, Integer> stockIncrCounts) {
        stockIncrCounts.forEach((id, incCount) -> productSpuMapper.updateStock(id, incCount));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuStatus(ProductSpuUpdateStatusReqVO updateReqVO) {
        // 校验存在
        validateSpuExists(updateReqVO.getId());
        // TODO 芋艿：【可选】参与活动中的商品，不允许下架？？？

        // 更新状态
        ProductSpuDO productSpuDO = productSpuMapper.selectById(updateReqVO.getId()).setStatus(updateReqVO.getStatus());
        productSpuMapper.updateById(productSpuDO);
    }

    @Override
    public Map<Integer, Long> getTabsCount() {
        Map<Integer, Long> counts = Maps.newLinkedHashMapWithExpectedSize(6);
        // 草稿中的数量
        counts.put(ProductSpuPageReqVO.DRAFT,
                productSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.DRAFT.getStatus()));
        // 查询销售中的商品数量
        counts.put(ProductSpuPageReqVO.FOR_SALE,
                productSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.FOR_SALE.getStatus()));
        // 查询下架的商品数量
        counts.put(ProductSpuPageReqVO.OFF_SALE,
                productSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.OFF_SALE.getStatus()));
        // 查询售空的商品数量
        counts.put(ProductSpuPageReqVO.SOLD_OUT,
                productSpuMapper.selectCount(ProductSpuDO::getStock, 0));
        // 查询触发警戒库存的商品数量
        counts.put(ProductSpuPageReqVO.ALERT_STOCK,
                productSpuMapper.selectCount());
        // 查询回收站中的商品数量
        counts.put(ProductSpuPageReqVO.ARCHIVED,
                productSpuMapper.selectCount(ProductSpuDO::getStatus, ProductSpuStatusEnum.ARCHIVED.getStatus()));
        return counts;
    }

    @Override
    public Long getSpuCountByCategoryId(Long categoryId) {
        return productSpuMapper.selectCount(ProductSpuDO::getCategoryId, categoryId);
    }

    @Override
    public ProductSpuModel getProductSpu(Long spuId) {
        ProductSpuDO spuDO = getSpu(spuId);
        List<ProductSkuDO> skuDOList = productSkuService.getSkuListBySpuId(spuId);
        ProductCategoryDO categoryDO = categoryService.getProductCategory(spuDO.getCategoryId());
        ProductBrandDO brandDO = brandService.getBrand(spuDO.getBrandId());

        ProductSpuModel spuModel = BeanUtils.toBean(spuDO, ProductSpuModel.class);
        spuModel.setCategoryModel(BeanUtils.toBean(categoryDO, CategoryModel.class));
        spuModel.setBrandModel(BeanUtils.toBean(brandDO, BrandModel.class));
        spuModel.setSkuModelList(BeanUtils.toBean(skuDOList, ProductSkuModel.class));

        // 填充属性
        hydrateSpuModelAttrs(spuModel);

        return spuModel;
    }

    private void hydrateSpuModelAttrs(ProductSpuModel model) {
        List<ProductSpuAttrDO> attrs = productSpuAttrMapper.selectListBySpuId(model.getId());
        if (attrs.isEmpty()) {
            return;
        }
        Map<String, String> attrMap = CollectionUtils.convertMap(attrs, ProductSpuAttrDO::getAttrId, ProductSpuAttrDO::getAttrValue);

        model.setItemDim(fromJson(attrMap.get("itemDim"), ProductDimensionDTO.class));
        model.setPkgDim(fromJson(attrMap.get("pkgDim"), ProductDimensionDTO.class));
        model.setBoxDim(fromJson(attrMap.get("boxDim"), ProductDimensionDTO.class));
        model.setInboxnum(fromJson(attrMap.get("inboxnum"), Integer.class));

        model.setCertifications(fromJsonList(attrMap.get("certifications"), ProductCertificationDTO.class));
        model.setSafetyStandards(fromJsonList(attrMap.get("safetyStandards"), String.class));
        model.setSafetyWarnings(fromJsonList(attrMap.get("safetyWarnings"), String.class));
        model.setMaterials(fromJsonList(attrMap.get("materials"), ProductMaterialDTO.class));
        model.setHazardousSubstances(fromJsonList(attrMap.get("hazardousSubstances"), String.class));
        model.setEnvironmentalCertifications(fromJsonList(attrMap.get("environmentalCertifications"), String.class));
        model.setPackagingMaterials(fromJsonList(attrMap.get("packagingMaterials"), String.class));
        model.setApplicableRegulations(fromJsonList(attrMap.get("applicableRegulations"), String.class));
        model.setRestrictedRegions(fromJsonList(attrMap.get("restrictedRegions"), String.class));
        model.setSpecialLicenses(fromJsonList(attrMap.get("specialLicenses"), String.class));
        model.setQualityReports(fromJsonList(attrMap.get("qualityReports"), ProductQualityReportDTO.class));
        Map<String, Object> attributes = fromJson(attrMap.get("attributes"), Map.class);
        if (attributes == null) {
            attributes = new java.util.HashMap<>();
        }
        attributes.put("packingSchemes", fromJsonList(attrMap.get("packingSchemes"), ProductPackingSchemeDTO.class));
        model.setAttributes(attributes);
    }
    @Override
    public List<ProductSkuRespVO> getSkuList(Collection<Long> ids, Collection<String> codes, Boolean hydrate) {
        // 1. 获取 SKU 数据
        Map<Long, ProductSkuDO> skuMapTemp = new LinkedHashMap<>();
        if (CollUtil.isNotEmpty(ids)) {
            productSkuMapper.selectBatchIds(ids).forEach(sku -> skuMapTemp.putIfAbsent(sku.getId(), sku));
        }
        if (CollUtil.isNotEmpty(codes)) {
            productSkuMapper.selectListByCodes(codes).forEach(sku -> skuMapTemp.putIfAbsent(sku.getId(), sku));
        }
        if (skuMapTemp.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProductSkuDO> skuList = new ArrayList<>(skuMapTemp.values());

        // 2. 批量查询元数据 (参考 getSkuPage 为组装做准备)
        Set<Long> spuIds = convertSet(skuList, ProductSkuDO::getSpuId);
        List<ProductSpuDO> spus = productSpuMapper.selectByIds(spuIds);
        Map<Long, ProductSpuDO> spuMap = convertMap(spus, ProductSpuDO::getId);
        
        Set<Long> categoryIds = convertSet(spus, ProductSpuDO::getCategoryId);
        Set<Long> brandIds = convertSet(spus, ProductSpuDO::getBrandId);
        Set<Long> unitIds = convertSet(spus, ProductSpuDO::getUnitId);

        Map<Long, ProductCategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap() :
                convertMap(categoryService.getProductCategoryList(categoryIds), ProductCategoryDO::getId);
        Map<Long, ProductBrandDO> brandMap = brandIds.isEmpty() ? Collections.emptyMap() :
                convertMap(brandService.getBrandList(brandIds), ProductBrandDO::getId);
        Map<Long, ErpProductUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap() :
                convertMap(unitService.getProductUnitList(unitIds), ErpProductUnitDO::getId);

        // 3. 组装并返回
        return BeanUtils.toBean(skuList, ProductSkuRespVO.class, vo -> {
            ProductSpuDO spu = spuMap.get(vo.getSpuId());
            if (spu != null) {
                vo.setSpuName(spu.getName());
                vo.setSpuCode(spu.getCode());
                vo.setStatus(spu.getStatus());
                vo.setCreateTime(spu.getCreateTime());
                Optional.ofNullable(categoryMap.get(spu.getCategoryId())).ifPresent(c -> vo.setCategoryName(c.getName()));
                Optional.ofNullable(brandMap.get(spu.getBrandId())).ifPresent(b -> vo.setBrandName(b.getName()));
                Optional.ofNullable(unitMap.get(spu.getUnitId())).ifPresent(u -> vo.setUnitName(u.getName()));
            }
            // 条件填充：耗材、属性、组合成分
            if (Boolean.TRUE.equals(hydrate)) {
                productSkuService.hydrateSkuAttrs(vo);
                productSkuService.hydrateSkuMaterials(vo);
                productSkuService.hydrateSkuCombo(vo);
            }
        });
    }

}
