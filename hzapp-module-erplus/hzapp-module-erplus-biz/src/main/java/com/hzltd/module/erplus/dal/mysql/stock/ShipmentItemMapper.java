package com.hzltd.module.erplus.dal.mysql.stock;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentItemDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 货件详情 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentItemMapper extends BaseMapperX<ShipmentItemDO> {

        /**
         * 根据货件计划ID删除货件计划商品
         *
         * @param planId 货件计划ID
         */
        @Delete("delete from erplus_shipment_item where shipment_id = #{planId}")
        void deleteBatchByPlanId(Integer planId);
        /**
         * 根据货件计划ID查询货件计划商品
         *
         * @param id 货件计划ID
         * @return 货件计划商品列表
         */
       default List<ShipmentItemDO> selectListByPlanId(Integer id) {
                return selectList(new LambdaQueryWrapperX<ShipmentItemDO>()
                                .eq(ShipmentItemDO::getShipmentId, id));
        }
}