package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRawDO;
import org.apache.ibatis.annotations.Mapper;

@DS("doris")
@Mapper
public interface AdsReportStreamRawMapper extends BaseMapperX<AdsReportStreamRawDO> {
}
