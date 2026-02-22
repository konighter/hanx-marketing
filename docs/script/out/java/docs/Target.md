

# Target


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adGroupId** | **String** | A unique identifier for the ad group associated with the target. Only used for ad-group level targets. |  [optional] |
|**adProduct** | **AdProduct** |  |  |
|**bid** | [**TargetBid**](TargetBid.md) |  |  [optional] |
|**campaignId** | **String** | A unique identifier for the campaign associated with the target. Only used for campaign-level targets. |  [optional] |
|**creationDateTime** | **OffsetDateTime** | The date time the target was created. |  |
|**globalTargetId** | **String** | The global target identifier that manages this marketplace target. |  [optional] |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time the target was last updated. |  |
|**marketplaceConfigurations** | [**List&lt;MarketplaceTargetConfigurations&gt;**](MarketplaceTargetConfigurations.md) | List of marketplace-specific configurations for a global target that enables overriding certain attributes at individual marketplace level. For example, if a global target is ENABLED but needs to be PAUSED in DE marketplace, you can specify: [{marketplace: DE, overrides: {state: PAUSED}}]. When a marketplace-specific override is not provided, the target&#39;s global value is applied to that marketplace. |  [optional] |
|**marketplaceScope** | **MarketplaceScope** |  |  [optional] |
|**marketplaces** | **List&lt;Marketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  [optional] |
|**negative** | **Boolean** | Indicates whether the target is negative or not. |  |
|**state** | **State** |  |  |
|**status** | [**Status**](Status.md) |  |  [optional] |
|**tags** | [**List&lt;Tag&gt;**](Tag.md) | Open ended labels with a key value pair applied to the target |  [optional] |
|**targetDetails** | [**TargetDetails**](TargetDetails.md) |  |  |
|**targetId** | **String** | A unique identifier for the target. |  |
|**targetLevel** | **TargetLevel** |  |  [optional] |
|**targetType** | **TargetType** |  |  |



