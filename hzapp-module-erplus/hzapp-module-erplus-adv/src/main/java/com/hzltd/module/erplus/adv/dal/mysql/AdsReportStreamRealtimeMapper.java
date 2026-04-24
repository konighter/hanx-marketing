package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRealtimeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实时统一广告多维流数据宽表 Mapper
 *
 * @author antigravity
 */
@Mapper
@DS("doris")
public interface AdsReportStreamRealtimeMapper extends BaseMapperX<AdsReportStreamRealtimeDO> {
}
