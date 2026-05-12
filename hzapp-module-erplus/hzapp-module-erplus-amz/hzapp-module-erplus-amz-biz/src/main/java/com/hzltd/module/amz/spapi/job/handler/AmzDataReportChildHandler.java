package com.hzltd.module.amz.spapi.job.handler;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.amz.dal.dataobject.AmzSearchTermsWeeklyDO;
import com.hzltd.module.amz.dal.mysql.AmzSearchTermsWeeklyMapper;
import com.hzltd.module.amz.spapi.api.AmazonReportService;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.TenantBaseTaskHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import software.amazon.spapi.models.reports.v2021_06_30.CreateReportSpecification;
import software.amazon.spapi.models.reports.v2021_06_30.Report;
import software.amazon.spapi.models.reports.v2021_06_30.ReportDocument;
import software.amazon.spapi.models.reports.v2021_06_30.ReportOptions;

import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * 亚马逊品牌分析 - 子任务处理器
 * 负责单个报表的申请、轮询与解析入库
 */
@Slf4j
@Component
public class AmzDataReportChildHandler extends TenantBaseTaskHandler {

    @Resource
    private AmazonReportService amazonReportService;

    @Resource
    private AmzSearchTermsWeeklyMapper amzSearchTermsWeeklyMapper;

    @Override
    public ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop) {
        String reportType = (String) task.getContext().get("reportType");
        log.info("[AmzDataReportChildHandler] 开始请求报告: {}, 店铺: {}", reportType, shop.getId());

        try {
            String amzReportId = amazonReportService.createReport(new ApiRequest<CreateReportSpecification>()
                    .setCrossPlatform(CrossPlatformEnum.AMAZON).setShopId(task.getShopId().toString()).setRequest(buildReportSpecification(task, shop, reportType)));
            
            task.getContext().put("amzReportId", amzReportId);
            return ErpTaskResult.builder().context(task.getContext()).build();
        } catch (Exception e) {
            log.error("[AmzDataReportChildHandler] 请求报告失败: {}", reportType, e);
            return ErpTaskResult.failed("请求报告失败: " + e.getMessage());
        }
    }

    @Override
    public ErpTaskResult doPoll(ErpTaskDTO task, ShopModel shop) {
        String reportType = (String) task.getContext().get("reportType");
        String amzReportId = (String) task.getContext().get("amzReportId");

        try {
            ApiRequest<String> req = new ApiRequest<String>().setShopId(task.getShopId().toString()).setRequest(amzReportId);
            Report report = amazonReportService.checkReportStatus(req);

            if (Report.ProcessingStatusEnum.DONE.equals(report.getProcessingStatus())) {
                log.info("[AmzDataReportChildHandler] 报告已就绪: {}", reportType);
                ReportDocument doc = amazonReportService.getReportDocument(req.setRequest(report.getReportDocumentId()));
                
                downloadAndProcess(doc.getUrl(), doc.getCompressionAlgorithm().getValue(), reportType, task);
                return ErpTaskResult.success();
            } else if (Report.ProcessingStatusEnum.FATAL.equals(report.getProcessingStatus()) || 
                       Report.ProcessingStatusEnum.CANCELLED.equals(report.getProcessingStatus())) {
                return ErpTaskResult.failed("亚马逊端报告生成失败: " + report.getProcessingStatus());
            }
        } catch (Exception e) {
            log.error("[AmzDataReportChildHandler] 轮询异常: {}", reportType, e);
        }

        // 继续轮询
        return ErpTaskResult.builder().context(task.getContext()).build();
    }

    private void downloadAndProcess(String url, String compression, String reportType, ErpTaskDTO task) {
        try {
            Response response = new OkHttpClient().newCall(new Request.Builder().url(url).get().build()).execute();
            if (!response.isSuccessful()) {
                log.error("[AmzDataReportChildHandler] 下载报告失败, HTTP 状态码: {}", response.code());
                return;
            }
            try (ResponseBody body = response.body()) {
                if (body == null) return;
                InputStream is = body.byteStream();
                if ("GZIP".equals(compression)) {
                    is = new GZIPInputStream(is);
                }
                processReport(reportType, is, task);
            }
        } catch (Exception e) {
            log.error("[AmzDataReportChildHandler] 下载解析报告异常: {}", reportType, e);
        }
    }

    private void processReport(String type, InputStream is, ErpTaskDTO task) {
        if ("GET_BRAND_ANALYTICS_SEARCH_TERMS_REPORT".equals(type)) {
            processSearchTerms(is, task);
        } else {
            log.warn("[AmzDataReportChildHandler] 暂未实现解析逻辑: {}", type);
        }
    }

    private void processSearchTerms(InputStream is, ErpTaskDTO task) {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        
        try (JsonParser parser = factory.createParser(is)) {
            String marketplaceId = "ATVPDKIKX0DER"; // 默认美国
            
            while (parser.nextToken() != null) {
                String fieldName = parser.getCurrentName();
                
                // 1. 获取 marketplaceId
                if ("marketplaceIds".equals(fieldName)) {
                    if (parser.nextToken() == JsonToken.START_ARRAY && parser.nextToken() != JsonToken.END_ARRAY) {
                        marketplaceId = parser.getText();
                    }
                }
                
                // 2. 流式解析 data 数组
                if ("data".equals(fieldName) && parser.nextToken() == JsonToken.START_ARRAY) {
                    List<AmzSearchTermsWeeklyDO> batch = new ArrayList<>();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        // 使用 ObjectMapper 将当前节点解析为 Map，减小内存压力
                        Map<String, Object> row = mapper.readValue(parser, Map.class);
                        
                        AmzSearchTermsWeeklyDO rowDO = AmzSearchTermsWeeklyDO.builder()
                                .date(task.getDateRangeStart())
                                .marketplaceId(marketplaceId)
                                .searchTerm((String) row.get("searchTerm"))
                                .searchFrequencyRank(Long.valueOf(row.get("searchFrequencyRank").toString()))
                                .updateTime(java.time.LocalDateTime.now())
                                .build();

                        List<Map<String, Object>> topAsins = (List<Map<String, Object>>) row.get("topClickedASINs");
                        if (CollUtil.isNotEmpty(topAsins)) {
                            fillTopAsins(rowDO, topAsins);
                        }
                        
                        batch.add(rowDO);
                        if (batch.size() >= 1000) {
                            amzSearchTermsWeeklyMapper.insertBatch(batch);
                            batch.clear();
                        }
                    }
                    if (CollUtil.isNotEmpty(batch)) {
                        amzSearchTermsWeeklyMapper.insertBatch(batch);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[AmzDataReportChildHandler] 解析搜索词报告异常", e);
        }
    }

    private void fillTopAsins(AmzSearchTermsWeeklyDO rowDO, List<Map<String, Object>> topAsins) {
        for (int i = 0; i < Math.min(topAsins.size(), 3); i++) {
            Map<String, Object> asin = topAsins.get(i);
            if (i == 0) {
                rowDO.setTop1ClickedAsin((String) asin.get("asin"));
                rowDO.setTop1ProductTitle((String) asin.get("productTitle"));
                rowDO.setTop1ClickShare(Double.valueOf(asin.get("clickShare").toString()));
                rowDO.setTop1ConversionShare(Double.valueOf(asin.get("conversionShare").toString()));
            } else if (i == 1) {
                rowDO.setTop2ClickedAsin((String) asin.get("asin"));
                rowDO.setTop2ProductTitle((String) asin.get("productTitle"));
                rowDO.setTop2ClickShare(Double.valueOf(asin.get("clickShare").toString()));
                rowDO.setTop2ConversionShare(Double.valueOf(asin.get("conversionShare").toString()));
            } else if (i == 2) {
                rowDO.setTop3ClickedAsin((String) asin.get("asin"));
                rowDO.setTop3ProductTitle((String) asin.get("productTitle"));
                rowDO.setTop3ClickShare(Double.valueOf(asin.get("clickShare").toString()));
                rowDO.setTop3ConversionShare(Double.valueOf(asin.get("conversionShare").toString()));
            }
        }
    }

    private OffsetDateTime convertToOffsetDateTime(java.time.LocalDate date) {
        return OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, ZoneOffset.UTC);
    }


    private CreateReportSpecification buildReportSpecification(ErpTaskDTO task, ShopModel shop, String reportType) {
        ReportOptions options = new ReportOptions();

        // 根据不同的报告类型设置不同的 options
        switch (reportType) {
            case "GET_BRAND_ANALYTICS_SEARCH_TERMS_REPORT":
            case "GET_BRAND_ANALYTICS_MARKET_BASKET_REPORT":
            case "GET_BRAND_ANALYTICS_SEARCH_CATALOG_PERFORMANCE_REPORT":
            case "GET_BRAND_ANALYTICS_SEARCH_QUERY_PERFORMANCE_REPORT":
                // 品牌分析报表目前主要按周同步
                options.put("reportPeriod", "WEEK");
                break;
            default:
                // 默认使用 WEEK，可根据需要扩展
                options.put("reportPeriod", "WEEK");
                break;
        }

        // 如果 context 中传递了特殊的 options，可以合并进来
        Map<String, Object> taskOptions = (Map<String, Object>) task.getContext().get("reportOptions");
        if (CollUtil.isNotEmpty(taskOptions)) {
            taskOptions.forEach((k, v) -> options.put(k, v.toString()));
        }

        CreateReportSpecification spec = new CreateReportSpecification()
                .reportType(reportType)
                .reportOptions(options)
                .dataStartTime(convertToOffsetDateTime(task.getDateRangeStart()))
                .dataEndTime(convertToOffsetDateTime(task.getDateRangeEnd()));

        return spec;
    }




    @Override
    public String getTaskType() {
        return AdsSyncTaskTypeEnum.AMZ_DATA_REPORT_CHILD.getCode();
    }
}
