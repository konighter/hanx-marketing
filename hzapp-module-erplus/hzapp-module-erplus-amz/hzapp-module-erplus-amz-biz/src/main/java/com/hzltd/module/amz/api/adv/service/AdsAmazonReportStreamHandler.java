package com.hzltd.module.amz.api.adv.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.amz.api.adv.model.event.AmazonAdConvertMetric;
import com.hzltd.module.amz.api.adv.model.event.AmazonAdTrafficMetric;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamConvertDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsReportStreamRawDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamConvertMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsReportStreamRawMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdsAmazonReportStreamHandler {

    @Resource
    private AdsReportStreamRawMapper adsReportStreamRawMapper;

    @Resource
    private AdsReportStreamConvertMapper adsReportStreamConvertMapper;

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
        TenantUtils.executeIgnore(() -> {
            log.info("[processSPConvertData] message = {}", sqsMessageBody);
            AmazonAdConvertMetric convertMetric = JsonUtils.parseObject(sqsMessageBody, AmazonAdConvertMetric.class);
            adsReportStreamConvertMapper.insert(BeanUtils.toBean(convertMetric, AdsReportStreamConvertDO.class));
        });
    }


}

