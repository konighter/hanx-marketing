package com.hzltd.module.erplus.controller.admin.shop.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthRespVO;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 店铺信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ShopRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3188")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台")
    private Integer platform;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台")
    private String platformName;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域")
    private String region;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域")
    private String regionName;

    @ExcelProperty("国家代码")
    private String countryCode;

    @Schema(description = "状态", example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "卖家ID")
    @ExcelProperty("卖家ID")
    private String sellerId;

    @Schema(description = "授权信息")
    private List<PlatformAuthRespVO> auths;

    @Schema(description = "授权信息")
    private AuthorizationModelV0 authInfo;

    @Schema(description = "时区", example = "Asia/Shanghai")
    @ExcelProperty("时区")
    private String timezone;

}