package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportHourlyDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告小时绩效报表 Mapper (Doris)
 */
@DS("doris")
@Mapper
public interface AdsReportHourlyMapper extends BaseMapperX<AdsReportHourlyDO> {

    default List<AdsReportHourlyDO> selectListByAccountAndHourRange(Long shopId, Long accountId, String groupColumn,
                                                                     LocalDateTime startHour, LocalDateTime endHour) {
        return selectList(new LambdaQueryWrapperX<AdsReportHourlyDO>()
                .eqIfPresent(AdsReportHourlyDO::getShopId, shopId)
                .eq(AdsReportHourlyDO::getAccountId, accountId)
                .eqIfPresent(AdsReportHourlyDO::getGroupColumn, groupColumn)
                .betweenIfPresent(AdsReportHourlyDO::getReportHour, startHour, endHour)
                .orderByAsc(AdsReportHourlyDO::getReportHour));
    }
}
