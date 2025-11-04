package com.hzltd.module.erplus.service.spu;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimBatchReqVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimPageReqVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimRespVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductClaimSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductClaimDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 商品认领 Service 接口
 *
 * @author hzadd
 */
public interface ProductClaimService {

    /**
     * 创建商品认领
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createProductClaim(@Valid ProductClaimSaveReqVO createReqVO);

    /**
     * 创建商品认领
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    List<Integer> batchCreateProductClaim(@Valid ProductClaimBatchReqVO createReqVO);



    /**
     * 更新商品认领
     *
     * @param updateReqVO 更新信息
     */
    void updateProductClaim(@Valid ProductClaimSaveReqVO updateReqVO);

    /**
     * 删除商品认领
     *
     * @param id 编号
     */
    void deleteProductClaim(Integer id);

    /**
     * 获得商品认领
     *
     * @param id 编号
     * @return 商品认领
     */
    ProductClaimRespVO getProductClaim(Integer id);


    /**
     * 获得商品认领
     *
     * @param id 编号
     * @return 商品认领
     */
    ProductClaimDO getProductClaimDetail(Integer id);


    /**
     * 获得商品认领
     *
     * @param ids 编号
     * @return 商品认领
     */
    List<ProductClaimDO> getProductClaimBatch(List<Integer> ids);
    /**
     * 获得商品认领分页
     *
     * @param pageReqVO 分页查询
     * @return 商品认领分页
     */
    PageResult<ProductClaimRespVO> getProductClaimPage(ProductClaimPageReqVO pageReqVO);


    /**
     * 同步商品到平台
     * @param productClaim
     */
    void syncProductClaim(ProductClaimDO productClaim);

    /**
     * 同步商品到平台
     * @param productClaims
     */
    void syncProductClaimBatch(List<ProductClaimDO> productClaims);
}