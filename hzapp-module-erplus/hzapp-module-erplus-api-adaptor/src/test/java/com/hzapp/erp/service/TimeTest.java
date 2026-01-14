package com.hzapp.erp.service;

import com.hzltd.framework.common.util.date.DateUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;

public class TimeTest {


    @Test
    void TestTime() {
        OffsetDateTime endTime = OffsetDateTime.of(2025, 12, 6, 0, 0, 0, 0, OffsetDateTime.now().getOffset());
        System.out.println(endTime);
        System.out.println(endTime.plusWeeks(-1));
        System.out.println( DateUtils.buildTime(2025, 11, 30));
        System.out.println( DateUtils.buildTime(2025, 12, 6));
    }

    @Test
    void TestHttp() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://sellingpartnerapi-na.amazon.com/listings/2021-08-01/items/A2K2SU3BU6HGRH?marketplaceIds=ATVPDKIKX0DER&includedData=summaries&sortBy=lastUpdatedDate&sortOrder=DESC&pageSize=10")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("", "")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }


}
