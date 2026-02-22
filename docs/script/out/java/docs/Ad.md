

# Ad


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**activeCreative** | [**Creative**](Creative.md) |  |  [optional] |
|**adGroupId** | **String** | The ad group associated with the ad. |  [optional] |
|**adId** | **String** | The identifier of the ad. |  |
|**adProduct** | **AdProduct** |  |  |
|**adType** | **AdType** |  |  |
|**campaignId** | **String** | The campaign associated with the ad. It&#39;s a read-only field. |  [optional] |
|**creationDateTime** | **OffsetDateTime** | The date time that the ad was created. |  |
|**creative** | [**Creative**](Creative.md) |  |  |
|**globalAdId** | **String** | The global ad identifier that manages this marketplace ad. |  [optional] |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time that the ad was last updated. |  |
|**marketplaceConfigurations** | [**List&lt;MarketplaceAdConfigurations&gt;**](MarketplaceAdConfigurations.md) | List of marketplace-specific configurations for a global ad that enables overriding certain attributes at individual marketplace level. For example, if a global ad is ENABLED but needs to be PAUSED in DE marketplace, you can specify: [{marketplace: DE, overrides: {state: PAUSED}}]. When a marketplace-specific override is not provided, the ad&#39;s global value is applied to that marketplace. |  [optional] |
|**marketplaceScope** | **MarketplaceScope** |  |  [optional] |
|**marketplaces** | **List&lt;Marketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  [optional] |
|**name** | **String** | The name of the ad. |  [optional] |
|**state** | **State** |  |  |
|**status** | [**Status**](Status.md) |  |  [optional] |
|**tags** | [**List&lt;Tag&gt;**](Tag.md) | Open ended labels with a key value pair applied to the ad |  [optional] |



