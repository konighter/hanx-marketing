package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.module.erplus.adv.metadata.vo.report.AdsReportQueryReqVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdsReportQuerySqlProvider {

    public String buildQuery(@Param("req") AdsReportQueryReqVO req, @Param("today") LocalDate today) {
        // Validation
        if (req.getShopId() == null) {
            throw new IllegalArgumentException("shopId must not be null for security isolation");
        }

        boolean queryBatch = !req.getEndDate().isBefore(req.getStartDate()) && !req.getStartDate().isAfter(today.minusDays(1));
        boolean queryStream = !req.getEndDate().isBefore(today);

        String batchSql = queryBatch ? buildBaseSql(req, "ads_report_batch", "report_date") : "";
        String streamSql = queryStream ? buildBaseSql(req, "ads_report_stream_realtime", "DATE(window_start_time)") : "";

        String unionSql = "";
        if (queryBatch && queryStream) {
            unionSql = batchSql + " UNION ALL " + streamSql;
        } else if (queryBatch) {
            unionSql = batchSql;
        } else if (queryStream) {
            unionSql = streamSql;
        } else {
            // Edge case
            unionSql = "SELECT 1 WHERE 1=0";
        }

        // Final query wraps the union if we need GROUP BY again, BUT we can just construct the exact group by inside the parts
        // Wait, if both batch and stream return rows for the same entity, do we need an outer query to SUM them together?
        // Yes! Stream could have today's clicks for Campaign 1, Batch has yesterday's clicks for Campaign 1. If we group by Campaign, we must sum the union.

        StringBuilder finalQuery = new StringBuilder();
        
        List<String> selectDims = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getDimensions())) {
            for (String dim : req.getDimensions()) {
                selectDims.add(escapeCol(dim));
            }
        }
        
        List<String> selectMetrics = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getMetrics())) {
            for (String metric : req.getMetrics()) {
                selectMetrics.add("SUM(" + escapeCol(metric) + ") AS " + escapeCol(metric));
            }
        }

        // If no dimensions and no metrics, return empty safe query
        if (selectDims.isEmpty() && selectMetrics.isEmpty()) {
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

        return finalQuery.toString();
    }

    private String buildBaseSql(AdsReportQueryReqVO req, String table, String dateCol) {
        StringBuilder sql = new StringBuilder();
        
        List<String> selects = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getDimensions())) {
            for (String dim : req.getDimensions()) {
                selects.add(escapeCol(dim));
            }
        } else {
             // If no dims, put a dummy so UNION matches columns
             selects.add("1 AS dummy_dim"); 
        }

        if (!CollectionUtils.isEmpty(req.getMetrics())) {
            for (String metric : req.getMetrics()) {
                selects.add(escapeCol(metric));
            }
        } else {
             selects.add("0 AS dummy_metric");
        }

        sql.append("SELECT ").append(String.join(", ", selects));
        sql.append(" FROM ").append(table);
        sql.append(" WHERE shop_id = #{req.shopId} ");
        
        sql.append(" AND ").append(dateCol).append(" >= #{req.startDate} ");
        sql.append(" AND ").append(dateCol).append(" <= #{req.endDate} ");

        if (!CollectionUtils.isEmpty(req.getPlatforms())) {
            sql.append(" AND platform IN (");
            sql.append(req.getPlatforms().stream().map(p -> "'" + p.replace("'", "") + "'").collect(Collectors.joining(",")));
            sql.append(") ");
        }

        appendInClause(sql, "campaign_id", req.getCampaignIds());
        appendInClause(sql, "ad_group_id", req.getAdGroupIds());
        appendInClause(sql, "ad_id", req.getAdIds());
        appendInClause(sql, "keyword_id", req.getKeywordIds());

        if (!CollectionUtils.isEmpty(req.getProductIds())) {
            sql.append(" AND product_asin IN (");
            sql.append(req.getProductIds().stream().map(p -> "'" + p.replace("'", "") + "'").collect(Collectors.joining(",")));
            sql.append(") ");
        }

        return sql.toString();
    }

    private void appendInClause(StringBuilder sql, String colName, List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            sql.append(" AND ").append(colName).append(" IN (");
            sql.append(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }
    }

    private String escapeCol(String col) {
        // Basic security against injection since column names are dynamic
        return "`" + col.replaceAll("[^a-zA-Z0-9_]", "") + "`";
    }
}
