package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportQueryReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AdsReportQuerySqlProvider {

    public String buildQuery(@Param("req") AdsReportQueryReqVO req, @Param("today") LocalDate today) {
        // Validation
        if (req.getShopId() == null) {
            throw new IllegalArgumentException("shopId must not be null for security isolation");
        }

        boolean queryBatch = !req.getEndDate().isBefore(req.getStartDate()) && !req.getStartDate().isAfter(today.minusDays(1));
        boolean queryStream = !req.getEndDate().isBefore(today);

        StringBuilder finalQuery = new StringBuilder();
        
        List<String> selectDims = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getDimensions())) {
            for (String dim : req.getDimensions()) {
                selectDims.add(escapeCol(dim));
            }
        }
        
        // 解析真正需要去 DB 拿的列 (处理派生指标依赖与映射)
        Set<String> dbMetricsToFetch = AdsReportMetricConfig.getPhysicalMetrics(req.getMetrics());

        List<String> selectMetrics = new ArrayList<>();
        for (String m : dbMetricsToFetch) {
            String[] parts = m.split(" AS ");
            String alias = parts.length > 1 ? parts[1] : parts[0];
            selectMetrics.add("SUM(" + escapeCol(alias) + ") AS " + escapeCol(alias));
        }

        // If no dimensions and no metrics, return empty safe query
        if (selectDims.isEmpty() && dbMetricsToFetch.isEmpty()) {
            return "SELECT 1 WHERE 1=0";
        }

        String batchSql = queryBatch ? buildBaseSql(req, "ads_report_batch", "report_date", dbMetricsToFetch) : "";
        String streamSql = queryStream ? buildBaseSql(req, "ads_report_stream_realtime", "DATE(window_start_time)", dbMetricsToFetch) : "";

        String unionSql = "";
        if (queryBatch && queryStream) {
            unionSql = batchSql + " UNION ALL " + streamSql;
        } else if (queryBatch) {
            unionSql = batchSql;
        } else if (queryStream) {
            unionSql = streamSql;
        } else {
            return "SELECT 1 WHERE 1=0";
        }

        finalQuery.append("SELECT ");
        List<String> finalSelects = new ArrayList<>();
        finalSelects.addAll(selectDims);
        finalSelects.addAll(selectMetrics);
        finalQuery.append(String.join(", ", finalSelects));
        
        finalQuery.append(" FROM ( ").append(unionSql).append(" ) AS wrapper ");

        if (!selectDims.isEmpty()) {
            finalQuery.append(" GROUP BY ").append(String.join(", ", selectDims));
        }

        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            finalQuery.append(" ORDER BY ").append(escapeCol(req.getOrderBy()));
            if (Boolean.TRUE.equals(req.getIsAsc())) {
                finalQuery.append(" ASC ");
            } else {
                finalQuery.append(" DESC ");
            }
        }

        if (req.getLimit() != null && req.getLimit() > 0) {
            finalQuery.append(" LIMIT ").append(req.getLimit());
        }

        log.info("[AdsReportQuerySqlProvider] buildQuery = {} ", finalQuery);
        return finalQuery.toString();
    }

    private String buildBaseSql(AdsReportQueryReqVO req, String table, String dateCol, Set<String> dbMetricsToFetch) {
        StringBuilder sql = new StringBuilder();
        
        List<String> selects = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getDimensions())) {
            for (String dim : req.getDimensions()) {
                selects.add(mapDimension(dim, dateCol) + " AS " + escapeCol(dim));
            }
        } else {
             // If no dims, put a dummy so UNION matches columns
             selects.add("1 AS dummy_dim"); 
        }

        if (!CollectionUtils.isEmpty(dbMetricsToFetch)) {
            for (String m : dbMetricsToFetch) {
                String[] parts = m.split(" AS ");
                String physical = escapeCol(parts[0]);
                String alias = parts.length > 1 ? escapeCol(parts[1]) : physical;
                selects.add(physical + " AS " + alias);
            }
        } else {
             selects.add("0 AS dummy_metric");
        }

        sql.append("SELECT ").append(String.join(", ", selects));
        sql.append(" FROM ").append(table);
        sql.append(" WHERE shop_id = #{req.shopId} ");
        
        sql.append(" AND ").append(dateCol).append(" >= #{req.startDate} ");
        sql.append(" AND ").append(dateCol).append(" <= #{req.endDate} ");

        // 离线表需要根据维度自动路由 RecordType 以防止重复计算指标
        if ("ads_report_batch".equals(table)) {
            String recordType = resolveRecordType(req);
            sql.append(" AND record_type = '").append(recordType).append("' ");
        }

        if (!CollectionUtils.isEmpty(req.getPlatforms())) {
            sql.append(" AND platform IN (");
            sql.append(req.getPlatforms().stream().map(p -> "'" + p.replace("'", "") + "'").collect(Collectors.joining(",")));
            sql.append(") ");
        }

        appendInClause(sql, "campaign_id", req.getCampaignIds());
        appendInClause(sql, "ad_group_id", req.getAdGroupIds());
        appendInClause(sql, "ad_id", req.getAdIds());
        appendInClause(sql, "keyword_id", req.getKeywordIds());
        appendInClause(sql, "placement", req.getPlacements());

        if (!CollectionUtils.isEmpty(req.getProductIds())) {
            sql.append(" AND product_asin IN (");
            sql.append(req.getProductIds().stream().map(p -> "'" + p.replace("'", "") + "'").collect(Collectors.joining(",")));
            sql.append(") ");
        }

        return sql.toString();
    }

    /**
     * 根据请求维度和过滤器，解析出应该查询的 record_type
     */
    private String resolveRecordType(AdsReportQueryReqVO req) {
        List<String> dimensions = req.getDimensions() == null ? new ArrayList<>() : req.getDimensions();

        // 优先级: 搜索词 > 广告位 > 关键词/针对对象 > 广告 > 广告组 > 广告活动
        // 只要维度包含或过滤器包含，就下钻到对应层级
        if (dimensions.contains("searchTerm")) return "SEARCH_TERM";
        if (dimensions.contains("placement") || !CollectionUtils.isEmpty(req.getPlacements())) return "PLACEMENT";
        if (dimensions.contains("keywordId") || !CollectionUtils.isEmpty(req.getKeywordIds())) return "TARGETING";
        if (dimensions.contains("adId") || !CollectionUtils.isEmpty(req.getAdIds())) return "AD";
        if (dimensions.contains("adGroupId") || !CollectionUtils.isEmpty(req.getAdGroupIds())) return "AD_GROUP";

        return "CAMPAIGN";
    }

    private void appendInClause(StringBuilder sql, String colName, List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            sql.append(" AND ").append(colName).append(" IN (");
            sql.append(ids.stream()
                    .map(id -> "'" + id.replace("'", "") + "'")
                    .collect(Collectors.joining(",")));
            sql.append(") ");
        }
    }

    private String mapDimension(String dim, String dateCol) {
        switch (dim) {
            case "date":
                return dateCol;
            case "week":
                // %U marks Sunday as the first day of the week (00-53)
                return "DATE_FORMAT(" + dateCol + ", '%Y-%U')";
            case "month":
                return "DATE_FORMAT(" + dateCol + ", '%Y-%m')";
            default:
                return escapeCol(dim);
        }
    }

    private String escapeCol(String col) {
        // Basic security against injection since column names are dynamic
        return "`" + col.replaceAll("[^a-zA-Z0-9_]", "") + "`";
    }
}
