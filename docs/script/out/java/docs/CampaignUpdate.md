

# CampaignUpdate


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adProduct** | **AdProduct** |  |  [optional] |
|**budgets** | [**List&lt;CreateBudget&gt;**](CreateBudget.md) | The object containing budget details for the campaign (for campaigns that support multiple budgets). |  [optional] |
|**campaignId** | **String** | A unique identifier for a campaign. |  |
|**costType** | **CostType** |  |  [optional] |
|**countries** | **List&lt;CountryCode&gt;** | This field is used in Sponsored Ads and ADSP and impacts targeted supply. For Sponsored Ads, the campaign.countries field determines what Amazon retail supply (Amazon.com, Amazon.co.uk, Amazon.mx, etc) the campaign will serve in. Similarly in ADSP, this has an implicit filter on your inventory targets. If you choose an inventory target of AMAZON with campaign.countries set to US, this will target the retail supply of Amazon.com and non-retail Amazon properties. ADSP options include additional countries - for example, choosing Austria means targeting Austria eligible inventory and Amazon retail supply of Amazon.de. |  [optional] |
|**endDateTime** | **OffsetDateTime** | The end date time for the campaign. |  [optional] |
|**fees** | [**List&lt;CreateCampaignFee&gt;**](CreateCampaignFee.md) | Any fees associated with the campaign. |  [optional] |
|**flights** | [**List&lt;CreateCampaignFlight&gt;**](CreateCampaignFlight.md) | Flight details associated with the campaign. |  [optional] |
|**frequencies** | [**List&lt;CreateFrequency&gt;**](CreateFrequency.md) | Any frequency caps associated with the campaign. |  [optional] |
|**marketplaceConfigurations** | [**List&lt;CreateMarketplaceCampaignConfigurations&gt;**](CreateMarketplaceCampaignConfigurations.md) | List of marketplace-specific configurations for a global campaign that enables overriding certain attributes at individual marketplace level. For example, if a global campaign is ENABLED and startDate &#39;2024-06-01&#39; but needs to be PAUSED in DE with startDateTime &#39;2024-06-02&#39; marketplace, you can specify: [{marketplace: DE, overrides: {state: PAUSED, startDate: &#39;2024-06-02&#39;}}]. When a marketplace-specific override is not provided, the campaign&#39;s global value is applied to that marketplace. |  [optional] |
|**marketplaces** | **List&lt;Marketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  [optional] |
|**name** | **String** | The name of the campaign. |  [optional] |
|**optimizations** | [**UpdateCampaignOptimizations**](UpdateCampaignOptimizations.md) |  |  [optional] |
|**portfolioId** | **String** | The ID of the portfolio associated with the campaign. |  [optional] |
|**purchaseOrderNumber** | **String** | The purchase order number associated with the campaign. |  [optional] |
|**siteRestrictions** | **List&lt;SiteRestriction&gt;** | Restrict the ad to a particular site |  [optional] |
|**skanAppId** | **String** | StoreKit AdNetwork application ID. Represents iTunes application ID with which SKAN-enabled campaigns are associated. |  [optional] |
|**startDateTime** | **OffsetDateTime** | The start date time for the campaign. |  [optional] |
|**state** | **UpdateState** |  |  [optional] |
|**tags** | [**List&lt;CreateTag&gt;**](CreateTag.md) | Open ended labels with a key value pair applied to the campaign |  [optional] |



