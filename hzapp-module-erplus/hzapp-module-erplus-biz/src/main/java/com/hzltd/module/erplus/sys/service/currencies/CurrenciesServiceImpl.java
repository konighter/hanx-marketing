package com.hzltd.module.erplus.sys.service.currencies;

import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesSaveReqVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;

import com.hzltd.module.erplus.sys.dal.mysql.currencies.CurrenciesMapper;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * [Erplus] 货币定义 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class CurrenciesServiceImpl implements CurrenciesService {

    @Resource
    private CurrenciesMapper currenciesMapper;

    @Override
    public Integer createCurrencies(CurrenciesSaveReqVO createReqVO) {
        // 插入
        CurrenciesDO currencies = BeanUtils.toBean(createReqVO, CurrenciesDO.class);
        currenciesMapper.insert(currencies);
        // 返回
        return currencies.getId();
    }

    @Override
    public void updateCurrencies(CurrenciesSaveReqVO updateReqVO) {
        // 校验存在
        validateCurrenciesExists(updateReqVO.getId());
        // 更新
        CurrenciesDO updateObj = BeanUtils.toBean(updateReqVO, CurrenciesDO.class);
        currenciesMapper.updateById(updateObj);
    }

    @Override
    public void deleteCurrencies(Integer id) {
        // 校验存在
        validateCurrenciesExists(id);
        // 删除
        currenciesMapper.deleteById(id);
    }

    private void validateCurrenciesExists(Integer id) {
        if (currenciesMapper.selectById(id) == null) {
            throw exception(CURRENCIES_NOT_EXISTS);
        }
    }

    @Override
    public CurrenciesDO getCurrencies(Integer id) {
        return currenciesMapper.selectById(id);
    }

    @Override
    public PageResult<CurrenciesDO> getCurrenciesPage(CurrenciesPageReqVO pageReqVO) {
        return currenciesMapper.selectPage(pageReqVO);
    }

}