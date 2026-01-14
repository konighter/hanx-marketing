package com.hzltd.module.erplus.dal.mysql.amz;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.amz.AmzReportTaskDO;
import org.apache.ibatis.annotations.Mapper;
import com.hzltd.module.erplus.controller.admin.amz.vo.*;

/**
 * 亚马逊报告任务 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzReportTaskMapper extends BaseMapperX<AmzReportTaskDO> {

    default PageResult<AmzReportTaskDO> selectPage(AmzReportTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AmzReportTaskDO>()
                .eqIfPresent(AmzReportTaskDO::getQuery, reqVO.getQuery())
                .eqIfPresent(AmzReportTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AmzReportTaskDO::getReportResult, reqVO.getReportResult())
                .betweenIfPresent(AmzReportTaskDO::getLastCheckTime, reqVO.getLastCheckTime())
                .eqIfPresent(AmzReportTaskDO::getIsArchive, reqVO.getIsArchive())
                .betweenIfPresent(AmzReportTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AmzReportTaskDO::getId));
    }

}