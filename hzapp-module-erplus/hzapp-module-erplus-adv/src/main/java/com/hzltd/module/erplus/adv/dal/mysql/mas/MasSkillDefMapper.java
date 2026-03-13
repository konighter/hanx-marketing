package com.hzltd.module.erplus.adv.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MasSkillDefMapper extends BaseMapperX<MasSkillDefDO> {
    
    default MasSkillDefDO selectBySkillCode(String skillCode) {
        return selectOne(MasSkillDefDO::getSkillCode, skillCode);
    }
}
