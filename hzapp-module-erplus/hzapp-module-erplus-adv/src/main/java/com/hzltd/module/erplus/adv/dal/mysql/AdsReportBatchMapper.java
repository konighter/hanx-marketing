package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 离线统一广告多维批处理聚合表 (Doris) Mapper
 */
@Mapper
@DS("doris")
public interface AdsReportBatchMapper extends BaseMapper<AdsReportBatchDO> {
}
