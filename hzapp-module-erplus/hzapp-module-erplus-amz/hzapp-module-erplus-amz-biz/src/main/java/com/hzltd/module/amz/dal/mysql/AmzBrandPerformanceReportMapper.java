package com.hzltd.module.amz.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzltd.module.amz.dal.dataobject.AmzBrandPerformanceReportDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.Collection;

@Mapper
@DS("doris")
public interface AmzBrandPerformanceReportMapper extends BaseMapper<AmzBrandPerformanceReportDO> {
    default void insertBatch(Collection<AmzBrandPerformanceReportDO> entityList) {
        entityList.forEach(this::insert);
    }

    default java.util.List<AmzBrandPerformanceReportDO> selectStatsByAsin(String asin, java.time.LocalDate startDate) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AmzBrandPerformanceReportDO>()
                .eq(AmzBrandPerformanceReportDO::getAsin, asin)
                .eq(AmzBrandPerformanceReportDO::getReportType, "SCP")
                .ge(AmzBrandPerformanceReportDO::getStartDate, startDate)
                .orderByAsc(AmzBrandPerformanceReportDO::getStartDate));
    }
}
