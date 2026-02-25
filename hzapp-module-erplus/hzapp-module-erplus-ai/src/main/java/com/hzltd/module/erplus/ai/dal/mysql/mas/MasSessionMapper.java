package com.hzltd.module.erplus.ai.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MAS 会话主表 Mapper
 */
@Mapper
public interface MasSessionMapper extends BaseMapperX<MasSessionDO> {

    default MasSessionDO selectBySessionId(String sessionId) {
        return selectOne(new LambdaQueryWrapperX<MasSessionDO>()
                .eq(MasSessionDO::getSessionId, sessionId));
    }

}
