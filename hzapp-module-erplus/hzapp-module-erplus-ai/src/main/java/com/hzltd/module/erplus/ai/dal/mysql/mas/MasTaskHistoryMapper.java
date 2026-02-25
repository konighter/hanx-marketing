package com.hzltd.module.erplus.ai.dal.mysql.mas;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MAS 任务执行历史日志 Mapper
 */
@Mapper
public interface MasTaskHistoryMapper extends BaseMapperX<MasTaskHistoryDO> {

    default List<MasTaskHistoryDO> selectListBySessionId(String sessionId) {
        return selectList(MasTaskHistoryDO::getSessionId, sessionId);
    }

}
