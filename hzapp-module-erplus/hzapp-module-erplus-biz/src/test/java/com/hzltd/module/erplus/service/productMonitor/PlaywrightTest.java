package com.hzltd.module.erplus.service.productMonitor;

import com.hzltd.module.erplus.service.productMonitor.crawlers.PlaywrightCrawler;
import org.junit.jupiter.api.Test;


public class PlaywrightTest  {


    @Test
    public void testCrawl() {
        PlaywrightCrawler crawler = new PlaywrightCrawler();
        String metrics = crawler.crawl("https://www.baidu.com");
        System.out.println(metrics);
    }



}
