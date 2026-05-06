package com.hzltd.module.erplus.service.shop;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.controller.admin.shop.vo.*;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 店铺信息 Service 接口
 *
 * @author hzadd
 */
public interface ShopService {

    /**
     * 创建店铺信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createShop(@Valid ShopSaveReqVO createReqVO);

    /**
     * 更新店铺信息
     *
     * @param updateReqVO 更新信息
     */
    void updateShop(@Valid ShopSaveReqVO updateReqVO);

    /**
     * 删除店铺信息
     *
     * @param id 编号
     */
    void deleteShop(Integer id);

    /**
     * 获得店铺信息
     *
     * @param id 编号
     * @return 店铺信息
     */
    ShopDO getShop(Integer id);

    ShopRespVO getShopResp(Integer id);

    List<ShopDO> getShopsByIds(List<Integer> ids);

    /**
     * 获得店铺信息分页
     *
     * @param pageReqVO 分页查询
     * @return 店铺信息分页
     */
    PageResult<ShopRespVO> getShopPage(ShopPageReqVO pageReqVO);

    List<ShopRespVO> getShopList(ShopReqVO pageReqVO);

    List<ShopDO> getShopListByPlatform(Integer platformId);

     /**
      * 获得店铺级联信息
      * 平台 -> 店铺-> 子店铺
      *
      * @return 店铺级联信息
      */
    List<CascaderShopRespVO> getCascaderShopList();




}