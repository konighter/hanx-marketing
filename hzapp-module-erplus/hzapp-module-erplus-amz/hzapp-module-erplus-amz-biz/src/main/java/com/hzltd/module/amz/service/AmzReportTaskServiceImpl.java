package com.hzltd.module.amz.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.spapi.AmazonReportService;
import com.hzltd.module.amz.controller.admin.vo.AmzReportTaskPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzReportTaskSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzReportTaskDO;
import com.hzltd.module.amz.dal.mysql.AmzReportTaskMapper;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import software.amazon.spapi.models.reports.v2021_06_30.CreateReportSpecification;
import software.amazon.spapi.models.reports.v2021_06_30.Report;
import software.amazon.spapi.models.reports.v2021_06_30.ReportDocument;
import software.amazon.spapi.models.reports.v2021_06_30.ReportOptions;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;


/**
 * 亚马逊报告任务 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
@Validated
public class AmzReportTaskServiceImpl implements AmzReportTaskService {


    private static final String WEEK = "WEEK";
    private static final String MONTH = "MONTH";
    private static final String YEAR = "YEAR";


    @Resource
    private AmzReportTaskMapper amzReportTaskMapper;

    @Resource
    private AmazonReportService amazonReportService;

    @Override
    public Long createAmzReportTask(AmzReportTaskSaveReqVO createReqVO) {

        ApiRequest<CreateReportSpecification> apiRequest = new ApiRequest<CreateReportSpecification>().setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setShopId(createReqVO.getShopId().toString());

        Pair<OffsetDateTime, OffsetDateTime> timePeriod = buildTimePeriod(createReqVO);

        CreateReportSpecification createReportSpecification = new CreateReportSpecification()
                .reportType(createReqVO.getReportType())
                .reportOptions(buildReportOptions(createReqVO))
                .dataEndTime(timePeriod.getRight())
                .dataStartTime(timePeriod.getLeft());

        apiRequest.setRequest(createReportSpecification);

        String reportId = amazonReportService.createReport(apiRequest);

        AmzReportTaskDO amzReportTask = new AmzReportTaskDO();
        amzReportTask.setReportId(reportId);
        amzReportTask.setReportType(createReqVO.getReportType());
        amzReportTask.setQuery(JsonUtils.toJsonString(createReqVO));
        amzReportTask.setStatus(Report.ProcessingStatusEnum.IN_QUEUE.ordinal());
        // 插入
        amzReportTaskMapper.insert(amzReportTask);

        // 返回
        return amzReportTask.getId();
    }

    @Async
    @Override
    public void checkReportStatus(Long id) {
        AmzReportTaskDO task = getAmzReportTask(id);
        if (task == null) {
            return;
        }

        if (!(Report.ProcessingStatusEnum.IN_QUEUE.ordinal() == task.getStatus() || Report.ProcessingStatusEnum.IN_PROGRESS.ordinal() == task.getStatus())) {
            // 只有处理中的才去更新
            return;
        }


        AmzReportTaskSaveReqVO query = JsonUtils.parseObject(task.getQuery(), AmzReportTaskSaveReqVO.class);

        Report report = amazonReportService.checkReportStatus(new ApiRequest<String>().setShopId(query.getShopId().toString()).setRequest(task.getReportId()));
        task.setStatus(report.getProcessingStatus().ordinal());
        task.setLastCheckTime(DateTime.now().toLocalDateTime());
        if (Report.ProcessingStatusEnum.DONE.equals(report.getProcessingStatus()) || Report.ProcessingStatusEnum.FATAL.equals(report.getProcessingStatus())) {
            ReportDocument document = amazonReportService.getReportDocument(new ApiRequest<String>().setShopId(query.getShopId().toString()).setRequest(report.getReportDocumentId()));
            task.setReportResult(downloadContent(document.getUrl(), document.getCompressionAlgorithm().getValue()));
        }

        amzReportTaskMapper.updateById(task);

        if (Report.ProcessingStatusEnum.DONE.equals(report.getProcessingStatus())) {
            // todo-- 处理指标文件
            log.info("reportId: {}, document: {}", task.getReportId(), task.getReportResult());
        }
    }


    @Override
    public AmzReportTaskDO getAmzReportTask(Long id) {
        return amzReportTaskMapper.selectById(id);
    }

    @Override
    public PageResult<AmzReportTaskDO> getAmzReportTaskPage(AmzReportTaskPageReqVO pageReqVO) {
        return amzReportTaskMapper.selectPage(pageReqVO);
    }

    private ReportOptions buildReportOptions(AmzReportTaskSaveReqVO createReqVO) {
        ReportOptions reportOptions = new ReportOptions();
        reportOptions.put("reportPeriod", createReqVO.getPeriod());
        if (StringUtils.isNotEmpty(createReqVO.getAsins())) {
            reportOptions.put("asins", createReqVO.getAsins());
        }
        return reportOptions;
    }

    private Pair<OffsetDateTime, OffsetDateTime> buildTimePeriod(AmzReportTaskSaveReqVO createReqVO) {

        createReqVO.setReportStartDate(DateUtil.parse(createReqVO.getDateRange().get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        createReqVO.setReportEndDate(DateUtil.parse(createReqVO.getDateRange().get(1), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        String startTime = "";
        String endTime = "";
        if (WEEK.equals(createReqVO.getPeriod())) {
            return Pair.of(convert(DateUtil.beginOfWeek(createReqVO.getReportStartDate(), false).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()),
                    convert(DateUtil.endOfWeek(createReqVO.getReportEndDate(), false).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()));
        } else if (MONTH.equals(createReqVO.getPeriod())) {
            return Pair.of(convert(DateUtil.beginOfMonth(createReqVO.getReportStartDate()).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()),
                    convert(DateUtil.endOfMonth(createReqVO.getReportEndDate()).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()));
        } else if (YEAR.equals(createReqVO.getPeriod())) {
            return Pair.of(convert(DateUtil.beginOfYear(createReqVO.getReportStartDate()).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()),
                    convert(DateUtil.endOfYear(createReqVO.getReportEndDate()).toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()));
        } else {
            throw new RuntimeException("不支持的时间周期");
        }

    }

    private OffsetDateTime convert(java.time.OffsetDateTime javaTime) {
        return OffsetDateTime.of(javaTime.getYear(), javaTime.getMonth().getValue(), javaTime.getDayOfMonth(), 0, 0, 0, 0, ZoneOffset.UTC);
    }


    public String downloadContent(String url, String compressionAlgorithm) {
        try {


            OkHttpClient httpclient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = httpclient.newCall(request).execute();
            if (!response.isSuccessful()) {
                return StringUtils.EMPTY;
            }

            try (ResponseBody responseBody = response.body()) {
                MediaType mediaType = MediaType.parse(response.header("Content-Type"));
                Charset charset = mediaType.charset();
                if (charset == null) {
                    throw new IllegalArgumentException(String.format(
                            "Could not parse character set from '%s'", mediaType.toString()));
                }

                Closeable closeThis = null;
                try {
                    InputStream inputStream = responseBody.byteStream();
                    closeThis = inputStream;

                    if ("GZIP".equals(compressionAlgorithm)) {
                        inputStream = new GZIPInputStream(inputStream);
                        closeThis = inputStream;
                    }

                    if (responseBody.contentLength() > 10 * 1024 * 1024 * 1024) {
                        File tmpFile = FileUtil.touch("~/tmp/report-"+ RandomUtils.secure().randomInt());
                        FileUtil.writeFromStream(inputStream, tmpFile);
                        return tmpFile.getPath()+"/"+tmpFile.getName();
                    } else {
                        return IOUtils.toString(inputStream, charset);
                    }

                } finally {
                    if (closeThis != null) {
                        closeThis.close();
                    }
                }
            }
        } catch (Exception e) {

        }
        return "";
    }


}