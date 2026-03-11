package com.hzltd.module.erplus.ai.convert.mas;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasEventLogRespVO;
import com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasSessionRespVO;
import com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasTaskHistoryRespVO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MAS 转换器
 */
@Mapper
public interface MasConvert {

    MasConvert INSTANCE = Mappers.getMapper(MasConvert.class);

    @Mapping(source = "sessionId", target = "id")
    MasSessionRespVO convert(MasSessionDO bean);

    List<MasSessionRespVO> convertList(List<MasSessionDO> list);

    PageResult<MasSessionRespVO> convertPage(PageResult<MasSessionDO> page);

    MasTaskHistoryRespVO convert(MasTaskHistoryDO bean);

    List<MasTaskHistoryRespVO> convertList2(List<MasTaskHistoryDO> list);

    MasEventLogRespVO convert(MasEventLogDO bean);

    List<MasEventLogRespVO> convertList3(List<MasEventLogDO> list);

}
