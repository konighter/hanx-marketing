package com.hzltd.module.erplus.adv.mas.convert;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillInstanceVO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceRelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// import java.util.List removed

/**
 * 已激活技能转换器
 */
@Mapper
public interface MasSkillConvert {

    MasSkillConvert INSTANCE = Mappers.getMapper(MasSkillConvert.class);

    @Mapping(target = "skillName", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "latestProgress", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "processInstanceId", ignore = true)
    MasSkillInstanceVO convert(MasSkillInstanceRelDO bean);

    PageResult<MasSkillInstanceVO> convertPage(PageResult<MasSkillInstanceRelDO> page);
}
