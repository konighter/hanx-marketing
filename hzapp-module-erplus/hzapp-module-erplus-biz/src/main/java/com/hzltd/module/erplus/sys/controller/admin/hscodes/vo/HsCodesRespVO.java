package com.hzltd.module.erplus.sys.controller.admin.hscodes.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - [Erplus] 海关编码(HS Code) Response VO")
//@Data
@ExcelIgnoreUnannotated
public class HsCodesRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21432")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "HS Code编码 (可能包含点)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("商品编码(10)")
    private String code;

    @Schema(description = "编码描述", example = "你猜")
    @ExcelProperty("备注")
    private String description;

    @Schema(description = "关联的内部产品分类ID (方便查找)", example = "23692")
    @ExcelProperty("关联的内部产品分类ID (方便查找)")
    private Long categoryId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "附加商品编码")
    @ExcelProperty("附加商品编码")
    private String extCode;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("商品名称")
    private String name;

    @Schema(description = "低税率(优惠税率,最惠国)")
    @ExcelProperty("低税率")
    private Double lowTax;

    @Schema(description = "高税率(普通税率)")
    @ExcelProperty("高税率")
    private Double highTax;

    @Schema(description = "进口暂定税率")
    @ExcelProperty("进口暂定税率")
    private Double importTax;

    @Schema(description = "进口消费税率")
    @ExcelProperty("进口消费税率")
    private Double importVatTax;

    @Schema(description = "增值税率")
    @ExcelProperty("增值税率")
    private Double vatTax;

    @Schema(description = "出口税率")
    @ExcelProperty("出口税率")
    private Double exportTax;

    @Schema(description = "出口暂定税率")
    @ExcelProperty("出口暂定税率")
    private Double exportTaxProvision;

    @Schema(description = "法定第一计量单位")
    @ExcelProperty("法定第一计量单位")
    private String measureUnit1;

    @Schema(description = "法定第二计量单位")
    @ExcelProperty("法定第二计量单位")
    private String measureUnit2;

    @Schema(description = "监管代码")
    @ExcelProperty("监管代码")
    private String regulatoryCode;

    @Schema(description = "检验检疫代码")
    @ExcelProperty("检验检疫代码")
    private String quarantineCode;

    @Schema(description = "申报要素")
    @ExcelProperty("申报要素")
    private String declarationElements;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLowTax() {
        return lowTax;
    }

    public void setLowTax(Double lowTax) {
        this.lowTax = lowTax;
    }

    public Double getHighTax() {
        return highTax;
    }

    public void setHighTax(Double highTax) {
        this.highTax = highTax;
    }

    public Double getImportTax() {
        return importTax;
    }

    public void setImportTax(Double importTax) {
        this.importTax = importTax;
    }

    public Double getImportVatTax() {
        return importVatTax;
    }

    public void setImportVatTax(Double importVatTax) {
        this.importVatTax = importVatTax;
    }

    public Double getVatTax() {
        return vatTax;
    }

    public void setVatTax(Double vatTax) {
        this.vatTax = vatTax;
    }

    public Double getExportTax() {
        return exportTax;
    }

    public void setExportTax(Double exportTax) {
        this.exportTax = exportTax;
    }

    public Double getExportTaxProvision() {
        return exportTaxProvision;
    }

    public void setExportTaxProvision(Double exportTaxProvision) {
        this.exportTaxProvision = exportTaxProvision;
    }

    public String getMeasureUnit1() {
        return measureUnit1;
    }

    public void setMeasureUnit1(String measureUnit1) {
        this.measureUnit1 = measureUnit1;
    }

    public String getMeasureUnit2() {
        return measureUnit2;
    }

    public void setMeasureUnit2(String measureUnit2) {
        this.measureUnit2 = measureUnit2;
    }

    public String getRegulatoryCode() {
        return regulatoryCode;
    }

    public void setRegulatoryCode(String regulatoryCode) {
        this.regulatoryCode = regulatoryCode;
    }

    public String getQuarantineCode() {
        return quarantineCode;
    }

    public void setQuarantineCode(String quarantineCode) {
        this.quarantineCode = quarantineCode;
    }

    public String getDeclarationElements() {
        return declarationElements;
    }

    public void setDeclarationElements(String declarationElements) {
        this.declarationElements = declarationElements;
    }
}