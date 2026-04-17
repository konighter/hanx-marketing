package com.hzltd.module.erplus.service.spu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.api.sku.dto.ProductSkuUpdateStockReqDTO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSkuSaveReqVO;
import com.hzltd.module.erplus.convert.spu.ProductSkuConvert;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSkuRespVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpProductMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.spu.*;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.material.ErpProductMaterialMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuAttrMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuComboMapper;
import com.hzltd.module.erplus.dal.mysql.spu.ProductSkuMapper;
import com.hzltd.module.erplus.service.cross.backup.ProductUnitService;
import com.hzltd.module.erplus.spapi.enums.ProductSkuTypeEnum;
import com.hzltd.module.erplus.spapi.enums.ProductSpuSpecTypeEnum;
import com.hzltd.module.erplus.system.enums.ProductAttrKeyEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertMap;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertSet;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.*;

/**
 * 商品 SKU Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
@Slf4j
public class ProductSkuServiceImpl implements ProductSkuService {

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductSpuService productSpuService;
    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductPropertyService productPropertyService;
    @Resource
    private ProductPropertyValueService productPropertyValueService;
    @Resource
    private ProductSkuComboMapper productSkuComboMapper;
    @Resource
    private ProductSkuAttrMapper productSkuAttrMapper;
    @Resource
    private ProductCodeService productCodeService;
    @Resource
    private ErpProductMaterialMapper erpProductMaterialMapper;
    @Resource
    private ErpMaterialMapper erpMaterialMapper;
    @Resource
    private ProductUnitService unitService;

    @Override
    public void deleteSku(Long id) {
        // 校验存在
        validateSkuExists(id);
        // 删除
        productSkuMapper.deleteById(id);
    }

    private void validateSkuExists(Long id) {
        if (productSkuMapper.selectById(id) == null) {
            throw exception(SKU_NOT_EXISTS);
        }
    }

    @Override
    public ProductSkuDO getSku(Long id) {
        return productSkuMapper.selectById(id);
    }

    @Override
    public ProductSkuDO getSku(Long id, boolean includeDeleted) {
        if (includeDeleted) {
            return productSkuMapper.selectByIdIncludeDeleted(id);
        }
        return getSku(id);
    }

    @Override
    public List<ProductSkuDO> getSkuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return productSkuMapper.selectByIds(ids);
    }

    @Override
    public void validateSkuList(List<ProductSkuSaveReqVO> skus, Integer specType) {
        // 0、校验skus是否为空
        if (CollUtil.isEmpty(skus)) {
            throw exception(SKU_NOT_EXISTS);
        }
        // 单规格，赋予单规格默认属性
        if (ObjectUtil.equal(specType, ProductSpuSpecTypeEnum.SINGLE.getType())) {
            ProductSkuSaveReqVO skuVO = skus.get(0);
            List<ProductSkuSaveReqVO.Property> properties = new ArrayList<>();
            ProductSkuSaveReqVO.Property property = new ProductSkuSaveReqVO.Property()
                    .setPropertyId(ProductPropertyDO.ID_DEFAULT).setPropertyName(ProductPropertyDO.NAME_DEFAULT)
                    .setValueId(ProductPropertyValueDO.ID_DEFAULT).setValueName(ProductPropertyValueDO.NAME_DEFAULT);
            properties.add(property);
            skuVO.setProperties(properties);
            return; // 单规格不需要后续的校验
        }

        // 1、校验属性项存在
        Set<Long> propertyIds = skus.stream().filter(p -> p.getProperties() != null)
                // 遍历多个 Property 属性
                .flatMap(p -> p.getProperties().stream())
                // 将每个 Property 转换成对应的 propertyId，最后形成集合
                .map(ProductSkuSaveReqVO.Property::getPropertyId)
                .collect(Collectors.toSet());
        List<ProductPropertyDO> propertyList = productPropertyService.getPropertyList(propertyIds);
        if (propertyList.size() != propertyIds.size()) {
            throw exception(PROPERTY_NOT_EXISTS);
        }

        // 2. 校验，一个 SKU 下，没有重复的属性。校验方式是，遍历每个 SKU ，看看是否有重复的属性 propertyId
        Map<Long, ProductPropertyValueDO> propertyValueMap = convertMap(productPropertyValueService.getPropertyValueListByPropertyId(propertyIds), ProductPropertyValueDO::getId);
        skus.forEach(sku -> {
            Set<Long> skuPropertyIds = convertSet(sku.getProperties(), propertyItem -> propertyValueMap.get(propertyItem.getValueId()).getPropertyId());
            if (skuPropertyIds.size() != sku.getProperties().size()) {
                throw exception(SKU_PROPERTIES_DUPLICATED);
            }
        });

        // 3. 再校验，每个 Sku 的属性值的数量，是一致的。
        int attrValueIdsSize = skus.get(0).getProperties().size();
        for (int i = 1; i < skus.size(); i++) {
            if (attrValueIdsSize != skus.get(i).getProperties().size()) {
                throw exception(SPU_ATTR_NUMBERS_MUST_BE_EQUALS);
            }
        }

        // 4. 最后校验，每个 Sku 之间不是重复的
        // 每个元素，都是一个 Sku 的 attrValueId 集合。这样，通过最外层的 Set ，判断是否有重复的.
        Set<Set<Long>> skuAttrValues = new HashSet<>();
        for (ProductSkuSaveReqVO sku : skus) {
            // 添加失败，说明重复
            if (!skuAttrValues.add(convertSet(sku.getProperties(), ProductSkuSaveReqVO.Property::getValueId))) {
                throw exception(SPU_SKU_NOT_DUPLICATE);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSkuList(Long spuId, List<ProductSkuSaveReqVO> skuCreateReqList) {
        ProductSpuDO spu = productSpuService.getSpu(spuId);
        List<ProductSkuDO> skus = skuCreateReqList.stream().map(skuVO -> {
            ProductSkuDO skuDO = BeanUtils.toBean(skuVO, ProductSkuDO.class);
            skuDO.setSpuId(spuId);
            // 自动生成编码
            if (StrUtil.isBlank(skuDO.getCode())) {
                skuDO.setCode(productCodeService.generateSkuCode(spu.getCategoryId()));
            }
            // 默认普通产品
            if (skuDO.getType() == null) {
                skuDO.setType(ProductSkuTypeEnum.ORDINARY.getType());
            }
            return skuDO;
        }).collect(Collectors.toList());
        productSkuMapper.insertBatch(skus);
        
        // 处理扩展属性
        for (int i = 0; i < skuCreateReqList.size(); i++) {
             saveSkuAttrs(skus.get(i).getId(), skuCreateReqList.get(i));
        }

        // 处理组合产品关联
        for (int i = 0; i < skuCreateReqList.size(); i++) {
            ProductSkuSaveReqVO skuVO = skuCreateReqList.get(i);
            if (ObjectUtil.equal(skuVO.getType(), ProductSkuTypeEnum.COMBO.getType())) {
                saveSkuCombo(skus.get(i).getId(), skuVO);
            }
        }

        // 处理耗材/配件关联 (BOM)
        for (int i = 0; i < skuCreateReqList.size(); i++) {
            saveSkuMaterials(skus.get(i).getId(), skuCreateReqList.get(i));
        }
    }

    @Override
    public List<ProductSkuDO> getSkuListBySpuId(Long spuId) {
        return productSkuMapper.selectListBySpuId(spuId);
    }

    @Override
    public List<ProductSkuDO> getSkuListBySpuId(Collection<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyList();
        }
        return productSkuMapper.selectListBySpuId(spuIds);
    }

    @Override
    public void deleteSkuBySpuId(Long spuId) {
        productSkuMapper.deleteBySpuId(spuId);
    }

    @Override
    public int updateSkuProperty(Long propertyId, String propertyName) {
        // 获取所有的 sku
        List<ProductSkuDO> skuDOList = productSkuMapper.selectList();
        // 处理后需要更新的 sku
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        if (CollUtil.isEmpty(skuDOList)) {
            return 0;
        }
        skuDOList.stream().filter(sku -> sku.getProperties() != null)
                .forEach(sku -> sku.getProperties().forEach(property -> {
                    if (property.getPropertyId().equals(propertyId)) {
                        property.setPropertyName(propertyName);
                        updateSkus.add(sku);
                    }
                }));
        if (CollUtil.isEmpty(updateSkus)) {
            return 0;
        }

        productSkuMapper.updateBatch(updateSkus);
        return updateSkus.size();
    }

    @Override
    public int updateSkuPropertyValue(Long propertyValueId, String propertyValueName) {
        // 获取所有的 sku
        List<ProductSkuDO> skuDOList = productSkuMapper.selectList();
        // 处理后需要更新的 sku
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        if (CollUtil.isEmpty(skuDOList)) {
            return 0;
        }
        skuDOList.stream()
                .filter(sku -> sku.getProperties() != null)
                .forEach(sku -> sku.getProperties().forEach(property -> {
                    if (property.getValueId().equals(propertyValueId)) {
                        property.setValueName(propertyValueName);
                        updateSkus.add(sku);
                    }
                }));
        if (CollUtil.isEmpty(updateSkus)) {
            return 0;
        }

        productSkuMapper.updateBatch(updateSkus);
        return updateSkus.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuList(Long spuId, List<ProductSkuSaveReqVO> skus) {
        ProductSpuDO spu = productSpuService.getSpu(spuId);
        // 构建属性与 SKU 的映射关系;
        Map<String, ProductSkuDO> existsSkuMap = convertMap(productSkuMapper.selectListBySpuId(spuId),
                ProductSkuConvert.INSTANCE::buildPropertyKey, sku -> sku);

        // 拆分三个集合，新插入的、需要更新的、需要删除的
        List<ProductSkuDO> insertSkus = new ArrayList<>();
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        
        for (ProductSkuSaveReqVO skuVO : skus) {
            ProductSkuDO skuDO = BeanUtils.toBean(skuVO, ProductSkuDO.class);
            skuDO.setSpuId(spuId);
            String propertiesKey = ProductSkuConvert.INSTANCE.buildPropertyKey(skuDO);
            ProductSkuDO existsSku = existsSkuMap.remove(propertiesKey);
            
            if (existsSku != null) {
                skuDO.setId(existsSku.getId());
                // 如果旧的有 code 而新的没有，保留旧的 code (或者根据业务决定是否重置)
                if (StrUtil.isBlank(skuDO.getCode())) {
                    skuDO.setCode(existsSku.getCode());
                }
                updateSkus.add(skuDO);
            } else {
                // 新插入，生成 code
                if (StrUtil.isBlank(skuDO.getCode())) {
                    skuDO.setCode(productCodeService.generateSkuCode(spu.getCategoryId()));
                }
                if (skuDO.getType() == null) {
                    skuDO.setType(ProductSkuTypeEnum.ORDINARY.getType());
                }
                insertSkus.add(skuDO);
            }
        }

        // 执行最终的批量操作
        if (CollUtil.isNotEmpty(insertSkus)) {
            productSkuMapper.insertBatch(insertSkus);
            // 处理新插入的组合关联
            for (int i = 0; i < insertSkus.size(); i++) {
                // 这里需要根据 VO 找到对应的 DO，因为 id 是自增生成的
                // 暂时假设 list 顺序一致 (MapStruct/BeanUtils 转换通常保持顺序)
                // 严谨点应该在 VO 里带上标识，或者循环匹配
            }
        }
        if (CollUtil.isNotEmpty(updateSkus)) {
            updateSkus.forEach(sku -> productSkuMapper.updateById(sku));
        }

        // 处理扩展属性 (updateSkus 和 insertSkus)
        for (ProductSkuSaveReqVO skuVO : skus) {
            Long skuId = findSkuId(skuVO, insertSkus, updateSkus);
            if (skuId != null) {
                saveSkuAttrs(skuId, skuVO);
            }
        }
        if (CollUtil.isNotEmpty(existsSkuMap)) {
            productSkuMapper.deleteByIds(convertSet(existsSkuMap.values(), ProductSkuDO::getId));
        }
        
        // 统一处理组合关联更新
        for (ProductSkuSaveReqVO skuVO : skus) {
            // 需要找到对应的 SKU ID
            // 对于 insert 的，MyBatis Plus insertBatch 会回填 ID
            Long skuId = skuVO.getId(); // 如果是更新，VO 里可能有 ID
            if (skuId == null) {
                // 尝试从 insertSkus 中按 code 找 (code 是唯一的)
                for (ProductSkuDO ins : insertSkus) {
                    if (ins.getCode().equals(skuVO.getCode())) {
                        skuId = ins.getId();
                        break;
                    }
                }
            } else {
                // 尝试从 updateSkus 中找
                 for (ProductSkuDO upd : updateSkus) {
                    if (upd.getId().equals(skuId)) {
                        skuId = upd.getId();
                        break;
                    }
                }
            }
            if (skuId != null && ObjectUtil.equal(skuVO.getType(), ProductSkuTypeEnum.COMBO.getType())) {
                saveSkuCombo(skuId, skuVO);
            }
        }
        
        // 统一处理耗材/配件关联更新
        for (ProductSkuSaveReqVO skuVO : skus) {
            Long skuId = findSkuId(skuVO, insertSkus, updateSkus);
            if (skuId != null) {
                saveSkuMaterials(skuId, skuVO);
            }
        }
    }

    private void saveSkuCombo(Long parentSkuId, ProductSkuSaveReqVO skuVO) {
        if (ObjectUtil.notEqual(skuVO.getType(), ProductSkuTypeEnum.COMBO.getType())) {
            // 如果不是组合类型，确保删除可能存在的关联（防止类型切换）
            productSkuComboMapper.deleteByParentSkuId(parentSkuId);
            return;
        }

        List<ProductSkuSaveReqVO.ComboItem> comboItems = skuVO.getComboItems();
        if (comboItems == null) {
            comboItems = new ArrayList<>();
        }

        // 1. 获取现有组合关联
        List<ProductSkuComboDO> existingCombos = productSkuComboMapper.selectListByParentSkuId(parentSkuId);
        Map<Long, ProductSkuComboDO> existingMap = convertMap(existingCombos, ProductSkuComboDO::getChildSkuId);

        // 2. 差异对比
        List<ProductSkuComboDO> insertList = new ArrayList<>();
        List<ProductSkuComboDO> updateList = new ArrayList<>();
        Set<Long> processedChildSkuIds = new HashSet<>();

        for (ProductSkuSaveReqVO.ComboItem item : comboItems) {
            Long childSkuId = item.getId();
            if (childSkuId == null) {
                continue;
            }
            processedChildSkuIds.add(childSkuId);

            ProductSkuComboDO oldCombo = existingMap.get(childSkuId);
            if (oldCombo == null) {
                // 插入
                insertList.add(ProductSkuComboDO.builder()
                        .parentSkuId(parentSkuId)
                        .childSkuId(childSkuId)
                        .quantity(item.getQuantity())
                        .build());
            } else if (!ObjectUtil.equal(oldCombo.getQuantity(), item.getQuantity())) {
                // 更新
                updateList.add(ProductSkuComboDO.builder()
                        .id(oldCombo.getId())
                        .quantity(item.getQuantity())
                        .build());
            }
        }

        // 3. 执行数据库操作
        if (CollUtil.isNotEmpty(insertList)) {
            log.info("[saveSkuCombo][parentSkuId({}) 插入组合关联：{} 个]", parentSkuId, insertList.size());
            productSkuComboMapper.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            log.info("[saveSkuCombo][parentSkuId({}) 更新组合关联：{} 个]", parentSkuId, updateList.size());
            updateList.forEach(c -> productSkuComboMapper.updateById(c));
        }

        // 4. 删除不在新列表中的记录
        List<Long> deleteIds = existingCombos.stream()
                .filter(c -> !processedChildSkuIds.contains(c.getChildSkuId()))
                .map(ProductSkuComboDO::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deleteIds)) {
            log.info("[saveSkuCombo][parentSkuId({}) 删除组合关联：{} 个]", parentSkuId, deleteIds.size());
            productSkuComboMapper.deleteBatchIds(deleteIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO) {
        // 更新 SKU 库存
        updateStockReqDTO.getItems().forEach(item -> {
            if (item.getIncrCount() > 0) {
                productSkuMapper.updateStockIncr(item.getId(), item.getIncrCount());
            } else if (item.getIncrCount() < 0) {
                int updateStockIncr = productSkuMapper.updateStockDecr(item.getId(), item.getIncrCount());
                if (updateStockIncr == 0) {
                    throw exception(SKU_STOCK_NOT_ENOUGH);
                }
            }
        });

        // 更新 SPU 库存
        List<ProductSkuDO> skus = productSkuMapper.selectByIds(
                convertSet(updateStockReqDTO.getItems(), ProductSkuUpdateStockReqDTO.Item::getId));
        Map<Long, Integer> spuStockIncrCounts = ProductSkuConvert.INSTANCE.convertSpuStockMap(
                updateStockReqDTO.getItems(), skus);
        productSpuService.updateSpuStock(spuStockIncrCounts);
    }

    private Long findSkuId(ProductSkuSaveReqVO skuVO, List<ProductSkuDO> insertSkus, List<ProductSkuDO> updateSkus) {
        if (skuVO.getId() != null) {
            return skuVO.getId();
        }
        // 尝试从 insertSkus 中找 (通过 code 匹配，code 是唯一的)
        if (cn.hutool.core.collection.CollUtil.isNotEmpty(insertSkus)) {
            for (ProductSkuDO ins : insertSkus) {
                if (Objects.equals(ins.getCode(), skuVO.getCode())) {
                    return ins.getId();
                }
            }
        }
        return null;
    }

    private void saveSkuAttrs(Long skuId, ProductSkuSaveReqVO reqVO) {
        Map<String, Object> attributes = reqVO.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        // 1. 获取现有属性
        List<ProductSkuAttrDO> existingAttrs = productSkuAttrMapper.selectListBySkuId(skuId);
        Map<String, ProductSkuAttrDO> existingMap = convertMap(existingAttrs, ProductSkuAttrDO::getAttrId);

        // 2. 差异对比并执行
        Set<String> processedKeys = new HashSet<>();
        for (ProductAttrKeyEnum keyEnum : ProductAttrKeyEnum.values()) {
            String key = keyEnum.getKey();
            Object newVal = attributes.get(key);
            processedKeys.add(key);
            
            ProductSkuAttrDO oldAttr = existingMap.get(key);
            if (newVal == null) {
                if (oldAttr != null) {
                    productSkuAttrMapper.deleteById(oldAttr.getId());
                }
                continue;
            }

            String newValJson = JsonUtils.toJsonString(newVal);
            if (oldAttr == null) {
                productSkuAttrMapper.insert(ProductSkuAttrDO.builder()
                        .skuId(skuId)
                        .attrId(key)
                        .attrName(keyEnum.getName())
                        .attrValue(newValJson)
                        .attrClass(keyEnum.getClazz().getName())
                        .build());
            } else if (!ObjectUtil.equal(oldAttr.getAttrValue(), newValJson)) {
                productSkuAttrMapper.updateById(ProductSkuAttrDO.builder()
                        .id(oldAttr.getId())
                        .attrValue(newValJson)
                        .build());
            }
        }
        
        existingAttrs.forEach(old -> {
            if (!processedKeys.contains(old.getAttrId())) {
                productSkuAttrMapper.deleteById(old.getId());
            }
        });
    }

    @Override
    public void hydrateSkuAttrs(ProductSkuRespVO respVO) {
        List<ProductSkuAttrDO> attrs = productSkuAttrMapper.selectListBySkuId(respVO.getId());
        if (CollUtil.isEmpty(attrs)) {
            respVO.setAttributes(new HashMap<>());
            return;
        }
        Map<String, Object> attributeMap = new HashMap<>();
        for (ProductSkuAttrDO attr : attrs) {
            ProductAttrKeyEnum keyEnum = ProductAttrKeyEnum.findByKey(attr.getAttrId());
            if (keyEnum != null) {
                attributeMap.put(attr.getAttrId(), JsonUtils.parseObject(attr.getAttrValue(), keyEnum.getClazz()));
            } else {
                attributeMap.put(attr.getAttrId(), attr.getAttrValue());
            }
        }
        respVO.setAttributes(attributeMap);
    }

    private void saveSkuMaterials(Long skuId, ProductSkuSaveReqVO skuVO) {
        List<ProductSkuSaveReqVO.MaterialItem> materialItems = skuVO.getMaterialItems();
        if (materialItems == null) {
            materialItems = new ArrayList<>();
        }

        // 1. 获取现有材料关联
        List<ErpProductMaterialDO> existingMaterials = erpProductMaterialMapper.selectListBySkuId(skuId);
        Map<Long, ErpProductMaterialDO> existingMap = convertMap(existingMaterials, ErpProductMaterialDO::getMaterialId);

        // 2. 差异对比
        List<ErpProductMaterialDO> insertList = new ArrayList<>();
        List<ErpProductMaterialDO> updateList = new ArrayList<>();
        Set<Long> processedMaterialIds = new HashSet<>();

        for (ProductSkuSaveReqVO.MaterialItem item : materialItems) {
            Long materialId = item.getMaterialId();
            if (materialId == null) {
                continue;
            }
            processedMaterialIds.add(materialId);

            ErpProductMaterialDO oldMaterial = existingMap.get(materialId);
            if (oldMaterial == null) {
                // 插入
                insertList.add(new ErpProductMaterialDO()
                        .setSkuId(skuId)
                        .setMaterialId(materialId)
                        .setUsageQuantity(item.getUsageQuantity()));
            } else if (!ObjectUtil.equal(oldMaterial.getUsageQuantity(), item.getUsageQuantity())) {
                // 更新
                updateList.add(new ErpProductMaterialDO()
                        .setId(oldMaterial.getId())
                        .setUsageQuantity(item.getUsageQuantity()));
            }
        }

        // 3. 执行数据库操作
        if (CollUtil.isNotEmpty(insertList)) {
            log.info("[saveSkuMaterials][skuId({}) 插入耗材关联：{} 个]", skuId, insertList.size());
            erpProductMaterialMapper.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            log.info("[saveSkuMaterials][skuId({}) 更新耗材关联：{} 个]", skuId, updateList.size());
            updateList.forEach(m -> erpProductMaterialMapper.updateById(m));
        }

        // 4. 删除不在新列表中的记录
        List<Long> deleteIds = existingMaterials.stream()
                .filter(m -> !processedMaterialIds.contains(m.getMaterialId()))
                .map(ErpProductMaterialDO::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deleteIds)) {
            log.info("[saveSkuMaterials][skuId({}) 删除耗材关联：{} 个]", skuId, deleteIds.size());
            erpProductMaterialMapper.deleteBatchIds(deleteIds);
        }
    }

    @Override
    public void hydrateSkuMaterials(ProductSkuRespVO respVO) {
        List<ErpProductMaterialDO> materials = erpProductMaterialMapper.selectListBySkuId(respVO.getId());
        if (CollUtil.isEmpty(materials)) {
            respVO.setMaterialItems(new ArrayList<>());
            return;
        }

        // 获取耗材详细信息
        Set<Long> materialIds = convertSet(materials, ErpProductMaterialDO::getMaterialId);
        Map<Long, ErpMaterialDO> materialMap = convertMap(erpMaterialMapper.selectBatchIds(materialIds), ErpMaterialDO::getId);

        respVO.setMaterialItems(materials.stream().map(m -> {
            ProductSkuRespVO.MaterialItem item = new ProductSkuRespVO.MaterialItem();
            item.setMaterialId(m.getMaterialId());
            item.setUsageQuantity(m.getUsageQuantity());
            
            ErpMaterialDO materialDO = materialMap.get(m.getMaterialId());
            if (materialDO != null) {
                item.setMaterialName(materialDO.getName());
                item.setMaterialCode(materialDO.getCode());
                // 解析单位名称
                if (materialDO.getUnit() != null) {
                    Optional.ofNullable(unitService.getProductUnit(materialDO.getUnit()))
                            .ifPresent(u -> item.setMaterialUnit(u.getName()));
                }
            }
            return item;
        }).collect(Collectors.toList()));
    }

    @Override
    public void hydrateSkuCombo(ProductSkuRespVO respVO) {
        // 普通商品不查组合表
        if (ObjectUtil.notEqual(respVO.getType(), ProductSkuTypeEnum.COMBO.getType())) {
            respVO.setComboItems(new ArrayList<>());
            return;
        }
        
        List<ProductSkuComboDO> combos = productSkuComboMapper.selectListByParentSkuId(respVO.getId());
        if (CollUtil.isEmpty(combos)) {
            respVO.setComboItems(new ArrayList<>());
            return;
        }

        // 获取子 SKU 详细信息
        Set<Long> childSkuIds = convertSet(combos, ProductSkuComboDO::getChildSkuId);
        List<ProductSkuDO> childSkus = productSkuMapper.selectBatchIds(childSkuIds);
        Map<Long, ProductSkuDO> skuMap = convertMap(childSkus, ProductSkuDO::getId);

        // 获取对应的 SPU 名称 (SKU DO 本身不带 name，需要从 SPU 获取)
        Set<Long> spuIds = convertSet(childSkus, ProductSkuDO::getSpuId);
        Map<Long, ProductSpuDO> spuMap = productSpuService.getSpuMap(spuIds);

        respVO.setComboItems(combos.stream().map(c -> {
            ProductSkuRespVO.ComboItem item = new ProductSkuRespVO.ComboItem();
            item.setId(c.getChildSkuId());
            item.setQuantity(c.getQuantity());
            
            ProductSkuDO childSku = skuMap.get(c.getChildSkuId());
            if (childSku != null) {
                item.setCode(childSku.getCode());
                item.setPicUrl(childSku.getPicUrl());
                // 填充名称
                ProductSpuDO spu = spuMap.get(childSku.getSpuId());
                if (spu != null) {
                    item.setName(spu.getName());
                }
            }
            return item;
        }).collect(Collectors.toList()));
    }

}
