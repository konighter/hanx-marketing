package com.hzltd.module.erplus.controller.admin.listing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 亚马逊刊登表单字段 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzListingFormFieldVO {

    @Schema(description = "字段 ID (打平后的路径)", example = "item_name")
    private String id;

    @Schema(description = "字段标题", example = "商品名称")
    private String title;

    @Schema(description = "动态组件标识")
    private String uiWidget;

    @Schema(description = "字段类型", example = "string")
    private String type; // string, number, array, object, enum

    @Schema(description = "字段描述")
    private String description;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "是否可编辑")
    private Boolean editable;

    @Schema(description = "是否隐藏")
    private Boolean hidden;

    @Schema(description = "是否为非必填/不重要的可选项（隐藏于主表单区域外）")
    private Boolean optional;

    @Schema(description = "默认值")
    private Object defaultValue;

    @Schema(description = "可选项 (枚举类型使用)")
    private List<Option> options;

    @Schema(description = "联动规则")
    private List<AmzListingFieldRuleVO> linkageRules;

    @Schema(description = "扩展属性 (如 minLength, maxLength, pattern 等)")
    private Map<String, Object> extra;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        @Schema(description = "显示文本")
        private String label;
        @Schema(description = "实际值")
        private String value;
    }
}
