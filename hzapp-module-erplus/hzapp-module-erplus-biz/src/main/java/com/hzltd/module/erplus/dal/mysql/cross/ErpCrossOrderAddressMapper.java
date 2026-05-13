package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderPageRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderAddressDO;
import com.hzltd.module.erplus.controller.admin.cross.vo.CrossOrderStateStatsRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 跨境订单收货地址 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderAddressMapper extends BaseMapperX<CrossOrderAddressDO> {

    default CrossOrderAddressDO selectByOrderId(Long orderId) {
        return selectOne(CrossOrderAddressDO::getOrderId, orderId);
    }

    default CrossOrderAddressDO selectByPlatformOrderId(String platformOrderId) {
        return selectOne(CrossOrderAddressDO::getPlatformOrderId, platformOrderId);
    }

    @Select("<script>" +
            "SELECT t2.state_or_region as stateOrRegion, t3.country_code as countryCode, COUNT(DISTINCT t1.id) as count " +
            "FROM erplus_cross_order t1 " +
            "JOIN erplus_cross_order_address t2 ON t1.id = t2.order_id " +
            "JOIN erplus_platform_shop t3 ON t1.shop_id = t3.id " +
            "<if test='platformProductCode != null and platformProductCode != \"\"'>" +
            "JOIN erplus_cross_order_item t4 ON t1.id = t4.order_id " +
            "</if>" +
            "WHERE t1.shop_id = #{shopId} " +
            "<if test='orderTimeStart != null'> AND t1.order_time &gt;= #{orderTimeStart} </if>" +
            "<if test='orderTimeEnd != null'> AND t1.order_time &lt;= #{orderTimeEnd} </if>" +
            "<if test='platformProductCode != null and platformProductCode != \"\"'>" +
            "AND t4.platform_product_code = #{platformProductCode} " +
            "</if>" +
            "GROUP BY t2.state_or_region, t3.country_code" +
            "</script>")
    List<CrossOrderStateStatsRespVO> selectStateStats(CrossOrderPageRequest request);

}
