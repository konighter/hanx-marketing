package com.hzltd.module.amz.spapi.job.handler;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.amz.dal.dataobject.AmzBrandPerformanceReportDO;
import com.hzltd.module.amz.dal.mysql.AmzBrandPerformanceReportMapper;
import com.hzltd.module.amz.spapi.api.AmazonReportService;
import com.hzltd.module.erplus.spapi.enums.CrossListingStatus;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import com.hzltd.module.erplus.spapi.service.product.CrossProductService;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.enums.ErpTaskResultStatusEnum;
import com.hzltd.module.erplus.system.enums.ScheduleTaskTypeEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.TenantBaseTaskHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import software.amazon.spapi.models.reports.v2021_06_30.CreateReportSpecification;
import software.amazon.spapi.models.reports.v2021_06_30.Report;
import software.amazon.spapi.models.reports.v2021_06_30.ReportDocument;
import software.amazon.spapi.models.reports.v2021_06_30.ReportOptions;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * 亚马逊品牌分析 - 性能报表处理器 (SCP/SQP)
 * 负责 SCP 和 SQP 报表的申请、轮询与解析入库
 */
@Slf4j
@Component
public class AmzDataReportPerformanceHandler extends TenantBaseTaskHandler {

    @Resource
    private AmazonReportService amazonReportService;

    @Resource
    private AmzBrandPerformanceReportMapper brandPerformanceReportMapper;

    @Resource
    private CrossProductService crossProductService;

    @Override
    public ErpTaskResult doStart(ErpTaskDTO task, ShopModel shop) {
        String reportType = (String) task.getContext().get("reportType");
        log.info("[AmzDataReportPerformanceHandler] 开始请求性能报告: {}, 店铺: {}", reportType, shop.getId());

        try {
            String amzReportId = amazonReportService.createReport(new ApiRequest<CreateReportSpecification>()
                    .setCrossPlatform(CrossPlatformEnum.AMAZON).setShopId(task.getShopId().toString()).setRequest(buildReportSpecification(task, shop, reportType)));
            
            task.getContext().put("amzReportId", amzReportId);
            return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUBMITTED.getStatus()).context(task.getContext()).build();
        } catch (Exception e) {
            log.error("[AmzDataReportPerformanceHandler] 请求性能报告失败: {}", reportType, e);
            return ErpTaskResult.failed("请求性能报告失败: " + e.getMessage());
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
                log.info("[AmzDataReportPerformanceHandler] 性能报告已就绪: {}", reportType);
                ReportDocument doc = amazonReportService.getReportDocument(req.setRequest(report.getReportDocumentId()));
                
                downloadAndProcess(doc.getUrl(), doc.getCompressionAlgorithm().getValue(), reportType, task);
                return ErpTaskResult.success();
            } else if (Report.ProcessingStatusEnum.FATAL.equals(report.getProcessingStatus()) || 
                       Report.ProcessingStatusEnum.CANCELLED.equals(report.getProcessingStatus())) {
                return ErpTaskResult.failed("亚马逊端性能报告生成失败: " + report.getProcessingStatus());
            }
        } catch (Exception e) {
            log.error("[AmzDataReportPerformanceHandler] 性能报表轮询异常: {}", reportType, e);
        }

        // 继续轮询
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUBMITTED.getStatus()).context(task.getContext()).build();
    }

    void downloadAndProcess(String url, String compression, String reportType, ErpTaskDTO task) {
        try {
            Response response = new OkHttpClient().newCall(new Request.Builder().url(url).get().build()).execute();
            if (!response.isSuccessful()) {
                log.error("[AmzDataReportPerformanceHandler] 下载性能报告失败, HTTP 状态码: {}", response.code());
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
            log.error("[AmzDataReportPerformanceHandler] 下载解析性能报告异常: {}", reportType, e);
        }
    }

    private void processReport(String type, InputStream is, ErpTaskDTO task) {
        if ("GET_BRAND_ANALYTICS_SEARCH_CATALOG_PERFORMANCE_REPORT".equals(type)) {
            processScpReport(is, task);
        } else if ("GET_BRAND_ANALYTICS_SEARCH_QUERY_PERFORMANCE_REPORT".equals(type)) {
            processSqpReport(is, task);
        } else {
            log.warn("[AmzDataReportPerformanceHandler] 未知报告类型: {}", type);
        }
    }

    /**
     * 解析 SCP 报表 (Search Catalog Performance)
     */
    private void processScpReport(InputStream is, ErpTaskDTO task) {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        try (JsonParser parser = factory.createParser(is)) {
            String marketplaceId = "ATVPDKIKX0DER";

            while (parser.nextToken() != null) {
                String fieldName = parser.getCurrentName();
                if ("marketplaceIds".equals(fieldName)) {
                    if (parser.nextToken() == JsonToken.START_ARRAY && parser.nextToken() != JsonToken.END_ARRAY) {
                        marketplaceId = parser.getText();
                    }
                }

                if ("dataByAsin".equals(fieldName) && parser.nextToken() == JsonToken.START_ARRAY) {
                    List<AmzBrandPerformanceReportDO> batch = new ArrayList<>();
                    while (parser.nextToken() == JsonToken.START_OBJECT) {
                        Map<String, Object> node = mapper.readValue(parser, Map.class);

                        AmzBrandPerformanceReportDO rowDO = AmzBrandPerformanceReportDO.builder()
                                .startDate(LocalDate.parse((String) node.get("startDate")))
                                .endDate(LocalDate.parse((String) node.get("endDate")))
                                .shopId(task.getShopId().longValue())
                                .marketplaceId(marketplaceId)
                                .reportType("SCP")
                                .asin((String) node.get("asin"))
                                .searchQuery("") // SCP 不包含关键词维度
                                .updateTime(LocalDateTime.now())
                                .build();

                        // 1. 展现数据
                        Map<String, Object> imp = (Map<String, Object>) node.get("impressionData");
                        if (imp != null) {
                            rowDO.setAsinImpressionCount(getLongNumber(imp.get("impressionCount")));
                            rowDO.setImpressionMedianPrice(getAmount(imp.get("impressionMedianPrice")));
                            rowDO.setSamedayShippingImpressionCount(getLongNumber(imp.get("sameDayShippingImpressionCount")));
                            rowDO.setOnedayShippingImpressionCount(getLongNumber(imp.get("oneDayShippingImpressionCount")));
                            rowDO.setTwodayShippingImpressionCount(getLongNumber(imp.get("twoDayShippingImpressionCount")));
                        }

                        // 2. 点击数据
                        Map<String, Object> click = (Map<String, Object>) node.get("clickData");
                        if (click != null) {
                            rowDO.setAsinClickCount(getLongNumber(click.get("clickCount")));
                            rowDO.setAsinClickRate(getDoubleNumber(click.get("clickRate")));
                            rowDO.setAsinMedianClickPrice(getAmount(click.get("clickedMedianPrice")));
                            rowDO.setSamedayShippingClickCount(getLongNumber(click.get("sameDayShippingClickCount")));
                            rowDO.setOnedayShippingClickCount(getLongNumber(click.get("oneDayShippingClickCount")));
                            rowDO.setTwodayShippingClickCount(getLongNumber(click.get("twoDayShippingClickCount")));
                        }

                        // 3. 加购数据
                        Map<String, Object> cart = (Map<String, Object>) node.get("cartAddData");
                        if (cart != null) {
                            rowDO.setAsinCartAddCount(getLongNumber(cart.get("cartAddCount")));
                            rowDO.setAsinMedianCartAddPrice(getAmount(cart.get("cartAddedMedianPrice")));
                            rowDO.setSamedayShippingCartAddCount(getLongNumber(cart.get("sameDayShippingCartAddCount")));
                            rowDO.setOnedayShippingCartAddCount(getLongNumber(cart.get("oneDayShippingCartAddCount")));
                            rowDO.setTwodayShippingCartAddCount(getLongNumber(cart.get("twoDayShippingCartAddCount")));
                        }

                        // 4. 购买数据
                        Map<String, Object> purchase = (Map<String, Object>) node.get("purchaseData");
                        if (purchase != null) {
                            rowDO.setAsinPurchaseCount(getLongNumber(purchase.get("purchaseCount")));
                            rowDO.setAsinPurchaseSales(getAmount(purchase.get("searchTrafficSales")));
                            rowDO.setAsinConversionRate(getDoubleNumber(purchase.get("conversionRate")));
                            rowDO.setAsinMedianPurchasePrice(getAmount(purchase.get("purchaseMedianPrice")));
                            rowDO.setSamedayShippingPurchaseCount(getLongNumber(purchase.get("sameDayShippingPurchaseCount")));
                            rowDO.setOnedayShippingPurchaseCount(getLongNumber(purchase.get("oneDayShippingPurchaseCount")));
                            rowDO.setTwodayShippingPurchaseCount(getLongNumber(purchase.get("twoDayShippingPurchaseCount")));
                        }

                        batch.add(rowDO);
                        if (batch.size() >= 500) {
                            brandPerformanceReportMapper.insertBatch(batch);
                            batch.clear();
                        }
                    }
                    if (!batch.isEmpty()) brandPerformanceReportMapper.insertBatch(batch);
                }
            }
        } catch (Exception e) {
            log.error("[AmzDataReportPerformanceHandler] 解析 SCP 报告异常", e);
        }
    }

    /**
     * 解析 SQP 报表 (Search Query Performance)
     */
    private void processSqpReport(InputStream is, ErpTaskDTO task) {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        try (JsonParser parser = factory.createParser(is)) {
            String marketplaceId = "ATVPDKIKX0DER";

            while (parser.nextToken() != null) {
                String fieldName = parser.getCurrentName();
                if ("marketplaceIds".equals(fieldName)) {
                    if (parser.nextToken() == JsonToken.START_ARRAY && parser.nextToken() != JsonToken.END_ARRAY) {
                        marketplaceId = parser.getText();
                    }
                }

                if ("dataByAsin".equals(fieldName) && parser.nextToken() == JsonToken.START_ARRAY) {
                    List<AmzBrandPerformanceReportDO> batch = new ArrayList<>();
                    while (parser.nextToken() == JsonToken.START_OBJECT) {
                        Map<String, Object> node = mapper.readValue(parser, Map.class);

                        AmzBrandPerformanceReportDO rowDO = AmzBrandPerformanceReportDO.builder()
                                .startDate(LocalDate.parse((String) node.get("startDate")))
                                .endDate(LocalDate.parse((String) node.get("endDate")))
                                .shopId(task.getShopId().longValue())
                                .marketplaceId(marketplaceId)
                                .reportType("SQP")
                                .asin((String) node.get("asin"))
                                .updateTime(LocalDateTime.now())
                                .build();

                        // 1. 关键词信息
                        Map<String, Object> qData = (Map<String, Object>) node.get("searchQueryData");
                        if (qData != null) {
                            rowDO.setSearchQuery((String) qData.get("searchQuery"));
                            rowDO.setSearchQueryScore(getLongNumber(qData.get("searchQueryScore")));
                            rowDO.setSearchQueryVolume(getLongNumber(qData.get("searchQueryVolume")));
                        }

                        // 2. 展现数据
                        Map<String, Object> imp = (Map<String, Object>) node.get("impressionData");
                        if (imp != null) {
                            rowDO.setAsinImpressionCount(getLongNumber(imp.get("asinImpressionCount")));
                            rowDO.setTotalImpressionCount(getLongNumber(imp.get("totalQueryImpressionCount")));
                            rowDO.setImpressionShare(getDoubleNumber(imp.get("asinImpressionShare")));
                        }

                        // 3. 点击数据
                        Map<String, Object> click = (Map<String, Object>) node.get("clickData");
                        if (click != null) {
                            rowDO.setAsinClickCount(getLongNumber(click.get("asinClickCount")));
                            rowDO.setTotalClickCount(getLongNumber(click.get("totalClickCount")));
                            rowDO.setAsinClickShare(getDoubleNumber(click.get("asinClickShare")));
                            rowDO.setAsinClickRate(getDoubleNumber(click.get("asinClickRate")));
                            rowDO.setTotalClickRate(getDoubleNumber(click.get("totalClickRate")));
                            rowDO.setAsinMedianClickPrice(getAmount(click.get("asinMedianClickPrice")));
                            rowDO.setTotalMedianClickPrice(getAmount(click.get("totalMedianClickPrice")));
                            rowDO.setSamedayShippingClickCount(getLongNumber(click.get("totalSameDayShippingClickCount")));
                            rowDO.setOnedayShippingClickCount(getLongNumber(click.get("totalOneDayShippingClickCount")));
                            rowDO.setTwodayShippingClickCount(getLongNumber(click.get("totalTwoDayShippingClickCount")));
                        }

                        // 4. 加购数据
                        Map<String, Object> cart = (Map<String, Object>) node.get("cartAddData");
                        if (cart != null) {
                            rowDO.setAsinCartAddCount(getLongNumber(cart.get("asinCartAddCount")));
                            rowDO.setTotalCartAddCount(getLongNumber(cart.get("totalCartAddCount")));
                            rowDO.setAsinCartAddShare(getDoubleNumber(cart.get("asinCartAddShare")));
                            rowDO.setTotalCartAddRate(getDoubleNumber(cart.get("totalCartAddRate")));
                            rowDO.setAsinMedianCartAddPrice(getAmount(cart.get("asinMedianCartAddPrice")));
                            rowDO.setTotalMedianCartAddPrice(getAmount(cart.get("totalMedianCartAddPrice")));
                            rowDO.setSamedayShippingCartAddCount(getLongNumber(cart.get("totalSameDayShippingCartAddCount")));
                            rowDO.setOnedayShippingCartAddCount(getLongNumber(cart.get("totalOneDayShippingCartAddCount")));
                            rowDO.setTwodayShippingCartAddCount(getLongNumber(cart.get("totalTwoDayShippingCartAddCount")));
                        }

                        // 5. 购买数据
                        Map<String, Object> purchase = (Map<String, Object>) node.get("purchaseData");
                        if (purchase != null) {
                            rowDO.setAsinPurchaseCount(getLongNumber(purchase.get("asinPurchaseCount")));
                            rowDO.setTotalPurchaseCount(getLongNumber(purchase.get("totalPurchaseCount")));
                            rowDO.setAsinPurchaseShare(getDoubleNumber(purchase.get("asinPurchaseShare")));
                            rowDO.setAsinConversionRate(getDoubleNumber(purchase.get("asinPurchaseRate")));
                            rowDO.setTotalPurchaseRate(getDoubleNumber(purchase.get("totalPurchaseRate")));
                            rowDO.setAsinMedianPurchasePrice(getAmount(purchase.get("asinMedianPurchasePrice")));
                            rowDO.setTotalMedianPurchasePrice(getAmount(purchase.get("totalMedianPurchasePrice")));
                            rowDO.setSamedayShippingPurchaseCount(getLongNumber(purchase.get("totalSameDayShippingPurchaseCount")));
                            rowDO.setOnedayShippingPurchaseCount(getLongNumber(purchase.get("totalOneDayShippingPurchaseCount")));
                            rowDO.setTwodayShippingPurchaseCount(getLongNumber(purchase.get("totalTwoDayShippingPurchaseCount")));
                        }

                        batch.add(rowDO);
                        if (batch.size() >= 500) {
                            brandPerformanceReportMapper.insertBatch(batch);
                            batch.clear();
                        }
                    }
                    if (!batch.isEmpty()) brandPerformanceReportMapper.insertBatch(batch);
                }
            }
        } catch (Exception e) {
            log.error("[AmzDataReportPerformanceHandler] 解析 SQP 报告异常", e);
        }
    }

    private Double getAmount(Object obj) {
        if (obj instanceof Map) {
            Object amount = ((Map<?, ?>) obj).get("amount");
            return amount != null ? Double.parseDouble(amount.toString()) : 0.0;
        }
        return 0.0;
    }

    private Long getLongNumber(Object obj) {
        return obj != null ? Long.parseLong(obj.toString()) : 0L;
    }

    private Double getDoubleNumber(Object obj) {
        return obj != null ? Double.parseDouble(obj.toString()) : 0.0;
    }

    private CreateReportSpecification buildReportSpecification(ErpTaskDTO task, ShopModel shop, String reportType) {
        ReportOptions options = new ReportOptions();
        options.put("reportPeriod", "WEEK");

        Map<String, Object> taskOptions = (Map<String, Object>) task.getContext().get("reportOptions");
        if (CollUtil.isNotEmpty(taskOptions)) {
            taskOptions.forEach((k, v) -> options.put(k, v.toString()));
        }

        if ("GET_BRAND_ANALYTICS_SEARCH_QUERY_PERFORMANCE_REPORT".equalsIgnoreCase(reportType)) {
            if (!options.containsKey("asin")) {
                List<ProductModel> products = crossProductService.getProductModel(shop.getId().longValue(), List.of());
                if (CollectionUtils.isNotEmpty(products)) {
                    String asins = products.stream().filter(p -> CrossListingStatus.ACTIVATE.equals(p.getStatus())).map(ProductModel::getProductCode).collect(Collectors.joining(" "));
                    options.put("asin", asins);
                }
            }
        }

        return new CreateReportSpecification()
                .reportType(reportType)
                .reportOptions(options)
                .dataStartTime(convertToOffsetDateTime(task.getDateRangeStart()))
                .dataEndTime(convertToOffsetDateTime(task.getDateRangeEnd()));
    }

    private org.threeten.bp.OffsetDateTime convertToOffsetDateTime(java.time.LocalDate date) {
        return org.threeten.bp.OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, org.threeten.bp.ZoneOffset.UTC);
    }

    @Override
    public String getTaskType() {
        return ScheduleTaskTypeEnum.AMZ_DATA_REPORT_PERFORMANCE.getCode();
    }
}
