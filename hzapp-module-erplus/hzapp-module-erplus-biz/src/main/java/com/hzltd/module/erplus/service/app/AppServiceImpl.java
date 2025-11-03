package com.hzltd.module.erplus.service.app;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.enums.RedisKeyConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.hzltd.module.erplus.controller.admin.app.vo.*;
import com.hzltd.module.erplus.dal.dataobject.app.AppDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;

import com.hzltd.module.erplus.dal.mysql.app.AppMapper;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 应用注册信息 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Override
    public Integer createApp(AppSaveReqVO createReqVO) {
        if (CommonStatusEnum.isEnable(createReqVO.getStatus())) {
            validateAppDuplicate(createReqVO.getPlatformId());
        }

        // 插入
        AppDO app = BeanUtils.toBean(createReqVO, AppDO.class);
        appMapper.insert(app);
        // 返回
        return app.getId();
    }

    @Override
    public void updateApp(AppSaveReqVO updateReqVO) {
        if (CommonStatusEnum.isEnable(updateReqVO.getStatus())) {
            validateAppDuplicate(updateReqVO.getPlatformId());
        }

        // 校验存在
        validateAppExists(updateReqVO.getId());
        // 更新
        AppDO updateObj = BeanUtils.toBean(updateReqVO, AppDO.class);
        appMapper.updateById(updateObj);
    }

    @Override
    public void deleteApp(Integer id) {
        // 校验存在
        validateAppExists(id);
        // 删除
        appMapper.deleteById(id);
    }

    private void validateAppExists(Integer id) {
        if (appMapper.selectById(id) == null) {
            throw exception(APP_NOT_EXISTS);
        }
    }

    private void validateAppDuplicate(Long platformId) {
        List<AppDO> appList = appMapper.selectList(new LambdaQueryWrapperX<AppDO>()
                .eqIfPresent(AppDO::getPlatformId, platformId)
                .eq(AppDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
        if (CollectionUtils.isNotEmpty(appList)) {
            throw exception(APP_ALREADY_EXISTS);
        }
    }

    @Override
    public AppDO getApp(Integer id) {
        return appMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.KEY_APP, key = "#id")
    public AppDO getAppCache(Integer id) {
        return getApp(id);
    }

    @Override
    public PageResult<AppDO> getAppPage(AppPageReqVO pageReqVO) {
        return appMapper.selectPage(pageReqVO);
    }

}