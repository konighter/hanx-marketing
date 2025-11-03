package com.hzltd.module.erplus.service.productMonitor;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.service.productMonitor.crawlers.PlaywrightCrawler;
import com.hzltd.module.erplus.service.productMonitor.vo.MonitorMetricsVO;
import org.junit.jupiter.api.Test;


public class PlaywrightTest  {


    @Test
    public void testCrawl() {
        PlaywrightCrawler crawler = new PlaywrightCrawler();
        String metrics = crawler.crawl("https://www.baidu.com");
        System.out.println(metrics);
    }



}
