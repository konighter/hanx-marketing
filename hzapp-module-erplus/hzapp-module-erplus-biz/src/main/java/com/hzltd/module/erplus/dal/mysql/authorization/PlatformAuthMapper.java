package com.hzltd.module.erplus.dal.mysql.authorization;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 平台授权 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface PlatformAuthMapper extends BaseMapperX<PlatformAuthDO> {

    default PlatformAuthDO selectByUserIdAndPlatformAndRegionAndSellerId(Long userId, String platform, String region, Long appId, String sellerId) {
        return selectOne(new LambdaQueryWrapperX<PlatformAuthDO>()
                .eq(PlatformAuthDO::getUserId, userId)
                .eq(PlatformAuthDO::getPlatform, platform)
                .eq(PlatformAuthDO::getRegion, region)
                .eqIfPresent(PlatformAuthDO::getSellerId, sellerId)
                .eq(PlatformAuthDO::getAppId, appId));
    }

    @Select("SELECT a.* FROM erplus_platform_auth a " +
            "JOIN erplus_shop_auth s ON a.id = s.auth_id " +
            "WHERE s.shop_id = #{shopId} AND a.user_id = #{userId} " +
            "AND a.deleted = 0 AND s.deleted = 0")
    List<PlatformAuthDO> selectListByShopIdAndUserId(@Param("shopId") Long shopId, @Param("userId") Long userId);

    @Select("SELECT a.* FROM erplus_platform_auth a " +
            "JOIN erplus_shop_auth s ON a.id = s.auth_id " +
            "WHERE s.shop_id = #{shopId} AND a.auth_type = #{authType} " +
            "AND s.is_default = 1 AND a.deleted = 0 AND s.deleted = 0")
    PlatformAuthDO selectDefaultAuthByShopIdAndType(@Param("shopId") Long shopId, @Param("authType") String authType);

}
