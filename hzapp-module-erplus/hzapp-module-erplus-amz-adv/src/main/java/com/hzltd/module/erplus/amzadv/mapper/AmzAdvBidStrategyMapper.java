package com.hzltd.module.erplus.amzadv.mapper;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvBidStrategyPageReqVO;
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