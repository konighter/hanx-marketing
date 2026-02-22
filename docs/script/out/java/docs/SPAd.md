

# SPAd


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adGroupId** | **String** | The ad group associated with the ad. |  |
|**adId** | **String** | The identifier of the ad. |  |
|**adProduct** | **SPAdProduct** |  |  |
|**adType** | **SPAdType** |  |  |
|**campaignId** | **String** | The campaign associated with the ad. It&#39;s a read-only field. |  |
|**creationDateTime** | **OffsetDateTime** | The date time that the ad was created. |  |
|**creative** | [**SPCreative**](SPCreative.md) |  |  |
|**globalAdId** | **String** | The global ad identifier that manages this marketplace ad. |  [optional] |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time that the ad was last updated. |  |
|**marketplaceScope** | **SPMarketplaceScope** |  |  |
|**marketplaces** | **List&lt;SPMarketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  |
|**state** | **SPState** |  |  |
|**status** | [**SPStatus**](SPStatus.md) |  |  [optional] |
|**tags** | [**List&lt;SPTag&gt;**](SPTag.md) | Open ended labels with a key value pair applied to the ad |  [optional] |



