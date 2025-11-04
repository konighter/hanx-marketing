package com.hzltd.module.erplus.dal.mysql.plugin;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.plugin.PluginDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 插件 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface PluginMapper extends BaseMapperX<PluginDO> {

    default PageResult<PluginDO> selectPage(PluginPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PluginDO>()
                .likeIfPresent(PluginDO::getName, reqVO.getName())
                .eqIfPresent(PluginDO::getPluginKey, reqVO.getPluginKey())
                .eqIfPresent(PluginDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PluginDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PluginDO::getId));
    }

}