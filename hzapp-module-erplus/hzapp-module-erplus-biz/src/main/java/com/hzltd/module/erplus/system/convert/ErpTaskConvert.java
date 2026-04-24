package com.hzltd.module.erplus.system.convert;

import com.hzltd.module.erplus.system.dal.dataobject.ErpScheduleTaskDO;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ErpTaskConvert {

    ErpTaskConvert INSTANCE = Mappers.getMapper(ErpTaskConvert.class);

    ErpScheduleTaskDO convert(ErpTaskSubmitRequest request);

    ErpTaskDTO convert(ErpScheduleTaskDO bean);

    List<ErpTaskDTO> convertList(List<ErpScheduleTaskDO> list);
}
