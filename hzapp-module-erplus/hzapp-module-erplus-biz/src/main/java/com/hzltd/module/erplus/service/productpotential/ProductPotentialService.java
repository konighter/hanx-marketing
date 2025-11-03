package com.hzltd.module.erplus.service.productpotential;

import javax.validation.*;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productpotential.ProductPotentialDO;
import com.hzltd.framework.common.pojo.PageResult;

/**
 * 选品提案 Service 接口
 *
 * @author 翰展科技
 */
public interface ProductPotentialService {

    /**
     * 创建选品提案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createProductPotential(@Valid ProductPotentialSaveReqVO createReqVO);

    /**
     * 更新选品提案
     *
     * @param updateReqVO 更新信息
     */
    void updateProductPotential(@Valid ProductPotentialSaveReqVO updateReqVO);

    /**
     * 简单更新
     * @param simpleReqVO
     */
    void updateProductPotential(@Valid ProductPotentialSimpleReqVO simpleReqVO);

    /**
     * 删除选品提案
     *
     * @param id 编号
     */
    void deleteProductPotential(Integer id);

    /**
     * 获得选品提案
     *
     * @param id 编号
     * @return 选品提案
     */
    ProductPotentialDO getProductPotentialSimple(Integer id);

     /**
     * 获得选品提案
     *
     * @param id 编号
     * @return 选品提案
     */
     ProductPotentialRespVO getProductPotential(Integer id);


    /**
     * 获得选品提案分页
     *
     * @param pageReqVO 分页查询
     * @return 选品提案分页
     */
    PageResult<ProductPotentialDO> getProductPotentialPage(ProductPotentialPageReqVO pageReqVO);

}