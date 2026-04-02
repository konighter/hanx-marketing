package com.hzltd.module.erplus.service.plugin;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginPageReqVO;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.plugin.PluginDO;
import com.hzltd.module.erplus.dal.mysql.plugin.PluginMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PLUGIN_NOT_EXISTS;

/**
 * 插件 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class PluginServiceImpl implements PluginService {

    @Resource
    private PluginMapper pluginMapper;

    @Override
    public Integer createPlugin(PluginSaveReqVO createReqVO) {
        // 插入
        PluginDO plugin = BeanUtils.toBean(createReqVO, PluginDO.class);
        pluginMapper.insert(plugin);
        // 返回
        return plugin.getId();
    }

    @Override
    public void updatePlugin(PluginSaveReqVO updateReqVO) {
        // 校验存在
        validatePluginExists(updateReqVO.getId());
        // 更新
        PluginDO updateObj = BeanUtils.toBean(updateReqVO, PluginDO.class);
        pluginMapper.updateById(updateObj);
    }

    @Override
    public void deletePlugin(Integer id) {
        // 校验存在
        validatePluginExists(id);
        // 删除
        pluginMapper.deleteById(id);
    }

    private void validatePluginExists(Integer id) {
        if (pluginMapper.selectById(id) == null) {
            throw exception(PLUGIN_NOT_EXISTS);
        }
    }

    @Override
    public PluginDO getPlugin(Integer id) {
        return pluginMapper.selectById(id);
    }

    @Override
    public PageResult<PluginDO> getPluginPage(PluginPageReqVO pageReqVO) {
        return pluginMapper.selectPage(pageReqVO);
    }

}