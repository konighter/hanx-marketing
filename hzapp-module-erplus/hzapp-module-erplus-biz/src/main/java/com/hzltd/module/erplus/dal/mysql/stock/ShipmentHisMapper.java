package com.hzltd.module.erplus.dal.mysql.stock;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.stock.ShipmentHisDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 货件状态历史 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ShipmentHisMapper extends BaseMapperX<ShipmentHisDO> {


}