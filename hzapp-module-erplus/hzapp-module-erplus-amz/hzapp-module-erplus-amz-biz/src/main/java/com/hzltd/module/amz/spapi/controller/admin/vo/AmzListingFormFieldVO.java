package com.hzltd.module.amz.spapi.controller.admin.vo;

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

    @Schema(description = "业务属性路径 (用于联动规则)", example = "purchasable_offer.0.our_price.0.amount")
    private String bizField;

    @Schema(description = "表单渲染单位 ID", example = "item_name")
    private String formField;

    @Schema(description = "字段名称 (本地 key)", example = "value")
    private String name;

    @Schema(description = "子字段 (用于嵌套结构)")
    private List<AmzListingFormFieldVO> children;

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

    @Schema(description = "展示顺序")
    private Integer order;

    @Schema(description = "是否允许自定义输入 (与枚举配合使用)", example = "true")
    private Boolean allowCustomEnum;

    @Schema(description = "所属分组名称", example = "基本信息")
    private String groupName;

    @Schema(description = "是否为聚合/嵌套字段")
    private Boolean isComposite;

    @Schema(description = "联动规则 (结构化)")
    private List<AmzListingFieldRuleVO> linkages;

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
