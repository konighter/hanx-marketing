package com.hzltd.module.amz.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzReportTaskPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzReportTaskSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzReportTaskDO;
import jakarta.validation.Valid;

/**
 * 亚马逊报告任务 Service 接口
 *
 * @author 翰展科技
 */
public interface AmzReportTaskService {

    /**
     * 创建亚马逊报告任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAmzReportTask(@Valid AmzReportTaskSaveReqVO createReqVO);


    void checkReportStatus(Long id);

    /**
     * 获得亚马逊报告任务
     *
     * @param id 编号
     * @return 亚马逊报告任务
     */
    AmzReportTaskDO getAmzReportTask(Long id);

    /**
     * 获得亚马逊报告任务分页
     *
     * @param pageReqVO 分页查询
     * @return 亚马逊报告任务分页
     */
    PageResult<AmzReportTaskDO> getAmzReportTaskPage(AmzReportTaskPageReqVO pageReqVO);

}