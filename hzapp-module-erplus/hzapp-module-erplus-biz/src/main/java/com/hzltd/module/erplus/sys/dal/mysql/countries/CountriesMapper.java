package com.hzltd.module.erplus.sys.dal.mysql.countries;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.sys.dal.dataobject.countries.CountriesDO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * [Erplus] 国家/地区定义 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface CountriesMapper extends BaseMapperX<CountriesDO> {

    default PageResult<CountriesDO> selectPage(CountriesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CountriesDO>()
                .eqIfPresent(CountriesDO::getIsoCode2, reqVO.getIsoCode2())
                .eqIfPresent(CountriesDO::getIsoCode3, reqVO.getIsoCode3())
                .likeIfPresent(CountriesDO::getName, reqVO.getName())
                .eqIfPresent(CountriesDO::getDefaultLanguageCode, reqVO.getDefaultLanguageCode())
                .eqIfPresent(CountriesDO::getDefaultCurrencyCode, reqVO.getDefaultCurrencyCode())
                .eqIfPresent(CountriesDO::getIsActive, reqVO.getIsActive())
                .betweenIfPresent(CountriesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CountriesDO::getId));
    }

}