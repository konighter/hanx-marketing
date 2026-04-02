package com.hzapp.module.erplus;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeTest {

    @Test
    public void test() {

        LocalDateTime localDateTime = LocalDateTime.of(2026, 3, 1, 0,0,0,0);

        System.out.println(localDateTime.atZone(ZoneId.of("America/Los_Angeles")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

//        System.out.println(OffsetDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));





        OffsetDateTime.now(ZoneId.systemDefault());
    }


}
