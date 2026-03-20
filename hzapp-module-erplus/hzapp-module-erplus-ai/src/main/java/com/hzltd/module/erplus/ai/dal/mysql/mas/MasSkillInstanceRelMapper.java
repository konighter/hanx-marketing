package com.hzltd.module.erplus.ai.dal.mysql.mas;
 
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MAS Skill 实例关联 Mapper
 */
@Mapper
public interface MasSkillInstanceRelMapper extends BaseMapperX<MasSkillInstanceRelDO> {

    default List<MasSkillInstanceRelDO> selectBySkill(String skillCode) {
        return this.selectList(new LambdaQueryWrapperX<MasSkillInstanceRelDO>().eqIfPresent(MasSkillInstanceRelDO::getSkillCode, skillCode));
    }

}
