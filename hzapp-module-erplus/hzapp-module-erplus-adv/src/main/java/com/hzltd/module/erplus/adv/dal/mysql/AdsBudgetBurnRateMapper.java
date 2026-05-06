package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsBudgetBurnRateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 广告预算消耗进度记录 Mapper
 */
@TenantIgnore
@Mapper
public interface AdsBudgetBurnRateMapper extends BaseMapperX<AdsBudgetBurnRateDO> {

    @Select("<script>" +
            "SELECT * FROM (" +
            "  SELECT abbr.*, ROW_NUMBER() OVER (PARTITION BY budget_scope_id ORDER BY id DESC) AS rn " +
            "  FROM ads_budget_burn_rate abbr " +
            "  WHERE budget_scope_id IN " +
            "  <foreach item='id' collection='campaignIds' open='(' separator=',' close=')'>" +
            "    #{id}" +
            "  </foreach>" +
            ") t WHERE t.rn = 1" +
            "</script>")
    List<AdsBudgetBurnRateDO> getCampaignBudgetRate(@Param("campaignIds") List<String> campaignIds);



}
