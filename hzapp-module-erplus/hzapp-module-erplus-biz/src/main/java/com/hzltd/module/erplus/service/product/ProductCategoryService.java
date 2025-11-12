package com.hzltd.module.erplus.service.product;

import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategoryListReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategorySaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * ERP 产品分类 Service 接口
 *
 * @author 翰展科技
 */
public interface ProductCategoryService {

    /**
     * 创建产品分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductCategory(@Valid ProductCategorySaveReqVO createReqVO);

    /**
     * 更新产品分类
     *
     * @param updateReqVO 更新信息
     */
    void updateProductCategory(@Valid ProductCategorySaveReqVO updateReqVO);

    /**
     * 删除产品分类
     *
     * @param id 编号
     */
    void deleteProductCategory(Long id);

    /**
     * 获得产品分类
     *
     * @param id 编号
     * @return 产品分类
     */
    ProductCategoryDO getProductCategory(Long id);

    /**
     * 获得产品分类列表
     *
     * @param listReqVO 查询条件
     * @return 产品分类列表
     */
    List<ProductCategoryDO> getProductCategoryList(ProductCategoryListReqVO listReqVO);

    /**
     * 获得产品分类列表
     *
     * @param ids 编号数组
     * @return 产品分类列表
     */
    List<ProductCategoryDO> getProductCategoryList(Collection<Long> ids);

    /**
     * 获得产品分类 Map
     *
     * @param ids 编号数组
     * @return 产品分类 Map
     */
    default Map<Long, ProductCategoryDO> getProductCategoryMap(Collection<Long> ids) {
        return convertMap(getProductCategoryList(ids), ProductCategoryDO::getId);
    }

    /**
     * 校验分类是否存在
     *
     * @param id 分类编号
     */
    void validateCategory(Long id);

    /**
     * 获得分类层级
     *
     * @param id 分类编号
     * @return 分类层级
     */
    int getCategoryLevel(Long id);
}