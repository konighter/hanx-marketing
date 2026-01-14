package com.hzltd.module.erplus.service.app;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.erplus.dal.mysql.amz.AmzInboundPlanMapper;
import com.hzltd.module.erplus.dal.mysql.app.AppMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

public class AmzFulfillmentServiceTest extends BaseDbUnitTest{

@Resource
    private AmzInboundPlanMapper inboundPlanMapper;


    @Test
    public void testCreateFulfillmentOrder_success() {

        inboundPlanMapper.selectById(1);

    }


}
