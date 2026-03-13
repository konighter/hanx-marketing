package com.hzltd.module.erplus.adv.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasTaskSkillRelDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MasTaskSkillRelMapper extends BaseMapperX<MasTaskSkillRelDO> {
    
    default MasTaskSkillRelDO selectByTaskId(Long taskId) {
        return selectOne(MasTaskSkillRelDO::getTaskId, taskId);
    }
}
