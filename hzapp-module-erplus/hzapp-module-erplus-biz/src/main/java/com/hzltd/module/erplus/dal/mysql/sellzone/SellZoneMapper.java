package com.hzltd.module.erplus.dal.mysql.sellzone;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZonePageReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售区域 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface SellZoneMapper extends BaseMapperX<SellZoneDO> {

    default PageResult<SellZoneDO> selectPage(SellZonePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SellZoneDO>()
                .eqIfPresent(SellZoneDO::getPlatformId, reqVO.getPlatformId())
                .likeIfPresent(SellZoneDO::getZoneName, reqVO.getZoneName())
                .eqIfPresent(SellZoneDO::getZoneCode, reqVO.getZoneCode())
                .eqIfPresent(SellZoneDO::getLocale, reqVO.getLocale())
                .eqIfPresent(SellZoneDO::getLanguage, reqVO.getLanguage())
                .betweenIfPresent(SellZoneDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SellZoneDO::getId));
    }

    default List<SellZoneDO> selectList(SellZoneReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<SellZoneDO>()
                .eqIfPresent(SellZoneDO::getPlatformId, reqVO.getPlatformId())
                .likeIfPresent(SellZoneDO::getZoneName, reqVO.getZoneName())
                .eqIfPresent(SellZoneDO::getZoneCode, reqVO.getZoneCode())
                .eqIfPresent(SellZoneDO::getLocale, reqVO.getLocale())
                .eqIfPresent(SellZoneDO::getLanguage, reqVO.getLanguage())
                .betweenIfPresent(SellZoneDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SellZoneDO::getId));
    }

}