package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
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
@DS("doris")
@Mapper
public interface AdsReportDailyMapper extends BaseMapperX<AdsReportDailyDO> {

    default List<AdsReportDailyDO> selectListByEntityAndDateRange(Long shopId, String groupColumn, Long campaignId, Long adGroupId,
                                                                   LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eqIfPresent(AdsReportDailyDO::getShopId, shopId)
                .eq(AdsReportDailyDO::getGroupColumn, groupColumn)
                .eq(AdsReportDailyDO::getCampaignId, campaignId)
                .eqIfPresent(AdsReportDailyDO::getAdGroupId, adGroupId)
                .betweenIfPresent(AdsReportDailyDO::getReportDate, startDate, endDate)
                .orderByAsc(AdsReportDailyDO::getReportDate));
    }

    default List<AdsReportDailyDO> selectListByAccountAndDateRange(Long shopId, Long accountId, String groupColumn,
                                                                    LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eqIfPresent(AdsReportDailyDO::getShopId, shopId)
                .eqIfPresent(AdsReportDailyDO::getAccountId, accountId)
                .eqIfPresent(AdsReportDailyDO::getGroupColumn, groupColumn)
                .betweenIfPresent(AdsReportDailyDO::getReportDate, startDate, endDate)
                .orderByAsc(AdsReportDailyDO::getReportDate));
    }

    default AdsReportDailyDO selectByUniqueKey(Long shopId, Long accountId, String groupColumn, Long campaignId, Long adGroupId, 
                                               LocalDate reportDate, String targeting, String searchTerm) {
        return selectOne(new LambdaQueryWrapperX<AdsReportDailyDO>()
                .eqIfPresent(AdsReportDailyDO::getShopId, shopId)
                .eq(AdsReportDailyDO::getAccountId, accountId)
                .eq(AdsReportDailyDO::getGroupColumn, groupColumn)
                .eq(AdsReportDailyDO::getCampaignId, campaignId)
                .eqIfPresent(AdsReportDailyDO::getAdGroupId, adGroupId)
                .eq(AdsReportDailyDO::getReportDate, reportDate)
                .eqIfPresent(AdsReportDailyDO::getTargeting, targeting)
                .eqIfPresent(AdsReportDailyDO::getSearchTerm, searchTerm));
    }
}
