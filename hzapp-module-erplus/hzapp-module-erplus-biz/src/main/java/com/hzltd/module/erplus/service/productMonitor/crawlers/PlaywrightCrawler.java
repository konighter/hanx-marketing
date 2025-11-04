package com.hzltd.module.erplus.service.productMonitor.crawlers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class PlaywrightCrawler {

    private Playwright playwright;
    private final Browser browser;

    public PlaywrightCrawler() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true);
        this.browser = playwright.chromium().launch(launchOptions);
    }

    public String crawl(String url) {
        try (BrowserContext context = browser.newContext()) {
            Page page = context.newPage();
            page.navigate(url);
            // 等待页面加载完成
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            // 提取页面内容
            return page.content();
        }
    }


}
