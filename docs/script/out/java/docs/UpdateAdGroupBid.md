

# UpdateAdGroupBid


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**baseBid** | **Double** | The lower bound bid used for the ads in the ad group. |  [optional] |
|**defaultBid** | **Double** | The default maximum bid for ads and targets in the ad group. This is used in sponsored ads as the maximum bid during the auction. |  [optional] |
|**marketplaceSettings** | [**List&lt;CreateAdGroupBidMarketplaceSetting&gt;**](CreateAdGroupBidMarketplaceSetting.md) | The bid associated with the ad group at specified marketplace level. Either one of bid or marketplaceSettings should always be specified |  [optional] |
|**maxAverageBid** | **Double** | The max average bid that will be targeted on the ad group across all of the bids (a single bid could be lower or higher that this number). |  [optional] |



