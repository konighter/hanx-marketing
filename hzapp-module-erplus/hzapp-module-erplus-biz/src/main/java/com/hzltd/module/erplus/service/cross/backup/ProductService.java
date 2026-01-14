package com.hzltd.module.erplus.service.cross.backup;

import com.hzltd.module.erplus.controller.admin.product.vo.product.ProductSaveReqVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * ERP 产品 Service 接口
 *
 * @author 翰展科技
 */
public interface ProductService {

    /**
     * 创建产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProduct(@Valid ProductSaveReqVO createReqVO);

    /**
     * 更新产品
     *
     * @param updateReqVO 更新信息
     */
    void updateProduct(@Valid ProductSaveReqVO updateReqVO);

    /**
     * 删除产品
     *
     * @param id 编号
     */
    void deleteProduct(Long id);

    /**
     * 校验产品们的有效性
     *
     * @param ids 编号数组
     * @return 产品列表
     */
    List<ProductSpuDO> validProductList(Collection<Long> ids);

    /**
     * 获得产品
     *
     * @param id 编号
     * @return 产品
     */
    ProductSpuDO getProduct(Long id);

    /**
     * 获得指定状态的产品 VO 列表
     *
     * @param status 状态
     * @return 产品 VO 列表
     */
    List<ProductSpuRespVO> getProductVOListByStatus(Integer status);

    /**
     * 获得产品 VO 列表
     *
     * @param ids 编号数组
     * @return 产品 VO 列表
     */
    List<ProductSpuRespVO> getProductVOList(Collection<Long> ids);

    /**
     * 获得产品 VO Map
     *
     * @param ids 编号数组
     * @return 产品 VO Map
     */
    default Map<Long, ProductSpuRespVO> getProductVOMap(Collection<Long> ids) {
        return convertMap(getProductVOList(ids), ProductSpuRespVO::getId);
    }


    /**
     * 基于产品分类编号，获得产品数量
     *
     * @param categoryId 产品分类编号
     * @return 产品数量
     */
    Long getProductCountByCategoryId(Long categoryId);


}