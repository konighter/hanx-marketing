package com.hzltd.module.erplus.sys.dal.mysql.currencies;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * [Erplus] 货币定义 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface CurrenciesMapper extends BaseMapperX<CurrenciesDO> {

    default PageResult<CurrenciesDO> selectPage(CurrenciesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CurrenciesDO>()
                .eqIfPresent(CurrenciesDO::getCode, reqVO.getCode())
                .eqIfPresent(CurrenciesDO::getSymbol, reqVO.getSymbol())
                .likeIfPresent(CurrenciesDO::getName, reqVO.getName())
                .eqIfPresent(CurrenciesDO::getExchangeRate, reqVO.getExchangeRate())
                .eqIfPresent(CurrenciesDO::getIsActive, reqVO.getIsActive())
                .betweenIfPresent(CurrenciesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CurrenciesDO::getId));
    }

}