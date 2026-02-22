

# SBTarget


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adGroupId** | **String** | A unique identifier for the ad group associated with the target. Only used for ad-group level targets. |  |
|**adProduct** | **SBAdProduct** |  |  |
|**bid** | [**SBTargetBid**](SBTargetBid.md) |  |  [optional] |
|**campaignId** | **String** | A unique identifier for the campaign associated with the target. Only used for campaign-level targets. |  [optional] |
|**creationDateTime** | **OffsetDateTime** | The date time the target was created. |  |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time the target was last updated. |  |
|**marketplaceScope** | **SBMarketplaceScope** |  |  |
|**marketplaces** | **List&lt;SBMarketplace&gt;** | A list of country codes representing Amazon marketplaces | Marketplace | Description | | --- | --- | | &#x60;AE&#x60; |  | | &#x60;AU&#x60; |  | | &#x60;BE&#x60; |  | | &#x60;BR&#x60; |  | | &#x60;CA&#x60; |  | | &#x60;DE&#x60; |  | | &#x60;EG&#x60; |  | | &#x60;ES&#x60; |  | | &#x60;FR&#x60; |  | | &#x60;GB&#x60; |  | | &#x60;IE&#x60; |  | | &#x60;IN&#x60; |  | | &#x60;IT&#x60; |  | | &#x60;JP&#x60; |  | | &#x60;MX&#x60; |  | | &#x60;NL&#x60; |  | | &#x60;PL&#x60; |  | | &#x60;SA&#x60; |  | | &#x60;SE&#x60; |  | | &#x60;SG&#x60; |  | | &#x60;TR&#x60; |  | | &#x60;US&#x60; |  | | &#x60;ZA&#x60; |  | |  |
|**negative** | **Boolean** | Indicates whether the target is negative or not. |  |
|**state** | **SBState** |  |  |
|**status** | [**SBStatus**](SBStatus.md) |  |  [optional] |
|**targetDetails** | [**SBTargetDetails**](SBTargetDetails.md) |  |  |
|**targetId** | **String** | A unique identifier for the target. |  |
|**targetLevel** | **SBTargetLevel** |  |  |
|**targetType** | **SBTargetType** |  |  |



