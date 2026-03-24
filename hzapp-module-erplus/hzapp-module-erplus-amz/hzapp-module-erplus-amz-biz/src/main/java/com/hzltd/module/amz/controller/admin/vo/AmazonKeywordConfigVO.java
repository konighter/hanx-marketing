package com.hzltd.module.amz.controller.admin.vo;

import com.hzltd.module.amz.api.adv.model.sp.AdsSpKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 亚马逊广告关键词配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonKeywordConfigVO {

    @Schema(description = "站点的 Profile ID")
    private String profileId;

    @Schema(description = "扩展数据")
    private Object extendedData;

    @Schema(description = "全球关键词 ID")
    private String globalKeywordId;

    @Schema(description = "本地语言关键词")
    private String nativeLanguageKeyword;

    @Schema(description = "本地语言代码")
    private String nativeLanguageLocale;

    public static AmazonKeywordConfigVO fromSpKeyword(AdsSpKeyword spKeyword) {
        if (spKeyword == null) {
            return null;
        }
        return AmazonKeywordConfigVO.builder()
                .globalKeywordId(spKeyword.getGlobalKeywordId())
                .nativeLanguageKeyword(spKeyword.getNativeLanguageKeyword())
                .nativeLanguageLocale(spKeyword.getNativeLanguageLocale())
                .extendedData(spKeyword.getExtendedData())
                .build();
    }
}
