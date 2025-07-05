package com.hzltd.module.erplus.sys.service.languages;

import javax.validation.*;

import com.hzltd.module.erplus.sys.dal.dataobject.languages.LanguagesDO;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesSaveReqVO;

/**
 * [Erplus] 语言定义 Service 接口
 *
 * @author hzadd
 */
public interface LanguagesService {

    /**
     * 创建[Erplus] 语言定义
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createLanguages(@Valid LanguagesSaveReqVO createReqVO);

    /**
     * 更新[Erplus] 语言定义
     *
     * @param updateReqVO 更新信息
     */
    void updateLanguages(@Valid LanguagesSaveReqVO updateReqVO);

    /**
     * 删除[Erplus] 语言定义
     *
     * @param id 编号
     */
    void deleteLanguages(Integer id);

    /**
     * 获得[Erplus] 语言定义
     *
     * @param id 编号
     * @return [Erplus] 语言定义
     */
    LanguagesDO getLanguages(Integer id);

    /**
     * 获得[Erplus] 语言定义分页
     *
     * @param pageReqVO 分页查询
     * @return [Erplus] 语言定义分页
     */
    PageResult<LanguagesDO> getLanguagesPage(LanguagesPageReqVO pageReqVO);

}