package com.hzltd.module.erplus.sys.service.languages;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.languages.LanguagesDO;
import com.hzltd.module.erplus.sys.dal.mysql.languages.LanguagesMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.spapi.enums.ErrorCodeConstants.LANGUAGES_NOT_EXISTS;

/**
 * [Erplus] 语言定义 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class LanguagesServiceImpl implements LanguagesService {

    @Resource
    private LanguagesMapper languagesMapper;

    @Override
    public Integer createLanguages(LanguagesSaveReqVO createReqVO) {
        // 插入
        LanguagesDO languages = BeanUtils.toBean(createReqVO, LanguagesDO.class);
        languagesMapper.insert(languages);
        // 返回
        return languages.getId();
    }

    @Override
    public void updateLanguages(LanguagesSaveReqVO updateReqVO) {
        // 校验存在
        validateLanguagesExists(updateReqVO.getId());
        // 更新
        LanguagesDO updateObj = BeanUtils.toBean(updateReqVO, LanguagesDO.class);
        languagesMapper.updateById(updateObj);
    }

    @Override
    public void deleteLanguages(Integer id) {
        // 校验存在
        validateLanguagesExists(id);
        // 删除
        languagesMapper.deleteById(id);
    }

    private void validateLanguagesExists(Integer id) {
        if (languagesMapper.selectById(id) == null) {
            throw exception(LANGUAGES_NOT_EXISTS);
        }
    }

    @Override
    public LanguagesDO getLanguages(Integer id) {
        return languagesMapper.selectById(id);
    }

    @Override
    public PageResult<LanguagesDO> getLanguagesPage(LanguagesPageReqVO pageReqVO) {
        return languagesMapper.selectPage(pageReqVO);
    }

}