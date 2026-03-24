package com.hzltd.module.amz.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvBidStrategyPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvBidStrategySaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;

/**
 * 亚马逊广告出价策略 Service 接口
 *
 * @author 翰展科技
 */
public interface AmzAdvBidStrategyService {

    /**
     * 创建出价策略
     *
     * @param createReqVO 创建请求参数
     * @return 出价策略ID
     */
    Long createAmzAdvBidStrategy(AmzAdvBidStrategySaveReqVO createReqVO);

    /**
     * 更新出价策略
     *
     * @param updateReqVO 更新请求参数
     */
    void updateAmzAdvBidStrategy(AmzAdvBidStrategySaveReqVO updateReqVO);

    /**
     * 删除出价策略
     *
     * @param id 出价策略ID
     */
    void deleteAmzAdvBidStrategy(Long id);

    /**
     * 获取出价策略
     *
     * @param id 出价策略ID
     * @return 出价策略
     */
    AmzAdvBidStrategyDO getAmzAdvBidStrategy(Long id);

    /**
     * 分页查询出价策略
     *
     * @param pageReqVO 分页请求参数
     * @return 出价策略分页结果
     */
    PageResult<AmzAdvBidStrategyDO> getAmzAdvBidStrategyPage(AmzAdvBidStrategyPageReqVO pageReqVO);

    /**
     * 根据店铺ID和策略ID查询
     *
     * @param shopId      店铺ID
     * @param id          策略ID
     * @return 出价策略
     */
    AmzAdvBidStrategyDO getByShopIdAndId(String shopId, Long id);
}