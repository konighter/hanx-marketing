package com.hzltd.module.erplus.service.amz.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.quartz.core.handler.JobHandler;
import com.hzltd.framework.tenant.core.job.TenantJob;
import com.hzltd.module.amz.spapi.api.AmazonCategoryService;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryAttributeMapper;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryMapper;
import com.hzltd.module.erplus.dal.mysql.shop.ShopMapper;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.spapi.model.category.CategoryModel;
import com.hzltd.module.erplus.spapi.model.category.GetCategoryAttributeRequest;
import com.hzltd.module.erplus.spapi.model.category.GetCategoryRequest;
import com.hzltd.module.erplus.spapi.model.category.MetaCategorySchemaResult;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Amazon Schema Sync Task (Daily)
 * Refreshes Amazon product type definitions from SP-API.
 */
@Slf4j
@Component
public class AmazonSchemaSyncTask implements JobHandler {

    @Resource
    private CrossMetaCategoryMapper categoryMapper;

    @Resource
    private CrossMetaCategoryAttributeMapper attributeMapper;

    @Resource
    private AmazonCategoryService amazonCategoryService;

    @Resource
    private ShopMapper shopMapper;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        syncAmazonSchemas(param);
        return "";
    }

    //    @Scheduled(cron = "0 0 3 * * ?")
    public void syncAmazonSchemas(String param) {
        log.info("[syncAmazonSchemas] Starting sync...");


        ShopDO shop = shopMapper.selectOne(new LambdaQueryWrapper<ShopDO>()
                .eq(ShopDO::getPlatform, CrossPlatformEnum.AMAZON.getValue())
                .eq(ShopDO::getStatus, 0)
                .last("LIMIT 1"));

        if (shop == null) {
            log.error("[syncAmazonSchemas] No authorized Amazon shop found.");
            return;
        }

        Map<String,String> paramMap = Maps.newHashMap();
        if (StringUtils.isNotEmpty(param)) {
            paramMap = JsonUtils.parseObject(param, new TypeReference<Map<String, String>>() {});
        }

        // 只同步一个品类
        if (StringUtils.isNotEmpty(paramMap.get("categoryId"))) {
            CrossMetaCategoryDO metaCategory =  categoryMapper.selectById(Long.valueOf(paramMap.get("categoryId")));
            if (metaCategory != null) {
                syncCategorySchema(metaCategory, shop);
                return;
            }
        }

        // 同步shop下全部品类

        // 1. Fetch categories from SP-API to update local meta categories
        syncCategoriesFromApi(shop);

        // 2. Sync attributes for each category in local DB
        List<CrossMetaCategoryDO> categories = categoryMapper.selectList(new LambdaQueryWrapper<CrossMetaCategoryDO>()
                .eq(CrossMetaCategoryDO::getPlatformId, CrossPlatformEnum.AMAZON.getValue()));

        for (CrossMetaCategoryDO category : categories) {
            try {
                syncCategorySchema(category, shop);
            } catch (Exception e) {
                log.error("[syncAmazonSchemas] Failed category: {}", category.getCategoryCode(), e);
            }
        }
    }

    private void syncCategoriesFromApi(ShopDO shop) {
        ApiRequest<GetCategoryRequest> request = new ApiRequest<>();
        request.setShopId(String.valueOf(shop.getId()));
        GetCategoryRequest catReq = new GetCategoryRequest();
        catReq.setName(""); // Empty string might fetch default or common ones depending on SP-API behavior
        catReq.setShopId(shop.getId().longValue());
        request.setRequest(catReq);

        ApiResponse<List<CategoryModel>> response = amazonCategoryService.getCategories(request);
        if (response != null && response.getData() != null) {
            for (CategoryModel catModel : response.getData()) {
                saveCategory(catModel);
            }
        }
    }

    private void saveCategory(CategoryModel catModel) {
        CrossMetaCategoryDO categoryDO = categoryMapper.selectOne(new LambdaQueryWrapper<CrossMetaCategoryDO>()
                .eq(CrossMetaCategoryDO::getPlatformId, CrossPlatformEnum.AMAZON.getValue())
                .eq(CrossMetaCategoryDO::getCategoryCode, catModel.getCategoryId()));

        boolean isNew = (categoryDO == null);
        if (isNew) {
            categoryDO = new CrossMetaCategoryDO();
            categoryDO.setPlatformId(CrossPlatformEnum.AMAZON.getValue());
            categoryDO.setCategoryCode(catModel.getCategoryId());
            categoryDO.setLevel(1);
            categoryDO.setLeaf(true);
        }

        categoryDO.setCategoryName(catModel.getName());
        categoryDO.setExtra(null);
        categoryDO.setUpdateTime(LocalDateTime.now());

        if (isNew) categoryMapper.insert(categoryDO);
        else categoryMapper.updateById(categoryDO);
    }

    private void syncCategorySchema(CrossMetaCategoryDO category, ShopDO shop) {
        ApiRequest<GetCategoryAttributeRequest> request = new ApiRequest<>();
        request.setShopId(String.valueOf(shop.getId()));
        GetCategoryAttributeRequest attrReq = new GetCategoryAttributeRequest();
        attrReq.setCategoryId(category.getCategoryCode());
        request.setRequest(attrReq);

        ApiResponse<MetaCategorySchemaResult> response = amazonCategoryService.getCategoryAttributes(request);
        if (response != null && response.getData() != null) {
            MetaCategorySchemaResult result = response.getData();

            // 1. Save full category schema (for if-then logic and full validation)
            if (result.getFullSchema() != null) {
                category.setExtra(result.getFullSchema());
                categoryMapper.updateById(category);
            }

            // 2. Save individual attribute fragments
            if (result.getAttributes() != null) {
                for (CategoryAttributeModel attrModel : result.getAttributes()) {
                    saveAttribute(category.getCategoryCode(), attrModel);
                }
            }

//            // 3. Ensure a '$root' entry still exists for backward compatibility if needed,
//            // or just rely on the category.extra field.
//            if (result.getFullSchema() != null) {
//                CategoryAttributeModel rootModel = new CategoryAttributeModel();
//                rootModel.setAttrCode("$root");
//                rootModel.setAttrName("Root JSON Schema");
//                rootModel.setFieldType("object");
//                rootModel.setExtra(result.getFullSchema());
//                saveAttribute(category.getCategoryCode(), rootModel);
//            }
        }
    }

    private void saveAttribute(String categoryCode, CategoryAttributeModel attrModel) {
        CrossMetaCategoryAttributeDO attributeDO = attributeMapper.selectOne(new LambdaQueryWrapper<CrossMetaCategoryAttributeDO>()
                .eq(CrossMetaCategoryAttributeDO::getPlatformId, CrossPlatformEnum.AMAZON.getValue())
                .eq(CrossMetaCategoryAttributeDO::getCategoryCode, categoryCode)
                .eq(CrossMetaCategoryAttributeDO::getAttrCode, attrModel.getAttrCode()));

        boolean isNew = (attributeDO == null);
        if (isNew) {
            attributeDO = new CrossMetaCategoryAttributeDO();
            attributeDO.setPlatformId(CrossPlatformEnum.AMAZON.getValue());
            attributeDO.setCategoryCode(categoryCode);
            attributeDO.setAttrCode(attrModel.getAttrCode());
        }

        attributeDO.setAttrName(attrModel.getAttrName());
        attributeDO.setFieldType(attrModel.getFieldType());
        attributeDO.setExtra(attrModel.getExtra());

        if (isNew) attributeMapper.insert(attributeDO);
        else attributeMapper.updateById(attributeDO);
    }
}
