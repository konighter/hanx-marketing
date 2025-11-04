package com.hzltd.module.erplus.sys.dal.mysql.languages;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesPageReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.languages.LanguagesDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * [Erplus] 语言定义 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface LanguagesMapper extends BaseMapperX<LanguagesDO> {

    default PageResult<LanguagesDO> selectPage(LanguagesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LanguagesDO>()
                .eqIfPresent(LanguagesDO::getCode, reqVO.getCode())
                .likeIfPresent(LanguagesDO::getName, reqVO.getName())
                .eqIfPresent(LanguagesDO::getIsActive, reqVO.getIsActive())
                .betweenIfPresent(LanguagesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LanguagesDO::getId));
    }

}