package com.hzltd.module.erplus.adv.adapter.amazon.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.adv.adapter.amazon.model.event.AmazonAdTrafficMetric;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRawDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamRawMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdsAmazonReportStreamHandler {

    @Resource
    private AdsReportStreamRawMapper adsReportStreamRawMapper;

    @TenantIgnore
    public void processStreamMessage(String sqsMessageBody) {
        try {
            JSONObject message = JSONUtil.parseObj(sqsMessageBody);

            String datasetId = message.getStr("dataset_id");

            if ("sp-traffic".equals(datasetId)) {
                this.processSPTrafficData(sqsMessageBody);
            } else {
                this.processSPConvertData(sqsMessageBody);
            }


        } catch (Exception e) {
            log.error("processStreamMessage Error, message = {}", sqsMessageBody, e);
        }


    }

    private void processSPTrafficData(String sqsMessageBody) {
        TenantUtils.executeIgnore(() -> {
            log.info("[processSPTrafficData] message = {}", sqsMessageBody);
            AmazonAdTrafficMetric trafficMetric = JsonUtils.parseObject(sqsMessageBody, AmazonAdTrafficMetric.class);
            adsReportStreamRawMapper.insert(BeanUtils.toBean(trafficMetric, AdsReportStreamRawDO.class));
        });

    }

    private void processSPConvertData(String sqsMessageBody) {
        log.info("[processSPConvertData] message = {}", sqsMessageBody);
    }


}
