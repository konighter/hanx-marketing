package com.hzltd.module.erplus.dal.mysql.app;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.app.AppDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.app.vo.*;

/**
 * 应用注册信息 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AppMapper extends BaseMapperX<AppDO> {

    default PageResult<AppDO> selectPage(AppPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AppDO>()
                .likeIfPresent(AppDO::getName, reqVO.getName())
                .eqIfPresent(AppDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(AppDO::getAppId, reqVO.getAppId())
                .eqIfPresent(AppDO::getAppKey, reqVO.getAppKey())
                .eqIfPresent(AppDO::getAppSecret, reqVO.getAppSecret())
                .betweenIfPresent(AppDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AppDO::getId));
    }


}