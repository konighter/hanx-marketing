package com.hzltd.module.erplus.service.productMonitor.crawlers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import io.github.kihdev.playwright.stealth4j.Stealth4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PlaywrightCrawler {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightCrawler.class);

    private Playwright playwright;
    private final Browser browser;

    public PlaywrightCrawler() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(false);
        this.browser = playwright.chromium().launch(launchOptions);
    }

    public String crawl(String url) {
        try (BrowserContext context = Stealth4j.newStealthContext(browser)) {
            Page page = context.newPage();
            page.navigate(url);
            // 等待页面加载完成
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(RandomUtils.secure().randomLong(1000, 10000));

            page.evaluate("() => { window.scrollTo(0, document.body.scrollHeight); }");
            page.mouse().wheel(0, 1000);
            page.waitForTimeout(RandomUtils.secure().randomLong(1000, 10000));



            Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions()
                    .setFullPage(true);
            FileUtils.writeByteArrayToFile(new File("screenshot.png"), page.screenshot(screenshotOptions), false);

//            List<String> title = page.querySelector("#productTitle", "UTF-8");
//            logger.info("ProductTitle: {}", title);
            // 提取页面内容
            return page.title();
        } catch (IOException e) {
            throw new RuntimeException("Failed to crawl URL: " + url, e);
        }
    }


}
