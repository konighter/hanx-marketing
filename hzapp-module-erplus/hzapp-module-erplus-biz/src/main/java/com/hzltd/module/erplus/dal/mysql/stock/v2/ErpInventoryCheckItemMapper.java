package com.hzltd.module.erplus.dal.mysql.stock.v2;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ERP 库存盘点明细表 (Detail) Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpInventoryCheckItemMapper extends BaseMapperX<ErpInventoryCheckItemDO> {

    default List<ErpInventoryCheckItemDO> selectListByCheckId(Long checkId) {
        return selectList(ErpInventoryCheckItemDO::getCheckId, checkId);
    }

}
