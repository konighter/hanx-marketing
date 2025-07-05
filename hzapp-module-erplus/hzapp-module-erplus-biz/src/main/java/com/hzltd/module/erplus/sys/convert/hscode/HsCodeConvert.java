package com.hzltd.module.erplus.sys.convert.hscode;

import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesRespVO;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HsCodeConvert {
    HsCodeConvert INSTANCE = Mappers.getMapper(HsCodeConvert.class);

    List<HsCodesDO> convertList(List<HsCodesRespVO> list);



}
