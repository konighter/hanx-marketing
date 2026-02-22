

# StoreSpotlightSettings

An ad creative that contains ASINs within a brand Store.

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**brand** | **String** | The name of the brand being advertised. |  |
|**brandLogos** | [**List&lt;Image&gt;**](Image.md) | The brand logo image assets to be used in the ad. |  |
|**cards** | [**List&lt;CardCreativeElement&gt;**](CardCreativeElement.md) | The sub-elements of the creative. Each card highlights a different ASIN associated to a brand Store. |  |
|**creativePropertiesToOptimize** | **List&lt;StoreSpotlightCreativePropertiesToOptimize&gt;** | The CreativeProperty Amazon will enhance or generate based on various factors like audience, placement etc. |  [optional] |
|**enableCreativeAutoTranslation** | **Boolean** | If set to true and the headline and/or video are not in the marketplace&#39;s default language, Amazon will attempt to translate them to the marketplace&#39;s default language. If Amazon is unable to translate them, the ad will be rejected by moderation. |  [optional] |
|**headlines** | **List&lt;String&gt;** | The headline submitted as part of the ad creative. During your campaign, Amazon will optimize amongst the headlines you provide to match customer intent. |  |
|**landingPage** | [**StoreSpotlightLandingPage**](StoreSpotlightLandingPage.md) |  |  |
|**moderationStatus** | [**CreativeStatus**](CreativeStatus.md) |  |  [optional] |
|**untranslatedHeadlines** | **List&lt;String&gt;** | The headline entered by the advertiser. |  [optional] |



