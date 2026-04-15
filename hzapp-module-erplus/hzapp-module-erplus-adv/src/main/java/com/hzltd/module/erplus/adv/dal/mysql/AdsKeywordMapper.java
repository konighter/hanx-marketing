package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 关键词/投放目标 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsKeywordMapper extends BaseMapperX<AdsKeywordDO> {

    default List<AdsKeywordDO> selectListByAdGroupId(Long adGroupId) {
        return selectList(new LambdaQueryWrapperX<AdsKeywordDO>()
                .eq(AdsKeywordDO::getAdGroupId, adGroupId)
                .orderByDesc(AdsKeywordDO::getId));
    }

    default AdsKeywordDO selectByAdGroupAndExternalId(Long adGroupId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsKeywordDO>()
                .eq(AdsKeywordDO::getAdGroupId, adGroupId)
                .eq(AdsKeywordDO::getExternalId, externalId));
    }



    default AdsKeywordDO selectByShopAndExternalId(Long shopId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsKeywordDO>()
                .eq(AdsKeywordDO::getShopId, shopId)
                .eq(AdsKeywordDO::getExternalId, externalId));
    }



    default List<AdsKeywordDO> selectListByShopId(Long shopId) {
        return selectList(new LambdaQueryWrapperX<AdsKeywordDO>()
                .eq(AdsKeywordDO::getShopId, shopId)
                .orderByDesc(AdsKeywordDO::getId));
    }

    @SuppressWarnings("deprecation")
    default PageResult<AdsKeywordDO> selectPage(AdsKeywordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsKeywordDO>()
                .eqIfPresent(AdsKeywordDO::getShopId, reqVO.getShopId())
                .eqIfPresent(AdsKeywordDO::getAccountId, reqVO.getAccountId())
                .inIfPresent(AdsKeywordDO::getCampaignId, reqVO.getCampaignIds())
                .inIfPresent(AdsKeywordDO::getAdGroupId, reqVO.getAdGroupIds())
                .eqIfPresent(AdsKeywordDO::getExternalId, reqVO.getExternalId())
                .likeIfPresent(AdsKeywordDO::getKeywordText, reqVO.getKeywordText())
                .eqIfPresent(AdsKeywordDO::getMatchType, reqVO.getMatchType())
                .eqIfPresent(AdsKeywordDO::getStatus, reqVO.getStatus())
                .last("ORDER BY FIELD(status, 'ENABLED', 'PAUSED', 'ARCHIVED') ASC, id DESC"));
    }
}
