package com.hzltd.module.erplus.adv.auth.service;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
// import com.hzltd.module.amz.api.adv.model.event.AmazonAdTrafficMetric;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRawDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class StreamListenerTest {


    /*
    @Test
    public void testSpTraffic() {
        String json = "{\n" +
                "  \"advertiser_id\": \"A2K2SU3BU6HGRH\",\n" +
                "  \"marketplace_id\": \"ATVPDKIKX0DER\",\n" +
                "  \"dataset_id\": \"sp-traffic\",\n" +
                "  \"impressions\": 2,\n" +
                "  \"idempotency_id\": \"a13f22eb-5980-3b05-b506-593158aaa33f\",\n" +
                "  \"keyword_text\": \"key chain\",\n" +
                "  \"time_window_start\": \"2026-03-08T07:00:00-07:00\",\n" +
                "  \"ad_group_id\": \"448283180780832\",\n" +
                "  \"placement\": \"Top of Search on-Amazon\",\n" +
                "  \"cost\": 0.0,\n" +
                "  \"clicks\": 0,\n" +
                "  \"currency\": \"USD\",\n" +
                "  \"ad_id\": \"511081845139234\",\n" +
                "  \"match_type\": \"BROAD\",\n" +
                "  \"campaign_id\": \"132925643619089\",\n" +
                "  \"keyword_id\": \"367305939098673\"\n" +
                "}";

        AmazonAdTrafficMetric trafficMetric = JsonUtils.parseObject(json, AmazonAdTrafficMetric.class);

        log.info("metics={}", BeanUtils.toBean(trafficMetric, AdsReportStreamRawDO.class));

    }
    */



}
