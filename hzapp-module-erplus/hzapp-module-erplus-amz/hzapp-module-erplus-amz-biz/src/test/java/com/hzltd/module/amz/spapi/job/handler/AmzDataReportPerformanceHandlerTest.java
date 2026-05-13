package com.hzltd.module.amz.spapi.job.handler;

import org.junit.jupiter.api.Test;

public class AmzDataReportPerformanceHandlerTest {

    @Test
    public void Test_ProcessReportUrl() {


        AmzDataReportPerformanceHandler handler =  new AmzDataReportPerformanceHandler();

        handler.downloadAndProcess("https://tortuga-prod-na.s3-external-1.amazonaws.com/a9926f67-aa2e-4c19-a77a-af82d284caab.amzn1.tortuga.4.na.T35C9E8CAL94RY?response-content-encoding=identity&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260512T114838Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=AKIA5U6MO6RAJEGJ3GSS%2F20260512%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=5f5bc118d8e2019ca0fd48cbe32beae96e1c316959b9d17f0f54af5a0caf1854", "GZIP", "", null);
    }




}
