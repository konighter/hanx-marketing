package com.hzltd.module.erplus.sys.service.countries;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.countries.CountriesDO;
import jakarta.validation.Valid;

/**
 * [Erplus] 国家/地区定义 Service 接口
 *
 * @author hzadd
 */
public interface CountriesService {

    /**
     * 创建[Erplus] 国家/地区定义
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createCountries(@Valid CountriesSaveReqVO createReqVO);

    /**
     * 更新[Erplus] 国家/地区定义
     *
     * @param updateReqVO 更新信息
     */
    void updateCountries(@Valid CountriesSaveReqVO updateReqVO);

    /**
     * 删除[Erplus] 国家/地区定义
     *
     * @param id 编号
     */
    void deleteCountries(Integer id);

    /**
     * 获得[Erplus] 国家/地区定义
     *
     * @param id 编号
     * @return [Erplus] 国家/地区定义
     */
    CountriesDO getCountries(Integer id);

    /**
     * 获得[Erplus] 国家/地区定义分页
     *
     * @param pageReqVO 分页查询
     * @return [Erplus] 国家/地区定义分页
     */
    PageResult<CountriesDO> getCountriesPage(CountriesPageReqVO pageReqVO);

}