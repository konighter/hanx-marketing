package com.hzltd.module.erplus.dal.mysql.stock.v2;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 库存盘点表 (Header) Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpInventoryCheckMapper extends BaseMapperX<ErpInventoryCheckDO> {
}
