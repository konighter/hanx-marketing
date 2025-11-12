package com.hzltd.module.erplus.service.productMonitor;

import com.hzltd.module.erplus.service.productMonitor.crawlers.PlaywrightCrawler;
import org.junit.jupiter.api.Test;


public class PlaywrightTest  {


    @Test
    public void testCrawl() {
        PlaywrightCrawler crawler = new PlaywrightCrawler();
        String metrics = crawler.crawl("https://www.amazon.com/dp/B0F9PF5ZLK");
        System.out.println(metrics);
    }



}
