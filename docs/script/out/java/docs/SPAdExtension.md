

# SPAdExtension


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**adExtensionId** | **String** | A unique identifier for the ad_extension. |  |
|**adExtensionSettings** | [**SPAdExtensionSettings**](SPAdExtensionSettings.md) |  |  |
|**adExtensionStatus** | **SPAdExtensionStatus** |  |  [optional] |
|**adExtensionType** | **SPAdExtensionType** |  |  |
|**adGroupId** | **String** | A unique identifier for the ad group associated with the ad_extension. |  [optional] |
|**adId** | **String** | A unique identifier for the ad associated with the ad_extension. |  [optional] |
|**adProduct** | **SPAdProduct** |  |  |
|**creationDateTime** | **OffsetDateTime** | The date time the ad_extension was created. |  |
|**lastUpdatedDateTime** | **OffsetDateTime** | The date time the ad_extension was last updated. |  |
|**marketplaceScope** | **SPMarketplaceScope** |  |  |
|**marketplaces** | **List&lt;SPMarketplace&gt;** | The list of marketplace in which the global ad_extension is applicable. The marketplaces included should either be same as or subset of parent campaign/adGroup/ad |  |
|**state** | **SPState** |  |  |
|**status** | [**SPStatus**](SPStatus.md) |  |  [optional] |



