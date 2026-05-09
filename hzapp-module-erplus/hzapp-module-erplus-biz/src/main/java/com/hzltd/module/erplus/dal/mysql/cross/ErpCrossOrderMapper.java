package com.hzltd.module.erplus.dal.mysql.cross;

import cn.hutool.core.util.StrUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderMapper extends BaseMapperX<CrossOrderDO> {

    default PageResult<CrossOrderDO> selectPage(CrossOrderPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrossOrderDO>()
                .eqIfPresent(CrossOrderDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(CrossOrderDO::getShopId, reqVO.getShopId())
                .eqIfPresent(CrossOrderDO::getMarketId, reqVO.getMarketId())
                .eqIfPresent(CrossOrderDO::getFulfillType, reqVO.getFulfillType())
                .eqIfPresent(CrossOrderDO::getPlatformOrderId, reqVO.getPlatformOrderId())
                .eqIfPresent(CrossOrderDO::getStatus, reqVO.getStatus())
                .inIfPresent(CrossOrderDO::getId, reqVO.getOrderIds())
                .betweenIfPresent(CrossOrderDO::getOrderTime, reqVO.getOrderTimeStart(), reqVO.getOrderTimeEnd())
                .betweenIfPresent(CrossOrderDO::getUpdateTime, reqVO.getUpdateTimeRange())
                .apply(StrUtil.isNotEmpty(reqVO.getSellerSkuCode()),
                        "id IN (SELECT order_id FROM erplus_cross_order_item WHERE seller_sku_code LIKE CONCAT('%', {0}, '%'))",
                        reqVO.getSellerSkuCode())
                .apply(StrUtil.isNotEmpty(reqVO.getPlatformProductCode()),
                        "id IN (SELECT order_id FROM erplus_cross_order_item WHERE platform_product_code LIKE CONCAT('%', {0}, '%'))",
                        reqVO.getPlatformProductCode())
                .apply(StrUtil.isNotEmpty(reqVO.getTitle()),
                        "id IN (SELECT order_id FROM erplus_cross_order_item WHERE title LIKE CONCAT('%', {0}, '%'))",
                        reqVO.getTitle())
                .orderByDesc(CrossOrderDO::getOrderTime));
    }

}