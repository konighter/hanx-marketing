package com.hzltd.module.erplus.dal.mysql.authorization;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.authorization.ShopAuthDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺授权关联 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface ShopAuthMapper extends BaseMapperX<ShopAuthDO> {
}
