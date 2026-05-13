package com.hzltd.module.amz.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.amz.dal.dataobject.AmzSearchTermsWeeklyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 亚马逊搜索词周报 Mapper
 */
@DS("doris")
@Mapper
public interface AmzSearchTermsWeeklyMapper extends BaseMapperX<AmzSearchTermsWeeklyDO> {
}
