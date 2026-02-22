

# CreateDoubleVerifyBrandSafety


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**appAgeRating** | **List&lt;DVBrandSafetyAppAgeRatingType&gt;** | A list of app age ratings to be used for excluding apps. For example, TEENS_12_PLUS will only exclude apps with content rated for everyone ages 12 and over. UNKNOWN will exclude apps with content unrated or unknown to Double Verify. |  [optional] |
|**appStarRating** | **DVBrandSafetyAppStarRatingType** |  |  [optional] |
|**contentCategories** | **List&lt;DVBrandSafetyContentCategoryType&gt;** | A list of content categories to exclude from targeting. |  [optional] |
|**contentCategoriesWithRisk** | [**List&lt;CreateDVBrandSafetyContentCategoriesWithRiskMap&gt;**](CreateDVBrandSafetyContentCategoriesWithRiskMap.md) |  |  [optional] |
|**excludeAppsWithInsufficientRating** | **Boolean** | Set to true to exclude unofficial apps or apps with insufficient user ratings (&lt;100 lifetime). |  [optional] |
|**unknownContent** | **Boolean** | Set to true to exclude unknown content. |  [optional] |



