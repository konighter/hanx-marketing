package com.hzltd.module.erplus.service.shop;

import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;

import java.util.List;

/**
 * 平台应用 Service 接口
 *
 * @author hzadd
 */
public interface PlatformAppService {

    /**
     * 创建平台应用
     *
     * @param app 平台应用
     * @return 编号
     */
    Long createPlatformApp(PlatformAppDO app);

    /**
     * 更新平台应用
     *
     * @param app 平台应用
     */
    void updatePlatformApp(PlatformAppDO app);

    /**
     * 删除平台应用
     *
     * @param id 编号
     */
    void deletePlatformApp(Long id);

    /**
     * 获得平台应用
     *
     * @param id 编号
     * @return 平台应用
     */
    PlatformAppDO getPlatformApp(Long id);

    /**
     * 获得平台应用列表
     *
     * @param platform 平台类型
     * @return 平台应用列表
     */
    List<PlatformAppDO> getPlatformAppList(String platform);

}
