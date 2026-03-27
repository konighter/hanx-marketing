package com.hzltd.module.erplus.dal.mysql.shop;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 平台应用 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface PlatformAppMapper extends BaseMapperX<PlatformAppDO> {

    default List<PlatformAppDO> selectListByPlatform(String platform) {
        return selectList(new LambdaQueryWrapperX<PlatformAppDO>()
                .eqIfPresent(PlatformAppDO::getPlatform, platform));
    }

}
