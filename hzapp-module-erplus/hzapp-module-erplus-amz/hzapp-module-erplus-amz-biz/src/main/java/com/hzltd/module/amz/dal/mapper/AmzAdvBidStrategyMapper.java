package com.hzltd.module.amz.dal.mapper;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvBidStrategyPageReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 亚马逊广告出价策略 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzAdvBidStrategyMapper extends BaseMapperX<AmzAdvBidStrategyDO> {

    /**
     * 分页查询出价策略
     *
     * @param pageReqVO 分页请求参数
     * @return 出价策略分页结果
     */
    PageResult<AmzAdvBidStrategyDO> selectPage(AmzAdvBidStrategyPageReqVO pageReqVO);

    /**
     * 根据店铺ID和策略ID查询
     *
     * @param shopId      店铺ID
     * @param id          策略ID
     * @return 出价策略
     */
    AmzAdvBidStrategyDO selectByShopIdAndId(String shopId, Long id);
}