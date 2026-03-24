package com.hzltd.module.amz.api.adv.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Amazon SP API 通用查询请求基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpCommonQueryRequest {

    /** 下一页令牌 */
    private String nextToken;

    /** 最大结果数 */
    private Integer maxResults;

    /** 是否包含扩展数据字段 */
    private Boolean includeExtendedDataFields;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StringFilter {
        /** 包含的标识符列表 */
        private List<String> include;

        public static StringFilter from(List<String> include) {
            if (include == null || include.isEmpty()) {
                return null;
            }
            return StringFilter.builder().include(include).build();
        }
    }
}
