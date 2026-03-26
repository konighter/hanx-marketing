package com.hzltd.module.erplus.adv.mas.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.mas.dal.dataobject.MasTaskSkillRelDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MasTaskSkillRelMapper extends BaseMapperX<MasTaskSkillRelDO> {
    
    default MasTaskSkillRelDO selectByTaskId(Long taskId) {
        return selectOne(MasTaskSkillRelDO::getTaskId, taskId);
    }
}
