package com.hzltd.module.erplus.enums.amz;

import lombok.Getter;

@Getter
public enum AmazonMarketplaceEnum {

    // North America
    US("ATVPDKIKX0DER", "NA", "US", "America/New_York"),
    CA("A2EUQ1WTGCTBG2", "NA", "CA", "America/Toronto"),
    MX("A1AM78C64UM0Y8", "NA", "MX", "America/Mexico_City"),
    BR("A2Q3Y263D00KWC", "NA", "BR", "America/Sao_Paulo"),

    // Europe
    UK("A1F83G8C2ARO7P", "EU", "UK", "Europe/London"),
    DE("A1PA6795UKMFR9", "EU", "DE", "Europe/Berlin"),
    FR("A13V1IB3VIYZZH", "EU", "FR", "Europe/Paris"),
    IT("APJ6JRA9NG5V4", "EU", "IT", "Europe/Rome"),
    ES("A1RKKUPIHCS9HS", "EU", "ES", "Europe/Madrid"),
    NL("A1805IZSGTT6HS", "EU", "NL", "Europe/Amsterdam"),
    SE("A2NODRK01O0KOX", "EU", "SE", "Europe/Stockholm"),
    PL("A1C3SOST3A6W38", "EU", "PL", "Europe/Warsaw"),
    BE("AMEN7PMS3EDWL", "EU", "BE", "Europe/Brussels"),
    TR("A33AVAJ2PDY3EV", "EU", "TR", "Europe/Istanbul"),
    EG("ARBP9RLWVYKHZ", "EU", "EG", "Africa/Cairo"),
    SA("A17E79C6D8DWNP", "EU", "SA", "Asia/Riyadh"),
    AE("A2VIG99S91N5XS", "EU", "AE", "Asia/Dubai"),
    IN("A21TJRUUN4KGVY", "EU", "IN", "Asia/Kolkata"),

    // Far East
    JP("A1VC38T7YXB528", "FE", "JP", "Asia/Tokyo"),
    AU("A39IBJ37TRP1C6", "FE", "AU", "Australia/Sydney"),
    SG("A19VAU5U5O7RUS", "FE", "SG", "Asia/Singapore");

    private final String marketplaceId;
    private final String regionCode;
    private final String countryCode;
    private final String timezone;

    AmazonMarketplaceEnum(String marketplaceId, String regionCode, String countryCode, String timezone) {
        this.marketplaceId = marketplaceId;
        this.regionCode = regionCode;
        this.countryCode = countryCode;
        this.timezone = timezone;
    }

    public static AmazonMarketplaceEnum findByMarketplaceId(String marketplaceId) {
        for (AmazonMarketplaceEnum value : values()) {
            if (value.marketplaceId.equals(marketplaceId)) {
                return value;
            }
        }
        return null;
    }

    public static AmazonMarketplaceEnum findByCountryCode(String countryCode) {
        for (AmazonMarketplaceEnum value : values()) {
            if (value.countryCode.equalsIgnoreCase(countryCode)) {
                return value;
            }
        }
        return null;
    }
}
