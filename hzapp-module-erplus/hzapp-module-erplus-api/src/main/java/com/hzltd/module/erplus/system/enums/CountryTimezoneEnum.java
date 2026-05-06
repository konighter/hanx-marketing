package com.hzltd.module.erplus.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 国家编码与时区对应关系枚举
 *
 * @author antigravity
 */
@Getter
@AllArgsConstructor
public enum CountryTimezoneEnum {

    US("US", "America/Los_Angeles", "美国"),
    CA("CA", "America/Toronto", "加拿大"),
    MX("MX", "America/Mexico_City", "墨西哥"),
    BR("BR", "America/Sao_Paulo", "巴西"),
    UK("UK", "Europe/London", "英国"),
    GB("GB", "Europe/London", "英国"),
    DE("DE", "Europe/Berlin", "德国"),
    FR("FR", "Europe/Paris", "法国"),
    IT("IT", "Europe/Rome", "意大利"),
    ES("ES", "Europe/Madrid", "西班牙"),
    NL("NL", "Europe/Amsterdam", "荷兰"),
    SE("SE", "Europe/Stockholm", "瑞典"),
    PL("PL", "Europe/Warsaw", "波兰"),
    BE("BE", "Europe/Brussels", "比利时"),
    TR("TR", "Europe/Istanbul", "土耳其"),
    EG("EG", "Africa/Cairo", "埃及"),
    SA("SA", "Asia/Riyadh", "沙特阿拉伯"),
    AE("AE", "Asia/Dubai", "阿联酋"),
    IN("IN", "Asia/Kolkata", "印度"),
    JP("JP", "Asia/Tokyo", "日本"),
    AU("AU", "Australia/Sydney", "澳大利亚"),
    SG("SG", "Asia/Singapore", "新加坡");

    private final String countryCode;
    private final String timezone;
    private final String description;

    /**
     * 根据国家编码获取时区
     *
     * @param countryCode 国家编码
     * @return 时区ID，如果未找到则返回 null
     */
    public static String getTimezoneByCountryCode(String countryCode) {
        if (countryCode == null) {
            return null;
        }
        for (CountryTimezoneEnum value : values()) {
            if (value.countryCode.equalsIgnoreCase(countryCode)) {
                return value.timezone;
            }
        }
        return null;
    }

    /**
     * 根据国家编码查找枚举项
     *
     * @param countryCode 国家编码
     * @return 枚举项，如果未找到则返回 null
     */
    public static CountryTimezoneEnum findByCountryCode(String countryCode) {
        if (countryCode == null) {
            return null;
        }
        for (CountryTimezoneEnum value : values()) {
            if (value.countryCode.equalsIgnoreCase(countryCode)) {
                return value;
            }
        }
        return null;
    }
}
