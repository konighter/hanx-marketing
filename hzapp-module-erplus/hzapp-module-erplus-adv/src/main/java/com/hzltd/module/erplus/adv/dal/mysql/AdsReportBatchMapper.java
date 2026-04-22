package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportBatchDO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 离线统一广告多维批处理聚合表 (Doris) Mapper
 */
@Mapper
public interface AdsReportBatchMapper extends BaseMapper<AdsReportBatchDO> {
}
