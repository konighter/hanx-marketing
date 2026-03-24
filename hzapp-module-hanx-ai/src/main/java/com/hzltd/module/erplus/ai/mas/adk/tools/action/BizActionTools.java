package com.hzltd.module.erplus.ai.mas.adk.tools.action;

import com.hzltd.module.erplus.ai.mas.agent.MasTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 业务操作类 Tools (Mock 实现)
 * 提供商品价格调整、Listing 修改等操作能力
 */
@Slf4j
@Component("bizActionTools")
public class BizActionTools {

    @MasTool("调整指定 ASIN 的商品售价（单位: 美元）")
    public Map<String, Object> adjustPrice(String asin, double newPrice) {
        log.info("[Mock] adjustPrice: asin={}, newPrice={}", asin, newPrice);
        return Map.of(
                "success", true,
                "asin", asin,
                "previousPrice", 34.99,
                "newPrice", newPrice,
                "message", "商品价格已从 $34.99 调整为 $" + String.format("%.2f", newPrice)
        );
    }

    @MasTool("修改指定 ASIN 的 Listing 内容（field: title / bulletPoints / description / searchTerms, value: 新值）")
    public Map<String, Object> updateListing(String asin, String field, String value) {
        log.info("[Mock] updateListing: asin={}, field={}, value={}", asin, field, value);
        return Map.of(
                "success", true,
                "asin", asin,
                "field", field,
                "newValue", value,
                "message", "Listing 字段 '" + field + "' 已更新"
        );
    }
}
