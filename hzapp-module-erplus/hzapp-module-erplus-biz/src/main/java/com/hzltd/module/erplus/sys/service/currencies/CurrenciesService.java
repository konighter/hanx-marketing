package com.hzltd.module.erplus.sys.service.currencies;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import jakarta.validation.Valid;

/**
 * [Erplus] 货币定义 Service 接口
 *
 * @author hzadd
 */
public interface CurrenciesService {

    /**
     * 创建[Erplus] 货币定义
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createCurrencies(@Valid CurrenciesSaveReqVO createReqVO);

    /**
     * 更新[Erplus] 货币定义
     *
     * @param updateReqVO 更新信息
     */
    void updateCurrencies(@Valid CurrenciesSaveReqVO updateReqVO);

    /**
     * 删除[Erplus] 货币定义
     *
     * @param id 编号
     */
    void deleteCurrencies(Integer id);

    /**
     * 获得[Erplus] 货币定义
     *
     * @param id 编号
     * @return [Erplus] 货币定义
     */
    CurrenciesDO getCurrencies(Integer id);

    /**
     * 获得[Erplus] 货币定义分页
     *
     * @param pageReqVO 分页查询
     * @return [Erplus] 货币定义分页
     */
    PageResult<CurrenciesDO> getCurrenciesPage(CurrenciesPageReqVO pageReqVO);

}