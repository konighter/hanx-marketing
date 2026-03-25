package com.hzltd.module.erplus.service.amz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.amz.controller.admin.vo.AmzPlacementOption;
import com.hzltd.module.amz.dal.dataobject.AmzInboundPlanDO;
import com.hzltd.module.amz.dal.mysql.AmzInboundPlanMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class AmzFulfillmentServiceTest extends BaseDbUnitTest{

@Resource
    private AmzInboundPlanMapper inboundPlanMapper;


    @Test
    public void testCreateFulfillmentOrder_success() {

        AmzInboundPlanDO planDO = inboundPlanMapper.selectById(2);

        ObjectMapper ob = new ObjectMapper();
        ob.registerModule(new ThreeTenModule());
        JsonUtils.init(ob);


       List<AmzPlacementOption> optionList = JsonUtils.parseArray(planDO.getPlacementOptions(), AmzPlacementOption.class);
        log.info("options: {}", optionList);
    }


}
