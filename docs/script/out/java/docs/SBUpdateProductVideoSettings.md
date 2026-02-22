

# SBUpdateProductVideoSettings

An ad with a creative that includes a video.

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**brand** | **String** | The name of the brand being advertised. |  [optional] |
|**brandLogos** | [**List&lt;SBCreateImage&gt;**](SBCreateImage.md) | The brand logo image assets to be used in the ad. |  [optional] |
|**enableCreativeAutoTranslation** | **Boolean** | If set to true and the headline and/or video are not in the marketplace&#39;s default language, Amazon will attempt to translate them to the marketplace&#39;s default language. If Amazon is unable to translate them, the ad will be rejected by moderation. |  [optional] |
|**headlines** | **List&lt;String&gt;** | The headline submitted as part of the ad creative. During your campaign, Amazon will optimize amongst the headlines you provide to match customer intent. |  [optional] |
|**landingPage** | [**SBUpdateVideoLandingPage**](SBUpdateVideoLandingPage.md) |  |  [optional] |
|**products** | [**List&lt;SBCreateAdvertisedProducts&gt;**](SBCreateAdvertisedProducts.md) | The products featured in the video ad. |  [optional] |
|**videos** | [**List&lt;SBCreateVideo&gt;**](SBCreateVideo.md) | The video assets used in the ad. |  [optional] |



