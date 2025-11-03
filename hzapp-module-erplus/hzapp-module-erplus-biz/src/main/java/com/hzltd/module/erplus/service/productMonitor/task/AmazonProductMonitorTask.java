package com.hzltd.module.erplus.service.productMonitor.task;

import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.service.productMonitor.ProductMonitorService;
import com.hzltd.module.erplus.service.productMonitor.crawlers.PlaywrightCrawler;
import com.hzltd.module.erplus.service.productMonitor.vo.MonitorMetricsVO;
import com.hzltd.module.erplus.service.productMonitor.vo.TaskConfig;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class AmazonProductMonitorTask extends AbstractProductMonitorTask{

    @Resource
    private ProductMonitorService productMonitorService;

    @Resource
    private SellPlatformService sellPlatformService;

    private SellPlatformDO sellPlatformDO;

    @PostConstruct
    public void init() {
        sellPlatformDO = sellPlatformService.getSellPlatformByCode(CrossPlatformEnum.AMAZON.getCode());
    }

    @Override
    public String getTaskName() {
        return "AmazonProductMonitorTask";
    }

    @Override
    protected List<MonitorMetricsVO> doCrawl() {
        List<ProductMonitorDO> productMonitors = productMonitorService.getProductMonitorToRun(sellPlatformDO.getId());
        PlaywrightCrawler playwrightCrawler = new PlaywrightCrawler();

        playwrightCrawler.crawl(productMonitors.get(0).getProductLink());
        return null;
    }

    @Override
    public void init(TaskConfig taskConfig) {
        super.init(taskConfig);
    }
}
