package com.hzltd.module.erplus.controller.admin.common;

import com.hzltd.module.erplus.constant.LanguageEnum;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BaseCrossRequest {
    /**
     * 语言
     */
    private LanguageEnum language = LanguageEnum.ZH_CN;;

    /**
     * 平台枚举
     */
    @NotNull(message = "目标平台不能为空")
    private CrossPlatformEnum crossPlatform;

    /**
     * 店铺
     */
    private Long shopId; // TT 必填
}
