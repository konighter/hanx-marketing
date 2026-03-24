package com.hzltd.module.amz.api.adv.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;

public interface AdsAmazonReportService {

    /**
     * 执行同步任务状态机
     */
    void executeSyncTask(AdsSyncTaskDO task);

}

