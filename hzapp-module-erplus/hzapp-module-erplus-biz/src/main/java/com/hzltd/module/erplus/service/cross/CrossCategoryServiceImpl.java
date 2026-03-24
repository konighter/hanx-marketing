package com.hzltd.module.erplus.service.cross;

import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.CategoryApiFactory;
import com.hzltd.module.erplus.api.service.CategoryAttributeApiFactory;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryAttrReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryReqVO;
import com.hzltd.module.erplus.convert.category.CrossCategoryConvert;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryAttributeMapper;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryMapper;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.spapi.model.category.CategoryModel;
import com.hzltd.module.spapi.service.category.CategoryApi;
import com.hzltd.module.spapi.service.category.CategoryAttributeMappingApi;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.system.service.SystemProductService;
import com.hzltd.module.spapi.model.system.ProductSpuModel;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.CATEGORY_NOT_EMPTY;
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.PRODUCT_NOT_EXISTS;

@Service
public class CrossCategoryServiceImpl implements CrossCategoryService {
    @Resource
    private CategoryApiFactory categoryApiFactory;

    @Resource
    private CategoryAttributeApiFactory categoryAttributeApiFactory;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private CrossMetaCategoryMapper crossMetaCategoryMapper;

    @Resource
    private CrossMetaCategoryAttributeMapper crossMetaCategoryAttributeMapper;

    @Resource
    private SystemProductService productSpuService;

    @Override
    public List<CategoryVO> getCrossCategoryList(CrossCategoryReqVO reqVO) {
        SellPlatformDO sellPlatformDO = sellPlatformService.getSellPlatform(reqVO.getPlatformId());
//        if (StringUtils.isEmpty(reqVO.getName()) && CrossPlatformEnum.AMAZON.equals(CrossPlatformEnum.of(sellPlatformDO.getCode()))) {
//            return Collections.emptyList();
//        }
        List<CrossMetaCategoryDO> crossMetaCategoryDOList = crossMetaCategoryMapper.selectList(
                new LambdaQueryWrapperX<CrossMetaCategoryDO>()
                        .eqIfPresent(CrossMetaCategoryDO::getPlatformId, reqVO.getPlatformId())
        );
        if (CollectionUtils.isNotEmpty(crossMetaCategoryDOList)) {
            return crossMetaCategoryDOList.stream().map(CrossCategoryConvert.INSTANCE::toCategoryVO).collect(Collectors.toList());
        }

        CategoryApi categoryApi = categoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatformDO.getCode()));
        ApiResponse<List<CategoryModel>> apiResponse = categoryApi.getCategories(ApiRequest.of(reqVO, CrossCategoryConvert.INSTANCE.toGetCategoryRequest(reqVO)));
        if (!apiResponse.success()) {
            throw new ServiceException(apiResponse.getCode(), apiResponse.getMsg());
        }

        if (CollectionUtils.isNotEmpty(apiResponse.getData())) {
            // todo-- check And Insert (按有效期), 异步更新
            crossMetaCategoryMapper.insertBatch(apiResponse.getData().stream().map(metaCategory -> {
                CrossMetaCategoryDO categoryDO = CrossCategoryConvert.INSTANCE.toMetaCategoryDO(metaCategory);
                categoryDO.setPlatformId(reqVO.getPlatformId());
                return categoryDO;
            }).collect(Collectors.toList()));
        }

        return apiResponse.getData().stream().map(CrossCategoryConvert.INSTANCE::toCategoryVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryAttributeVO> getCrossAttributeByCategory(CrossCategoryAttrReqVO reqVO) {
        if (StringUtils.isEmpty(reqVO.getCategoryId()) && CollectionUtils.isEmpty(reqVO.getCategoryIds())) {
            throw exception(CATEGORY_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(reqVO.getCategoryId())) {
            Integer index = Math.max(0, reqVO.getCategoryIds().size() - 1);
            reqVO.setCategoryId(reqVO.getCategoryIds().get(index));
        }
        List<CrossMetaCategoryAttributeDO> crossMetaCategoryAttributeDOList = crossMetaCategoryAttributeMapper.selectList(
                new LambdaQueryWrapperX<CrossMetaCategoryAttributeDO>()
                        .eq(CrossMetaCategoryAttributeDO::getCategoryCode, reqVO.getCategoryId())
                        .eq(CrossMetaCategoryAttributeDO::getPlatformId, reqVO.getPlatformId())
        );

        if (CollectionUtils.isNotEmpty(crossMetaCategoryAttributeDOList)) {
            return crossMetaCategoryAttributeDOList.stream().map(CrossCategoryConvert.INSTANCE::toCategoryAttributeVO).collect(Collectors.toList());
        }

        SellPlatformDO sellPlatformDO = sellPlatformService.getSellPlatform(reqVO.getPlatformId());
        CategoryApi categoryApi = categoryApiFactory.getCrossApiService(CrossPlatformEnum.of(sellPlatformDO.getCode()));
        ApiResponse<List<CategoryAttributeModel>> apiResponse = categoryApi.getCategoryAttributes(ApiRequest.of(reqVO, CrossCategoryConvert.INSTANCE.toGetCategoryAttributeRequest(reqVO)));
        if (!apiResponse.success()) {
            throw new ServiceException(apiResponse.getCode(), apiResponse.getMsg());
        }

        if (CollectionUtils.isNotEmpty(apiResponse.getData())) {
            // todo-- check And Insert (按有效期), 异步更新
            crossMetaCategoryAttributeMapper.insertBatch(apiResponse.getData().stream().map(attr -> {
                CrossMetaCategoryAttributeDO attributeDO = CrossCategoryConvert.INSTANCE.toCategoryAttributeDO(attr);
                attributeDO.setPlatformId(reqVO.getPlatformId());
                attributeDO.setCategoryCode(reqVO.getCategoryId());
                return attributeDO;
            }).collect(Collectors.toList()));
        }

        return apiResponse.getData().stream().map(CrossCategoryConvert.INSTANCE::toCategoryAttrVO).collect(Collectors.toList());
    }


    @Override
    public List<CategoryAttributeVO> renderCategoryAttribute(CrossCategoryAttrReqVO crossCategoryAttrReqVO) {
        SellPlatformDO platformDO = sellPlatformService.getSellPlatform(crossCategoryAttrReqVO.getPlatformId());
        List<CategoryAttributeVO> categoryAttributeVOS = getCrossAttributeByCategory(crossCategoryAttrReqVO);
        ProductSpuModel spu = productSpuService.getProductSpu(crossCategoryAttrReqVO.getSpuId());
        if (spu == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
        spu.setCrossCategory(crossCategoryAttrReqVO.getCategoryId());

       CategoryAttributeMappingApi categoryAttributeApi = categoryAttributeApiFactory.getCrossApiService(CrossPlatformEnum.of(platformDO.getCode()));
       categoryAttributeApi.mapCategoryAttributeValues(categoryAttributeVOS, spu);

        return categoryAttributeVOS;
    }
}
