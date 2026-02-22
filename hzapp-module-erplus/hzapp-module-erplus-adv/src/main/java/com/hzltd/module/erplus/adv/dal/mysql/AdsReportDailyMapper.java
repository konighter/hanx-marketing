package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportDailyDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 广告每日绩效报表 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsReportDailyMapper extends BaseMapperX<AdsReportDailyDO> {

    default List<AdsReportDailyDO> selectListByEntityAndDateRange(String entityType, Long entityId,
                                                                   LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eq(AdsReportDailyDO::getEntityType, entityType)
                .eq(AdsReportDailyDO::getEntityId, entityId)
                .betweenIfPresent(AdsReportDailyDO::getReportDate, startDate, endDate)
                .orderByAsc(AdsReportDailyDO::getReportDate));
    }

    default List<AdsReportDailyDO> selectListByAccountAndDateRange(Long accountId, String entityType,
                                                                    LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eq(AdsReportDailyDO::getAccountId, accountId)
                .eqIfPresent(AdsReportDailyDO::getEntityType, entityType)
                .betweenIfPresent(AdsReportDailyDO::getReportDate, startDate, endDate)
                .orderByAsc(AdsReportDailyDO::getReportDate));
    }

    default AdsReportDailyDO selectByUniqueKey(Long accountId, String entityType, Long entityId, LocalDate reportDate) {
        return selectOne(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eq(AdsReportDailyDO::getAccountId, accountId)
                .eq(AdsReportDailyDO::getEntityType, entityType)
                .eq(AdsReportDailyDO::getEntityId, entityId)
                .eq(AdsReportDailyDO::getReportDate, reportDate));
    }
}
