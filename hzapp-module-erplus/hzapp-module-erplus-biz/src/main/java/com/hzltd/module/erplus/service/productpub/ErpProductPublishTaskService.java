package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;

import java.util.Optional;

public interface ErpProductPublishTaskService {

    Optional<ErpProductPublishTaskDO> getProductPublishTask(Long productId, Long version);
    Optional<ErpProductPublishTaskDO> getProductPublishTask(Long taskId);
    Long createProductPublishTask(ErpProductPublishTaskDO task);



}
