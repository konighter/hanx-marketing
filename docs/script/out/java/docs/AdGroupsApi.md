# AdGroupsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**sBCreateAdGroup**](AdGroupsApi.md#sBCreateAdGroup) | **POST** /adsApi/v1/create/adGroups |  |
| [**sBCreateAdGroupWithHttpInfo**](AdGroupsApi.md#sBCreateAdGroupWithHttpInfo) | **POST** /adsApi/v1/create/adGroups |  |
| [**sBDeleteAdGroup**](AdGroupsApi.md#sBDeleteAdGroup) | **POST** /adsApi/v1/delete/adGroups |  |
| [**sBDeleteAdGroupWithHttpInfo**](AdGroupsApi.md#sBDeleteAdGroupWithHttpInfo) | **POST** /adsApi/v1/delete/adGroups |  |
| [**sBQueryAdGroup**](AdGroupsApi.md#sBQueryAdGroup) | **POST** /adsApi/v1/query/adGroups |  |
| [**sBQueryAdGroupWithHttpInfo**](AdGroupsApi.md#sBQueryAdGroupWithHttpInfo) | **POST** /adsApi/v1/query/adGroups |  |
| [**sBUpdateAdGroup**](AdGroupsApi.md#sBUpdateAdGroup) | **POST** /adsApi/v1/update/adGroups |  |
| [**sBUpdateAdGroupWithHttpInfo**](AdGroupsApi.md#sBUpdateAdGroupWithHttpInfo) | **POST** /adsApi/v1/update/adGroups |  |



## sBCreateAdGroup

> SBAdGroupMultiStatusResponse sBCreateAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdGroupRequest)



Create ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateAdGroupRequest sbCreateAdGroupRequest = new SBCreateAdGroupRequest(); // SBCreateAdGroupRequest | 
        try {
            SBAdGroupMultiStatusResponse result = apiInstance.sBCreateAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdGroupRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBCreateAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbCreateAdGroupRequest** | [**SBCreateAdGroupRequest**](SBCreateAdGroupRequest.md)|  | [optional] |

### Return type

[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |

## sBCreateAdGroupWithHttpInfo

> ApiResponse<SBAdGroupMultiStatusResponse> sBCreateAdGroup sBCreateAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdGroupRequest)



Create ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateAdGroupRequest sbCreateAdGroupRequest = new SBCreateAdGroupRequest(); // SBCreateAdGroupRequest | 
        try {
            ApiResponse<SBAdGroupMultiStatusResponse> response = apiInstance.sBCreateAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdGroupRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBCreateAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbCreateAdGroupRequest** | [**SBCreateAdGroupRequest**](SBCreateAdGroupRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |


## sBDeleteAdGroup

> SBAdGroupMultiStatusResponse sBDeleteAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdGroupRequest)



Delete ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteAdGroupRequest sbDeleteAdGroupRequest = new SBDeleteAdGroupRequest(); // SBDeleteAdGroupRequest | 
        try {
            SBAdGroupMultiStatusResponse result = apiInstance.sBDeleteAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdGroupRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBDeleteAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbDeleteAdGroupRequest** | [**SBDeleteAdGroupRequest**](SBDeleteAdGroupRequest.md)|  | [optional] |

### Return type

[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |

## sBDeleteAdGroupWithHttpInfo

> ApiResponse<SBAdGroupMultiStatusResponse> sBDeleteAdGroup sBDeleteAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdGroupRequest)



Delete ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteAdGroupRequest sbDeleteAdGroupRequest = new SBDeleteAdGroupRequest(); // SBDeleteAdGroupRequest | 
        try {
            ApiResponse<SBAdGroupMultiStatusResponse> response = apiInstance.sBDeleteAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdGroupRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBDeleteAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbDeleteAdGroupRequest** | [**SBDeleteAdGroupRequest**](SBDeleteAdGroupRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |


## sBQueryAdGroup

> SBAdGroupSuccessResponse sBQueryAdGroup(amazonAdsClientId, sbQueryAdGroupRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryAdGroupRequest sbQueryAdGroupRequest = new SBQueryAdGroupRequest(); // SBQueryAdGroupRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            SBAdGroupSuccessResponse result = apiInstance.sBQueryAdGroup(amazonAdsClientId, sbQueryAdGroupRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBQueryAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **sbQueryAdGroupRequest** | [**SBQueryAdGroupRequest**](SBQueryAdGroupRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

[**SBAdGroupSuccessResponse**](SBAdGroupSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryAdGroup 200 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |

## sBQueryAdGroupWithHttpInfo

> ApiResponse<SBAdGroupSuccessResponse> sBQueryAdGroup sBQueryAdGroupWithHttpInfo(amazonAdsClientId, sbQueryAdGroupRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryAdGroupRequest sbQueryAdGroupRequest = new SBQueryAdGroupRequest(); // SBQueryAdGroupRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            ApiResponse<SBAdGroupSuccessResponse> response = apiInstance.sBQueryAdGroupWithHttpInfo(amazonAdsClientId, sbQueryAdGroupRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBQueryAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **sbQueryAdGroupRequest** | [**SBQueryAdGroupRequest**](SBQueryAdGroupRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

ApiResponse<[**SBAdGroupSuccessResponse**](SBAdGroupSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryAdGroup 200 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |


## sBUpdateAdGroup

> SBAdGroupMultiStatusResponse sBUpdateAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdGroupRequest)



Update ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateAdGroupRequest sbUpdateAdGroupRequest = new SBUpdateAdGroupRequest(); // SBUpdateAdGroupRequest | 
        try {
            SBAdGroupMultiStatusResponse result = apiInstance.sBUpdateAdGroup(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdGroupRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBUpdateAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbUpdateAdGroupRequest** | [**SBUpdateAdGroupRequest**](SBUpdateAdGroupRequest.md)|  | [optional] |

### Return type

[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |

## sBUpdateAdGroupWithHttpInfo

> ApiResponse<SBAdGroupMultiStatusResponse> sBUpdateAdGroup sBUpdateAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdGroupRequest)



Update ad groups  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdGroupsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdGroupsApi apiInstance = new AdGroupsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateAdGroupRequest sbUpdateAdGroupRequest = new SBUpdateAdGroupRequest(); // SBUpdateAdGroupRequest | 
        try {
            ApiResponse<SBAdGroupMultiStatusResponse> response = apiInstance.sBUpdateAdGroupWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdGroupRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdGroupsApi#sBUpdateAdGroup");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |
| **sbUpdateAdGroupRequest** | [**SBUpdateAdGroupRequest**](SBUpdateAdGroupRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdGroupMultiStatusResponse**](SBAdGroupMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateAdGroup 207 response |  -  |
| **400** | BadRequest 400 response |  -  |
| **401** | Unauthorized 401 response |  -  |
| **403** | Forbidden 403 response |  -  |
| **404** | NotFound 404 response |  -  |
| **413** | ContentTooLarge 413 response |  -  |
| **429** | TooManyRequests 429 response |  -  |
| **500** | InternalServerError 500 response |  -  |
| **502** | BadGateway 502 response |  -  |
| **503** | ServiceUnavailableError 503 response |  -  |
| **504** | GatewayTimeout 504 response |  -  |

