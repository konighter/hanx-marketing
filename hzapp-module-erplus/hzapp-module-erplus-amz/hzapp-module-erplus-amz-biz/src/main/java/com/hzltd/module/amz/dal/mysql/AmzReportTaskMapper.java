package com.hzltd.module.amz.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.amz.controller.admin.vo.AmzReportTaskPageReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzReportTaskDO;
import org.apache.ibatis.annotations.Mapper;

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