package com.hzltd.module.erplus.adv.dal.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportQueryReqVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@DS("doris")
@Mapper

public interface AdsReportQueryMapper {
    @TenantIgnore
    @SelectProvider(type = AdsReportQuerySqlProvider.class, method = "buildQuery")
    List<Map<String, Object>> queryAggregatedData(@Param("req") AdsReportQueryReqVO reqVO, 
                                                  @Param("today") LocalDate today);
}
