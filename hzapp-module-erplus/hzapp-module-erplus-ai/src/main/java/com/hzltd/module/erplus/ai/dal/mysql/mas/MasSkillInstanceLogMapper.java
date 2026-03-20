package com.hzltd.module.erplus.ai.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceLogDO;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MAS 技能实例运行日志 Mapper
 */
@Mapper
public interface MasSkillInstanceLogMapper extends BaseMapperX<MasSkillInstanceLogDO> {

    default List<MasSkillInstanceLogDO> selectByInstanceId(Long instanceId) {
        return selectList(new LambdaQueryWrapperX<MasSkillInstanceLogDO>()
                .eq(MasSkillInstanceLogDO::getInstanceId, instanceId));
    }

}
