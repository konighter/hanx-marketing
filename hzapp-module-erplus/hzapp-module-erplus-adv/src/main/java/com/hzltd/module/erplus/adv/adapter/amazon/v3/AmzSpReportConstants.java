package com.hzltd.module.erplus.adv.adapter.amazon.v3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Constants for Amazon Advertising Reporting API V3.0
 * Includes report type definitions based on the Amazon Advertising Reporting matrix.
 */
public class AmzSpReportConstants {

    // Ad Products
    public static final String AD_PRODUCT_SP = "SPONSORED_PRODUCTS";
    public static final String AD_PRODUCT_SB = "SPONSORED_BRANDS";
    public static final String AD_PRODUCT_SD = "SPONSORED_DISPLAY";
    public static final String AD_PRODUCT_STV = "SPONSORED_TV";
    public static final String AD_PRODUCT_DSP = "DSP";

    // --- Sponsored Products (SP) ---
    public static final String SP_CAMPAIGNS = "spCampaigns";
    public static final String SP_AD_GROUPS = "spAdGroups";
    public static final String SP_ADS = "spAds";
    public static final String SP_TARGETING = "spTargeting";
    public static final String SP_SEARCH_TERM = "spSearchTerm";
    public static final String SP_PURCHASED_PRODUCT = "spPurchasedProduct";
    public static final String SP_GROSS_AND_INVALIDS = "spGrossAndInvalids";
    public static final String SP_PROMPT_AD_EXTENSION = "spPromptAdExtension";

    // --- Sponsored Brands (SB) ---
    public static final String SB_CAMPAIGNS = "sbCampaigns";
    public static final String SB_AD_GROUPS = "sbAdGroup";
    public static final String SB_ADS = "sbAds";
    public static final String SB_TARGETING = "sbTargeting";
    public static final String SB_SEARCH_TERM = "sbSearchTerm";
    public static final String SB_PURCHASED_PRODUCT = "sbPurchasedProduct";
    public static final String SB_GROSS_AND_INVALIDS = "sbGrossAndInvalids";
    public static final String SB_PLACEMENT = "sbPlacement";

    // --- Sponsored Display (SD) ---
    public static final String SD_CAMPAIGNS = "sdCampaigns";
    public static final String SD_AD_GROUPS = "sdAdGroup";
    public static final String SD_ADS = "sdAdvertisedProduct";
    public static final String SD_TARGETING = "sdTargeting";
    public static final String SD_PURCHASED_PRODUCT = "sdPurchasedProduct";
    public static final String SD_GROSS_AND_INVALIDS = "sdGrossAndInvalids";

    // --- Sponsored TV (STV) ---
    public static final String STV_CAMPAIGNS = "stCampaigns";
    public static final String STV_TARGETING = "stTargeting";

    // --- Amazon DSP ---
    public static final String DSP_CAMPAIGN = "dspCampaign";
    public static final String DSP_AUDIENCE = "dspAudience";
    public static final String DSP_AUDIO_VIDEO = "dspAudioVideo";
    public static final String DSP_BID_ADJUSTMENTS = "dspBidAdjustments";
    public static final String DSP_BRAND_SUITABILITY = "dspBrandSuitability";
    public static final String DSP_GEO = "dspGeo";
    public static final String DSP_INVENTORY = "dspInventory";
    public static final String DSP_PRODUCT = "dspProduct";
    public static final String DSP_REACH_FREQUENCY = "dspReachFrequency";
    public static final String DSP_TECH = "dspTech";

    // Aliases for backwards compatibility or simplified access
    public static final String REPORT_TYPE_CAMPAIGNS = SP_CAMPAIGNS;
    public static final String REPORT_TYPE_AD_GROUPS = SP_AD_GROUPS;
    public static final String REPORT_TYPE_ADS = SP_ADS;
    public static final String REPORT_TYPE_KEYWORDS = SP_TARGETING;

    // Common Performance Metrics (Columns)
    private static final List<String> BASE_PERFORMANCE_METRICS = Arrays.asList(
            "date",
//            "startDate",
//            "endDate",
            "impressions",
            "clicks",
            "cost",
            "purchases1d",
            "purchases7d",
            "purchases14d",
            "purchases30d",
            "sales1d",
            "sales7d",
            "sales14d",
            "sales30d"
    );


    private static Map<String, List<String>> reportTypeColumns = new java.util.HashMap<>();
    static {
        // campaign
        reportTypeColumns.put(SP_CAMPAIGNS, Arrays.asList("impressions", "addToList", "qualifiedBorrows", "royaltyQualifiedBorrows", "clicks", "cost", "purchases1d", "purchases7d", "purchases14d", "purchases30d", "purchasesSameSku1d", "purchasesSameSku7d", "purchasesSameSku14d", "purchasesSameSku30d", "unitsSoldClicks1d", "unitsSoldClicks7d", "unitsSoldClicks14d", "unitsSoldClicks30d", "sales1d", "sales7d", "sales14d", "sales30d", "attributedSalesSameSku1d", "attributedSalesSameSku7d", "attributedSalesSameSku14d", "attributedSalesSameSku30d", "unitsSoldSameSku1d", "unitsSoldSameSku7d", "unitsSoldSameSku14d", "unitsSoldSameSku30d", "kindleEditionNormalizedPagesRead14d", "kindleEditionNormalizedPagesRoyalties14d", "date", "campaignBiddingStrategy", "costPerClick", "clickThroughRate", "spend"));

        //impressions, addToList, qualifiedBorrows, royaltyQualifiedBorrows, clicks, costPerClick, clickThroughRate, cost, purchases1d, purchases7d, purchases14d, purchases30d, purchasesSameSku1d, purchasesSameSku7d, purchasesSameSku14d, purchasesSameSku30d, unitsSoldClicks1d, unitsSoldClicks7d, unitsSoldClicks14d, unitsSoldClicks30d, sales1d, sales7d, sales14d, sales30d, attributedSalesSameSku1d, attributedSalesSameSku7d, attributedSalesSameSku14d, attributedSalesSameSku30d, unitsSoldSameSku1d, unitsSoldSameSku7d, unitsSoldSameSku14d, unitsSoldSameSku30d, kindleEditionNormalizedPagesRead14d, kindleEditionNormalizedPagesRoyalties14d, salesOtherSku7d, unitsSoldOtherSku7d, acosClicks7d, acosClicks14d, roasClicks7d, roasClicks14d, keywordId, keyword, campaignBudgetCurrencyCode, date, startDate, endDate, portfolioId, campaignName, campaignId, campaignBudgetType, campaignBudgetAmount, campaignStatus, keywordBid, adGroupName, adGroupId, keywordType, matchType, targeting, topOfSearchImpressionShare
        reportTypeColumns.put(SP_TARGETING, Arrays.asList("impressions", "addToList", "qualifiedBorrows", "royaltyQualifiedBorrows", "clicks", "costPerClick", "clickThroughRate", "cost", "purchases1d", "purchases7d", "purchases14d", "purchases30d", "purchasesSameSku1d", "purchasesSameSku7d", "purchasesSameSku14d", "purchasesSameSku30d", "unitsSoldClicks1d", "unitsSoldClicks7d", "unitsSoldClicks14d", "unitsSoldClicks30d", "sales1d", "sales7d", "sales14d", "sales30d", "attributedSalesSameSku1d", "attributedSalesSameSku7d", "attributedSalesSameSku14d", "attributedSalesSameSku30d", "unitsSoldSameSku1d", "unitsSoldSameSku7d", "unitsSoldSameSku14d", "unitsSoldSameSku30d", "kindleEditionNormalizedPagesRead14d", "kindleEditionNormalizedPagesRoyalties14d", "salesOtherSku7d", "unitsSoldOtherSku7d", "acosClicks7d", "acosClicks14d", "roasClicks7d", "roasClicks14d", "keywordId", "keyword", "campaignBudgetCurrencyCode", "date", "portfolioId", "campaignName", "campaignId", "campaignBudgetType", "campaignBudgetAmount", "campaignStatus", "keywordBid", "adGroupName", "adGroupId", "keywordType", "matchType", "targeting", "topOfSearchImpressionShare"));

        // impressions, addToList, qualifiedBorrows, royaltyQualifiedBorrows, clicks, costPerClick, clickThroughRate, cost, purchases1d, purchases7d, purchases14d, purchases30d, purchasesSameSku1d, purchasesSameSku7d, purchasesSameSku14d, purchasesSameSku30d, unitsSoldClicks1d, unitsSoldClicks7d, unitsSoldClicks14d, unitsSoldClicks30d, sales1d, sales7d, sales14d, sales30d, attributedSalesSameSku1d, attributedSalesSameSku7d, attributedSalesSameSku14d, attributedSalesSameSku30d, unitsSoldSameSku1d, unitsSoldSameSku7d, unitsSoldSameSku14d, unitsSoldSameSku30d, kindleEditionNormalizedPagesRead14d, kindleEditionNormalizedPagesRoyalties14d, salesOtherSku7d, unitsSoldOtherSku7d, acosClicks7d, acosClicks14d, roasClicks7d, roasClicks14d, keywordId, keyword, campaignBudgetCurrencyCode, date, startDate, endDate, portfolioId, searchTerm, campaignName, campaignId, campaignBudgetType, campaignBudgetAmount, campaignStatus, keywordBid, adGroupName, adGroupId, keywordType, matchType, targeting, adKeywordStatus
        reportTypeColumns.put(SP_SEARCH_TERM, Arrays.asList("impressions", "addToList", "qualifiedBorrows", "royaltyQualifiedBorrows", "clicks", "costPerClick", "clickThroughRate", "cost", "purchases1d", "purchases7d", "purchases14d", "purchases30d", "purchasesSameSku1d", "purchasesSameSku7d", "purchasesSameSku14d", "purchasesSameSku30d", "unitsSoldClicks1d", "unitsSoldClicks7d", "unitsSoldClicks14d", "unitsSoldClicks30d", "sales1d", "sales7d", "sales14d", "sales30d", "attributedSalesSameSku1d", "attributedSalesSameSku7d", "attributedSalesSameSku14d", "attributedSalesSameSku30d", "unitsSoldSameSku1d", "unitsSoldSameSku7d", "unitsSoldSameSku14d", "unitsSoldSameSku30d", "kindleEditionNormalizedPagesRead14d", "kindleEditionNormalizedPagesRoyalties14d", "salesOtherSku7d", "unitsSoldOtherSku7d", "acosClicks7d", "acosClicks14d", "roasClicks7d", "roasClicks14d", "keywordId", "keyword", "campaignBudgetCurrencyCode", "date", "portfolioId", "searchTerm", "campaignName", "campaignId", "campaignBudgetType", "campaignBudgetAmount", "campaignStatus", "keywordBid", "adGroupName", "adGroupId", "keywordType", "matchType", "targeting", "adKeywordStatus"));

    }

    /**
     * Get columns for a specific report type and groupBy dimension
     */
    public static List<String> getColumnsForType(String reportType, String groupBy) {
        List<String> columns = new java.util.ArrayList<>();
        if (reportTypeColumns.containsKey(reportType)) {
            columns.addAll(reportTypeColumns.get(reportType));
        } else {
            columns.addAll(BASE_PERFORMANCE_METRICS);
        }

        // Add dimension specific columns if needed (for those not already in the common lists)
        if (groupBy != null) {
            String[] dims = groupBy.split(",");
            for (String dim : dims) {
                dim = dim.trim();
                switch (dim) {
                    case "campaignPlacement":
                        addIfNotExists(columns, Arrays.asList("placementClassification", "campaignName", "campaignId", "campaignStatus", "campaignBudgetAmount", "campaignBudgetType", "campaignRuleBasedBudgetAmount", "campaignApplicableBudgetRuleId", "campaignApplicableBudgetRuleName", "campaignBudgetCurrencyCode", "topOfSearchImpressionShare"));
                        break;
                    case "adGroup":
                        addIfNotExists(columns, Arrays.asList("adGroupName", "adGroupId", "adStatus"));
                        break;
                    case "campaign":
                        addIfNotExists(columns, Arrays.asList("campaignName", "campaignId", "campaignStatus", "campaignBudgetAmount", "campaignBudgetType", "campaignBudgetCurrencyCode"));
                        break;
                    case "targeting":
                        addIfNotExists(columns, Arrays.asList("keywordId", "keyword", "keywordBid", "keywordType", "matchType", "targeting"));
                        break;
                    case "searchTerm":
                        addIfNotExists(columns, Arrays.asList("searchTerm", "keywordId", "keyword"));
                        break;
                }
            }
            if ("campaign,campaignPlacement".equals(groupBy)) {
                columns.remove("topOfSearchImpressionShare");
            }
        }
        return columns;
    }

    private static void addIfNotExists(List<String> columns, List<String> fields) {
        for (String field : fields) {
            if (!columns.contains(field)) {
                columns.add(field);
            }
        }
    }

    /**
     * Get groupBy dimension for a specific report type
     * Ref: Amazon Advertising API v3.0 Configurations
     */
    public static List<String> getGroupByForType(String reportType) {
        switch (reportType) {
            case SP_CAMPAIGNS:
                return Arrays.asList("campaign", "campaignPlacement");
            case SB_CAMPAIGNS:
            case SD_CAMPAIGNS:
            case STV_CAMPAIGNS:
            case DSP_CAMPAIGN:
                return Arrays.asList("campaign");

            case SP_AD_GROUPS:
            case SB_AD_GROUPS:
            case SD_AD_GROUPS:
                return Arrays.asList("adGroup");

            case SP_ADS:
            case SB_ADS:
            case SD_ADS:
                return Arrays.asList("ad");

            case SP_TARGETING:
            case SB_TARGETING:
            case SD_TARGETING:
            case STV_TARGETING:
                return Arrays.asList("targeting");

            case SP_SEARCH_TERM:
            case SB_SEARCH_TERM:
                return Arrays.asList("searchTerm");

            default:
                return Arrays.asList("campaign");
        }
    }
    
    // Statuses
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_PENDING = "PENDING";
}
