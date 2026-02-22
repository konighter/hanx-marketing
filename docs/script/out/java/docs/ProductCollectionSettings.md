

# ProductCollectionSettings

An ad creative that contains multiple products and a custom image.

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**brand** | **String** | The name of the brand being advertised. |  |
|**brandLogos** | [**List&lt;Image&gt;**](Image.md) | The brand logo image assets to be used in the ad. |  |
|**creativePropertiesToOptimize** | **List&lt;ProductCollectionCreativePropertiesToOptimize&gt;** | The CreativeProperty Amazon will enhance or generate based on various factors like audience, placement etc. |  [optional] |
|**customImages** | [**List&lt;Image&gt;**](Image.md) | The set of custom images featured in the ad. |  |
|**enableCreativeAutoTranslation** | **Boolean** | If set to true and the headline and/or video are not in the marketplace&#39;s default language, Amazon will attempt to translate them to the marketplace&#39;s default language. If Amazon is unable to translate them, the ad will be rejected by moderation. |  [optional] |
|**headlines** | **List&lt;String&gt;** | The headline submitted as part of the ad creative. During your campaign, Amazon will optimize amongst the headlines you provide to match customer intent. |  |
|**landingPage** | [**ProductCollectionLandingPage**](ProductCollectionLandingPage.md) |  |  |
|**moderationStatus** | [**CreativeStatus**](CreativeStatus.md) |  |  [optional] |
|**products** | [**List&lt;AdvertisedProducts&gt;**](AdvertisedProducts.md) | The products featured in the ad. |  [optional] |
|**untranslatedHeadlines** | **List&lt;String&gt;** | The headlines entered by the advertiser. |  [optional] |



