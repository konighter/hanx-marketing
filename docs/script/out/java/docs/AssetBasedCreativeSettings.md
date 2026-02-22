

# AssetBasedCreativeSettings


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**additionalHtml** | **String** | Additional HTML to include with the render response for display inventory targets. |  [optional] |
|**backgrounds** | [**List&lt;Background&gt;**](Background.md) | The background which is displayed on the ad. |  [optional] |
|**bodyText** | **List&lt;String&gt;** | The body text to use for the Asset Based Creative experience. |  [optional] |
|**brand** | **String** | The brand of the product(s) being advertised. |  [optional] |
|**callToActions** | [**AssetBasedCreativeCallToAction**](AssetBasedCreativeCallToAction.md) |  |  [optional] |
|**clickTrackingUrls** | [**List&lt;CreativeTrackingUrl&gt;**](CreativeTrackingUrl.md) | The third party urls to trigger when an click is recorded. |  [optional] |
|**creativeSizes** | [**List&lt;Size&gt;**](Size.md) | The placement sizes this creative should serve on. |  [optional] |
|**customVideos** | [**Video**](Video.md) |  |  [optional] |
|**disclaimers** | **String** | The disclaimers to use for the Asset Based Creative experience. |  [optional] |
|**enableCreativeAutoTranslation** | **Boolean** | If set to true and the headline and/or video are not in the marketplace&#39;s default language, Amazon will attempt to translate them to the marketplace&#39;s default language. If Amazon is unable to translate them, the ad will be rejected by moderation. |  [optional] |
|**hasTermsAndConditions** | **Boolean** | Indicates that the ad promotes a free product or service and has qualifying terms and conditions applicable to the customer. LandingPageURL must link out to a page detailing terms and conditions or contain a link to those. |  [optional] |
|**headlines** | **List&lt;String&gt;** | The headline(s) to use for the Asset Based Creative experience. |  |
|**images** | [**List&lt;Image&gt;**](Image.md) | The image(s) to use. |  [optional] |
|**impressionTrackingUrls** | [**List&lt;CreativeTrackingUrl&gt;**](CreativeTrackingUrl.md) | The third party urls to trigger when an impression is recorded. |  [optional] |
|**inventoryTypes** | **List&lt;ComponentInventoryType&gt;** | The inventory types this creative should serve on. |  [optional] |
|**landingPage** | [**ComponentLandingPage**](ComponentLandingPage.md) |  |  [optional] |
|**language** | **LanguageLocale** |  |  [optional] |
|**logos** | [**List&lt;Image&gt;**](Image.md) | The logos to use for the Asset Based Creative experience. |  [optional] |
|**moderationStatus** | [**CreativeStatus**](CreativeStatus.md) |  |  [optional] |
|**optimizationGoalKpi** | **CreativeOptimizationGoalKpi** |  |  [optional] |
|**responsiveSizingBehavior** | **ResponsiveSizingBehavior** |  |  [optional] |
|**squareImages** | [**List&lt;Image&gt;**](Image.md) | The square image(s) to use. |  [optional] |
|**tallImages** | [**List&lt;Image&gt;**](Image.md) | The tall image(s) to use. |  [optional] |
|**untranslatedHeadlines** | **List&lt;String&gt;** | The headline entered by the advertiser. |  [optional] |
|**wideImages** | [**List&lt;Image&gt;**](Image.md) | The wide image(s) to use. |  [optional] |



