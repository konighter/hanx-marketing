package com.hzltd.module.erplus.sys.dal.mysql.hscodes;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesPageReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * [Erplus] 海关编码(HS Code) Mapper
 *
 * @author hzadd
 */
@Mapper
public interface HsCodesMapper extends BaseMapperX<HsCodesDO> {

    default PageResult<HsCodesDO> selectPage(HsCodesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HsCodesDO>()
                .eqIfPresent(HsCodesDO::getCode, reqVO.getCode())
                .eqIfPresent(HsCodesDO::getDescription, reqVO.getDescription())
                .eqIfPresent(HsCodesDO::getCategoryId, reqVO.getCategoryId())
                .betweenIfPresent(HsCodesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HsCodesDO::getId));
    }

}