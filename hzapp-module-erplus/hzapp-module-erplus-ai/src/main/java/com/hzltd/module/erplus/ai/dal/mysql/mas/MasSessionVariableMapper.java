package com.hzltd.module.erplus.ai.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionVariableDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MAS 会话上下文变量 Mapper
 */
@Mapper
public interface MasSessionVariableMapper extends BaseMapperX<MasSessionVariableDO> {

    default List<MasSessionVariableDO> selectListBySessionId(String sessionId) {
        return selectList(MasSessionVariableDO::getSessionId, sessionId);
    }

}
