package com.hzltd.module.erplus.dal.mysql.cross;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 跨境产品价格 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossProductPriceMapper extends BaseMapperX<CrossProductPriceDO> {

}