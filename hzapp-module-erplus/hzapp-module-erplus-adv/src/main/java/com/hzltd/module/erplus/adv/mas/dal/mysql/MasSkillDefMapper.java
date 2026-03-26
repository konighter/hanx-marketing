package com.hzltd.module.erplus.adv.mas.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.mas.dal.dataobject.MasSkillDefDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MasSkillDefMapper extends BaseMapperX<MasSkillDefDO> {
    
    default MasSkillDefDO selectBySkillCode(String skillCode) {
        return selectOne(MasSkillDefDO::getSkillCode, skillCode);
    }
}
