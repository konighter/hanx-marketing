package com.hzltd.module.erplus.system.model;

import com.hzltd.module.erplus.spapi.enums.LanguageEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseCrossRequest {
    /**
     * 语言
     */
    private LanguageEnum language = LanguageEnum.ZH_CN;

    /**
     * 平台枚举
     */
    @NotNull(message = "目标平台不能为空")
    private Integer platformId;

    /**
     * 店铺
     */
    private Integer shopId; // TT 必填
}
