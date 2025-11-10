package com.hzltd.module.erplus.service.productassets;

import java.util.*;
import jakarta.validation.*;
import com.hzltd.module.erplus.controller.admin.productassets.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productassets.ProductAssetsDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.PageParam;

/**
 * 商品素材 Service 接口
 *
 * @author 翰展科技
 */
public interface ProductAssetsService {

    /**
     * 创建商品素材
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createProductAssets(@Valid ProductAssetsSaveReqVO createReqVO);

    /**
     * 更新商品素材
     *
     * @param updateReqVO 更新信息
     */
    void updateProductAssets(@Valid ProductAssetsSaveReqVO updateReqVO);

    /**
     * 删除商品素材
     *
     * @param id 编号
     */
    void deleteProductAssets(Integer id);

    /**
    * 批量删除商品素材
    *
    * @param ids 编号
    */
    void deleteProductAssetsListByIds(List<Integer> ids);

    /**
     * 获得商品素材
     *
     * @param id 编号
     * @return 商品素材
     */
    ProductAssetsDO getProductAssets(Integer id);

    /**
     * 获得商品素材分页
     *
     * @param pageReqVO 分页查询
     * @return 商品素材分页
     */
    PageResult<ProductAssetsDO> getProductAssetsPage(ProductAssetsPageReqVO pageReqVO);

}