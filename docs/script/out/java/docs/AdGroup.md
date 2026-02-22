

# AdGroup


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adGroupId** | **String** | The unique identifier of the ad group. |  |
|**adProduct** | **AdProduct** |  |  |
|**advertisedProductCategoryIds** | **List&lt;String&gt;** | The array of identifiers of product categories associated with the ad group. For VIDEO ad group type only one parent product category or multiple sub-categories from one parent product category are allowed. |  [optional] |
|**bid** | [**AdGroupBid**](AdGroupBid.md) |  |  [optional] |
|**budgets** | [**List&lt;Budget&gt;**](Budget.md) | An object containing budget details for the ad group. |  [optional] |
|**campaignId** | **String** | The unique identifier of the campaign the ad group belongs to. |  |
|**creationDateTime** | **OffsetDateTime** | The date time that the ad group was created. |  |
|**creativeRotationType** | **CreativeRotationType** |  |  [optional] |
|**creativeType** | **CreativeType** |  |  [optional] |
|**endDateTime** | **OffsetDateTime** | The end date time for the ad group. |  [optional] |
|**fees** | [**List&lt;Fee&gt;**](Fee.md) | The fees associated with the ad group. |  [optional] |
|**frequencies** | [**List&lt;Frequency&gt;**](Frequency.md) | An object containing frequency details for the ad group. |  [optional] |
|**globalAdGroupId** | **String** | The global adGroup identifier that manages this marketplace adGroup. |  [optional] |
|**inventoryType** | **InventoryType** |  |  [optional] |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time that the ad group was last updated. |  |
|**marketplaceConfigurations** | [**List&lt;MarketplaceAdGroupConfigurations&gt;**](MarketplaceAdGroupConfigurations.md) | List of marketplace-specific configurations for a global ad group that enables overriding certain attributes at individual marketplace level. For example, if a global ad group state is ENABLED and needs to be PAUSED only in DE marketplace, you can specify: [{marketplace: DE, overrides: {state: PAUSED}}]. When a marketplace-specific override is not provided, ad group&#39;s global value is applied to that marketplace. |  [optional] |
|**marketplaceScope** | **MarketplaceScope** |  |  [optional] |
|**marketplaces** | **List&lt;Marketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  [optional] |
|**name** | **String** | The name of the ad group. |  |
|**optimization** | [**Optimization**](Optimization.md) |  |  [optional] |
|**pacing** | [**Pacing**](Pacing.md) |  |  [optional] |
|**purchaseOrderNumber** | **String** | The purchase order number associated with the ad group. |  [optional] |
|**startDateTime** | **OffsetDateTime** | The start date time for the ad group. |  [optional] |
|**state** | **State** |  |  |
|**status** | [**Status**](Status.md) |  |  [optional] |
|**tags** | [**List&lt;Tag&gt;**](Tag.md) | Open ended labels with a key value pair applied to the ad group |  [optional] |
|**targetingSettings** | [**TargetingSettings**](TargetingSettings.md) |  |  [optional] |



