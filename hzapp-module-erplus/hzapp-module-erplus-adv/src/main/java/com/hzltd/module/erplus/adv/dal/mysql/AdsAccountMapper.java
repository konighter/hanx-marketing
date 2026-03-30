package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告账户 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsAccountMapper extends BaseMapperX<AdsAccountDO> {

    default AdsAccountDO selectByPlatformAndExternalId(String platform, String externalAccountId) {
        return selectOne(new LambdaQueryWrapperX<AdsAccountDO>()
                .eq(AdsAccountDO::getPlatform, platform)
                .eq(AdsAccountDO::getExternalAccountId, externalAccountId));
    }

    default List<AdsAccountDO> selectListByShopId(Long shopId) {
        return selectList(AdsAccountDO::getShopId, shopId);
    }

    default List<AdsAccountDO> selectListByPlatform(String platform) {
        return selectList(new LambdaQueryWrapperX<AdsAccountDO>()
                .eqIfPresent(AdsAccountDO::getPlatform, platform)
                .orderByDesc(AdsAccountDO::getId));
    }

    default List<AdsAccountDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<AdsAccountDO>()
                .eq(AdsAccountDO::getParentId, parentId)
                .orderByDesc(AdsAccountDO::getId));
    }

    default List<AdsAccountDO> selectListByCredentialId(Long credentialId) {
        return selectList(new LambdaQueryWrapperX<AdsAccountDO>()
                .eq(AdsAccountDO::getCredentialId, credentialId)
                .orderByDesc(AdsAccountDO::getId));
    }

    default List<AdsAccountDO> selectListByAuthStatus(Integer authStatus) {
        return selectList(new LambdaQueryWrapperX<AdsAccountDO>()
                .eq(AdsAccountDO::getAuthStatus, authStatus)
                .orderByDesc(AdsAccountDO::getId));
    }

    default PageResult<AdsAccountDO> selectPage(com.hzltd.module.erplus.adv.auth.vo.AdsAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAccountDO>()
                .likeIfPresent(AdsAccountDO::getName, reqVO.getName())
                .eqIfPresent(AdsAccountDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(AdsAccountDO::getAuthStatus, reqVO.getAuthStatus())
                .betweenIfPresent(AdsAccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdsAccountDO::getId));
    }
}
