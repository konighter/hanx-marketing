package com.hzltd.module.erplus.service.plugin;

import java.util.*;
import javax.validation.*;
import com.hzltd.module.erplus.controller.admin.plugin.vo.*;
import com.hzltd.module.erplus.dal.dataobject.plugin.PluginDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.PageParam;

/**
 * 插件 Service 接口
 *
 * @author 翰展科技
 */
public interface PluginService {

    /**
     * 创建插件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createPlugin(@Valid PluginSaveReqVO createReqVO);

    /**
     * 更新插件
     *
     * @param updateReqVO 更新信息
     */
    void updatePlugin(@Valid PluginSaveReqVO updateReqVO);

    /**
     * 删除插件
     *
     * @param id 编号
     */
    void deletePlugin(Integer id);

    /**
     * 获得插件
     *
     * @param id 编号
     * @return 插件
     */
    PluginDO getPlugin(Integer id);

    /**
     * 获得插件分页
     *
     * @param pageReqVO 分页查询
     * @return 插件分页
     */
    PageResult<PluginDO> getPluginPage(PluginPageReqVO pageReqVO);

}