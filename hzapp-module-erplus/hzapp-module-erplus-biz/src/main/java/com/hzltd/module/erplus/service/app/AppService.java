package com.hzltd.module.erplus.service.app;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.app.vo.AppPageReqVO;
import com.hzltd.module.erplus.controller.admin.app.vo.AppSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.app.AppDO;
import jakarta.validation.Valid;

/**
 * 应用注册信息 Service 接口
 *
 * @author 翰展科技
 */
public interface AppService {

    /**
     * 创建应用注册信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createApp(@Valid AppSaveReqVO createReqVO);

    /**
     * 更新应用注册信息
     *
     * @param updateReqVO 更新信息
     */
    void updateApp(@Valid AppSaveReqVO updateReqVO);

    /**
     * 删除应用注册信息
     *
     * @param id 编号
     */
    void deleteApp(Integer id);

    /**
     * 获得应用注册信息
     *
     * @param id 编号
     * @return 应用注册信息
     */
    AppDO getApp(Integer id);

    /**
     * 获得应用注册信息
     *
     * @param id 编号
     * @return 应用注册信息
     */
    AppDO getAppCache(Integer id);

    /**
     * 获得应用注册信息分页
     *
     * @param pageReqVO 分页查询
     * @return 应用注册信息分页
     */
    PageResult<AppDO> getAppPage(AppPageReqVO pageReqVO);

}