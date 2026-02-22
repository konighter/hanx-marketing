

# SBUpdateProductCollectionSettings

An ad creative that contains multiple products and a custom image.

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**brand** | **String** | The name of the brand being advertised. |  [optional] |
|**brandLogos** | [**List&lt;SBCreateImage&gt;**](SBCreateImage.md) | The brand logo image assets to be used in the ad. |  [optional] |
|**creativePropertiesToOptimize** | **List&lt;SBProductCollectionCreativePropertiesToOptimize&gt;** | The CreativeProperty Amazon will enhance or generate based on various factors like audience, placement etc. |  [optional] |
|**customImages** | [**List&lt;SBCreateImage&gt;**](SBCreateImage.md) | The set of custom images featured in the ad. |  [optional] |
|**enableCreativeAutoTranslation** | **Boolean** | If set to true and the headline and/or video are not in the marketplace&#39;s default language, Amazon will attempt to translate them to the marketplace&#39;s default language. If Amazon is unable to translate them, the ad will be rejected by moderation. |  [optional] |
|**headlines** | **List&lt;String&gt;** | The headline submitted as part of the ad creative. During your campaign, Amazon will optimize amongst the headlines you provide to match customer intent. |  [optional] |
|**landingPage** | [**SBUpdateProductCollectionLandingPage**](SBUpdateProductCollectionLandingPage.md) |  |  [optional] |
|**products** | [**List&lt;SBCreateAdvertisedProducts&gt;**](SBCreateAdvertisedProducts.md) | The products featured in the ad. |  [optional] |



