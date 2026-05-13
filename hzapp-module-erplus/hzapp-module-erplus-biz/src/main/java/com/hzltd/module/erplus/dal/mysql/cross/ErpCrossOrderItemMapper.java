package com.hzltd.module.erplus.dal.mysql.cross;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossOrderItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 订单项 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpCrossOrderItemMapper extends BaseMapperX<CrossOrderItemDO> {

    @Select("<script>" +
            "SELECT product_id, DATE(create_time) as `date`, " +
            "COUNT(DISTINCT order_id) as order_count, " +
            "SUM(item_count) as unit_count, " +
            "SUM(item_price) as sales " +
            "FROM erplus_cross_order_item " +
            "WHERE product_id IN " +
            "<foreach item='id' collection='productIds' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND create_time >= #{startDate} " +
            "GROUP BY product_id, DATE(create_time)" +
            "</script>")
    List<Map<String, Object>> selectAggregatedPerformance(@Param("productIds") Collection<Long> productIds, 
                                                          @Param("startDate") LocalDateTime startDate);

   default CrossOrderItemDO selectOne(String platformOrderId, Integer platformId) {
        return this.selectOne(new LambdaQueryWrapperX<CrossOrderItemDO>()
                .eqIfPresent(CrossOrderItemDO::getPlatformOrderId, platformOrderId)
                .eqIfPresent(CrossOrderItemDO::getPlatformId, platformId));
    }

    default List<CrossOrderItemDO> selectListByOrderIds(List<Long> orderIds) {
        return this.selectList(new LambdaQueryWrapperX<CrossOrderItemDO>()
                .inIfPresent(CrossOrderItemDO::getOrderId, orderIds));
    }

}