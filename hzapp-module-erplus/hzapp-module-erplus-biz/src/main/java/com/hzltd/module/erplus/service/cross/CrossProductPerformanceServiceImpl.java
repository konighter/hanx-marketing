package com.hzltd.module.erplus.service.cross;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.hzltd.module.erplus.controller.admin.cross.vo.ListingPerformanceDTO;
import com.hzltd.module.erplus.dal.mysql.cross.ErpCrossOrderItemMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 跨境产品表现服务实现类
 * 直接从 erplus_cross_order_item 表查询全量订单销售数据 (MySQL)
 */
@Service
public class CrossProductPerformanceServiceImpl implements CrossProductPerformanceService {

    @Resource
    private ErpCrossOrderItemMapper erpCrossOrderItemMapper;

    @Override
    public ListingPerformanceDTO getPerformance(Long productId, String sellerSku) {
        Map<Long, ListingPerformanceDTO> map = getBatchPerformance(Collections.singletonList(productId));
        return map.getOrDefault(productId, new ListingPerformanceDTO(0, 0, 0, 0L, 0.0, Collections.emptyList()));
    }

    @Override
    public Map<Long, ListingPerformanceDTO> getBatchPerformance(Collection<Long> productIds) {
        if (CollectionUtil.isEmpty(productIds)) {
            return Collections.emptyMap();
        }

        Map<Long, ListingPerformanceDTO> resultMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        // 查询最近 30 天的数据
        LocalDateTime startDateTime = today.minusDays(30).atStartOfDay();

        try {
            // 直接从 MySQL 订单项表聚合数据 (包含所有来源订单，不仅仅是广告)
            List<Map<String, Object>> data = erpCrossOrderItemMapper.selectAggregatedPerformance(productIds, startDateTime);
            
            // 按 productId 分组处理数据
            Map<Long, List<Map<String, Object>>> productDataMap = data.stream()
                    .filter(d -> d.get("product_id") != null)
                    .collect(Collectors.groupingBy(d -> NumberUtil.parseLong(String.valueOf(d.get("product_id")))));

            for (Long productId : productIds) {
                List<Map<String, Object>> rows = productDataMap.getOrDefault(productId, Collections.emptyList());
                ListingPerformanceDTO performance = aggregatePerformanceFromOrders(rows, today);
                resultMap.put(productId, performance);
            }
        } catch (Exception e) {
            // 异常兜底
        }

        // 补全缺失的 ID
        for (Long id : productIds) {
            resultMap.putIfAbsent(id, new ListingPerformanceDTO(0, 0,0, 0L, 0.0, Collections.emptyList()));
        }

        return resultMap;
    }

    /**
     * 聚合单产品的订单数据
     */
    private ListingPerformanceDTO aggregatePerformanceFromOrders(List<Map<String, Object>> rows, LocalDate today) {
        int sales7d = 0;
        int sales30d = 0;
        int orderCount30d = 0;
        BigDecimal gmv30d = BigDecimal.ZERO;
        
        // 14天 Revenue Curve
        Map<LocalDate, BigDecimal> curveMap = new TreeMap<>();
        for (int i = 0; i < 14; i++) {
            curveMap.put(today.minusDays(i), BigDecimal.ZERO);
        }

        LocalDate d7Limit = today.minusDays(7);
        LocalDate d30Limit = today.minusDays(30);

        for (Map<String, Object> row : rows) {
            Object dateObj = row.get("date");
            if (dateObj == null) dateObj = row.get("DATE");
            LocalDate date = parseLocalDate(dateObj);
            if (date == null) continue;

            // 数据库存的是分，GMV 展示通常为元
            Object salesObj = row.get("sales");
            if (salesObj == null) salesObj = row.get("SALES");
            BigDecimal dailyRevenueCents = NumberUtil.toBigDecimal(String.valueOf(salesObj));
            BigDecimal dailyRevenue = dailyRevenueCents.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            Object unitsObj = row.get("unit_count");
            if (unitsObj == null) unitsObj = row.get("UNIT_COUNT");
            int dailyUnits = NumberUtil.parseInt(String.valueOf(unitsObj), 0);

            Object ordersObj = row.get("order_count");
            if (ordersObj == null) ordersObj = row.get("ORDER_COUNT");
            int dailyOrders = NumberUtil.parseInt(String.valueOf(ordersObj), 0);

            // 30天统计 (不含今天)
            if (date.isBefore(today) && !date.isBefore(d30Limit)) {
                sales30d += dailyUnits;
                orderCount30d += dailyOrders;
                gmv30d = gmv30d.add(dailyRevenue);
            }

            // 7天统计
            if (date.isBefore(today) && !date.isBefore(d7Limit)) {
                sales7d += dailyUnits;
            }

            // 曲线数据 (包含今天实时)
            if (curveMap.containsKey(date)) {
                curveMap.put(date, dailyRevenue);
            }
        }

        List<BigDecimal> revenueCurve = new ArrayList<>(curveMap.values());

        // 计算 7D 销量增长率 (当前 7 天 vs 上一个 7 天)
        int prevSales7d = 0;
        LocalDate prev7dLimit = today.minusDays(14);
        for (Map<String, Object> row : rows) {
            Object dateObj = row.get("date");
            if (dateObj == null) dateObj = row.get("DATE");
            LocalDate date = parseLocalDate(dateObj);
            if (date == null) continue;

            Object unitsObj = row.get("unit_count");
            if (unitsObj == null) unitsObj = row.get("UNIT_COUNT");
            int dailyUnits = NumberUtil.parseInt(String.valueOf(unitsObj), 0);
            if (date.isBefore(d7Limit) && !date.isBefore(prev7dLimit)) {
                prevSales7d += dailyUnits;
            }
        }

        Double growth = 0.0;
        if (prevSales7d > 0) {
            growth = (double) (sales7d - prevSales7d) / prevSales7d * 100;
        } else if (sales7d > 0) {
            growth = 100.0; // 如果上一期是 0，本期有销量，算 100% 增长
        }

        return new ListingPerformanceDTO(
                sales7d,
                sales30d,
                orderCount30d,
                gmv30d.setScale(0, RoundingMode.HALF_UP).longValue(),
                NumberUtil.round(growth, 1).doubleValue(),
                revenueCurve
        );
    }

    private LocalDate parseLocalDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDate) return (LocalDate) obj;
        if (obj instanceof java.sql.Date) return ((java.sql.Date) obj).toLocalDate();
        if (obj instanceof String) {
            try {
                return LocalDate.parse((String) obj);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
