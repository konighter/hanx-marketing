package com.hzltd.module.erplus.sys.service.countries;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.countries.CountriesDO;
import com.hzltd.module.erplus.sys.dal.mysql.countries.CountriesMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.COUNTRIES_NOT_EXISTS;

/**
 * [Erplus] 国家/地区定义 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class CountriesServiceImpl implements CountriesService {

    @Resource
    private CountriesMapper countriesMapper;

    @Override
    public Integer createCountries(CountriesSaveReqVO createReqVO) {
        // 插入
        CountriesDO countries = BeanUtils.toBean(createReqVO, CountriesDO.class);
        countriesMapper.insert(countries);
        // 返回
        return countries.getId();
    }

    @Override
    public void updateCountries(CountriesSaveReqVO updateReqVO) {
        // 校验存在
        validateCountriesExists(updateReqVO.getId());
        // 更新
        CountriesDO updateObj = BeanUtils.toBean(updateReqVO, CountriesDO.class);
        countriesMapper.updateById(updateObj);
    }

    @Override
    public void deleteCountries(Integer id) {
        // 校验存在
        validateCountriesExists(id);
        // 删除
        countriesMapper.deleteById(id);
    }

    private void validateCountriesExists(Integer id) {
        if (countriesMapper.selectById(id) == null) {
            throw exception(COUNTRIES_NOT_EXISTS);
        }
    }

    @Override
    public CountriesDO getCountries(Integer id) {
        return countriesMapper.selectById(id);
    }

    @Override
    public PageResult<CountriesDO> getCountriesPage(CountriesPageReqVO pageReqVO) {
        return countriesMapper.selectPage(pageReqVO);
    }

}