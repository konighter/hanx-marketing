package com.hzltd.module.erplus.service.spu.utils;

import cn.hutool.core.util.ObjectUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.system.enums.ProductAttrKeyEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品属性工具类
 * 处理 Map 与 DTO 之间的转换，结合 ProductAttrKeyEnum 进行类型校验和反序列化
 *
 * @author 翰展科技
 */
@Slf4j
public class ProductAttrUtils {

    /**
     * 解析属性 Map
     * 将前端传来的 Map<String, Object> (可能是 JsonObject) 按照 Registry 转换为正确的 DTO 类型
     */
    public static Map<String, Object> parseAttributes(Map<String, Object> originMap) {
        if (originMap == null || originMap.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        originMap.forEach((key, value) -> {
            ProductAttrKeyEnum keyEnum = ProductAttrKeyEnum.findByKey(key);
            if (keyEnum != null && value != null) {
                try {
                    // 如果已经是正确的类型，直接放入
                    if (keyEnum.getClazz().isInstance(value)) {
                        result.put(key, value);
                    } else {
                        // 否则尝试从 JSON/Map 转换
                        String json = JsonUtils.toJsonString(value);
                        Object dto = JsonUtils.parseObject(json, keyEnum.getClazz());
                        result.put(key, dto);
                    }
                } catch (Exception e) {
                    log.warn("[parseAttributes][Key({}) 转换 DTO({}) 失败, value: {}]", key, keyEnum.getClazz().getSimpleName(), value);
                }
            } else {
                // 未注册的属性原样保留，或者根据业务决定是否丢弃
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * 比较两个属性值是否相等（解析为 JSON 后对比，规避 Map/DTO 差异）
     */
    public static boolean isAttrEqual(Object oldVal, Object newVal) {
        if (oldVal == null && newVal == null) return true;
        if (oldVal == null || newVal == null) return false;
        String oldJson = oldVal instanceof String ? (String) oldVal : JsonUtils.toJsonString(oldVal);
        String newJson = newVal instanceof String ? (String) newVal : JsonUtils.toJsonString(newVal);
        return ObjectUtil.equal(oldJson, newJson);
    }
}
