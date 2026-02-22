

# SPCampaignCreate


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adProduct** | **SPAdProduct** |  |  |
|**autoCreationSettings** | [**SPCreateAutoCreationSettings**](SPCreateAutoCreationSettings.md) |  |  |
|**budgets** | [**List&lt;SPCreateBudget&gt;**](SPCreateBudget.md) | The object containing budget details for the campaign (for campaigns that support multiple budgets). |  |
|**countries** | **List&lt;SPCountryCode&gt;** | This field is used in Sponsored Ads and ADSP and impacts targeted supply. For Sponsored Ads, the campaign.countries field determines what Amazon retail supply (Amazon.com, Amazon.co.uk, Amazon.mx, etc) the campaign will serve in. Similarly in ADSP, this has an implicit filter on your inventory targets. If you choose an inventory target of AMAZON with campaign.countries set to US, this will target the retail supply of Amazon.com and non-retail Amazon properties. ADSP options include additional countries - for example, choosing Austria means targeting Austria eligible inventory and Amazon retail supply of Amazon.de. |  [optional] |
|**endDateTime** | **OffsetDateTime** | The end date time for the campaign. |  [optional] |
|**marketplaceScope** | **SPMarketplaceScope** |  |  |
|**marketplaces** | **List&lt;SPMarketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  [optional] |
|**name** | **String** | The name of the campaign. |  |
|**optimizations** | [**SPCreateCampaignOptimizations**](SPCreateCampaignOptimizations.md) |  |  [optional] |
|**portfolioId** | **String** | The ID of the portfolio associated with the campaign. |  [optional] |
|**siteRestrictions** | **List&lt;SPSiteRestriction&gt;** | Restrict the ad to a particular site |  [optional] |
|**startDateTime** | **OffsetDateTime** | The start date time for the campaign. |  |
|**state** | **SPCreateState** |  |  |
|**tags** | [**List&lt;SPCreateTag&gt;**](SPCreateTag.md) | Open ended labels with a key value pair applied to the campaign |  [optional] |



