# AdsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**sBCreateAd**](AdsApi.md#sBCreateAd) | **POST** /adsApi/v1/create/ads |  |
| [**sBCreateAdWithHttpInfo**](AdsApi.md#sBCreateAdWithHttpInfo) | **POST** /adsApi/v1/create/ads |  |
| [**sBDeleteAd**](AdsApi.md#sBDeleteAd) | **POST** /adsApi/v1/delete/ads |  |
| [**sBDeleteAdWithHttpInfo**](AdsApi.md#sBDeleteAdWithHttpInfo) | **POST** /adsApi/v1/delete/ads |  |
| [**sBQueryAd**](AdsApi.md#sBQueryAd) | **POST** /adsApi/v1/query/ads |  |
| [**sBQueryAdWithHttpInfo**](AdsApi.md#sBQueryAdWithHttpInfo) | **POST** /adsApi/v1/query/ads |  |
| [**sBUpdateAd**](AdsApi.md#sBUpdateAd) | **POST** /adsApi/v1/update/ads |  |
| [**sBUpdateAdWithHttpInfo**](AdsApi.md#sBUpdateAdWithHttpInfo) | **POST** /adsApi/v1/update/ads |  |



## sBCreateAd

> SBAdMultiStatusResponse sBCreateAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdRequest)



Create ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateAdRequest sbCreateAdRequest = new SBCreateAdRequest(); // SBCreateAdRequest | 
        try {
            SBAdMultiStatusResponse result = apiInstance.sBCreateAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBCreateAd");
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
| **sbCreateAdRequest** | [**SBCreateAdRequest**](SBCreateAdRequest.md)|  | [optional] |

### Return type

[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateAd 207 response |  -  |
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

## sBCreateAdWithHttpInfo

> ApiResponse<SBAdMultiStatusResponse> sBCreateAd sBCreateAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdRequest)



Create ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateAdRequest sbCreateAdRequest = new SBCreateAdRequest(); // SBCreateAdRequest | 
        try {
            ApiResponse<SBAdMultiStatusResponse> response = apiInstance.sBCreateAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateAdRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBCreateAd");
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
| **sbCreateAdRequest** | [**SBCreateAdRequest**](SBCreateAdRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateAd 207 response |  -  |
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


## sBDeleteAd

> SBAdMultiStatusResponse sBDeleteAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdRequest)



Delete ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteAdRequest sbDeleteAdRequest = new SBDeleteAdRequest(); // SBDeleteAdRequest | 
        try {
            SBAdMultiStatusResponse result = apiInstance.sBDeleteAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBDeleteAd");
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
| **sbDeleteAdRequest** | [**SBDeleteAdRequest**](SBDeleteAdRequest.md)|  | [optional] |

### Return type

[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteAd 207 response |  -  |
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

## sBDeleteAdWithHttpInfo

> ApiResponse<SBAdMultiStatusResponse> sBDeleteAd sBDeleteAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdRequest)



Delete ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteAdRequest sbDeleteAdRequest = new SBDeleteAdRequest(); // SBDeleteAdRequest | 
        try {
            ApiResponse<SBAdMultiStatusResponse> response = apiInstance.sBDeleteAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteAdRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBDeleteAd");
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
| **sbDeleteAdRequest** | [**SBDeleteAdRequest**](SBDeleteAdRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteAd 207 response |  -  |
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


## sBQueryAd

> SBAdSuccessResponse sBQueryAd(amazonAdsClientId, sbQueryAdRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_view\&quot;, \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryAdRequest sbQueryAdRequest = new SBQueryAdRequest(); // SBQueryAdRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            SBAdSuccessResponse result = apiInstance.sBQueryAd(amazonAdsClientId, sbQueryAdRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBQueryAd");
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
| **sbQueryAdRequest** | [**SBQueryAdRequest**](SBQueryAdRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

[**SBAdSuccessResponse**](SBAdSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryAd 200 response |  -  |
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

## sBQueryAdWithHttpInfo

> ApiResponse<SBAdSuccessResponse> sBQueryAd sBQueryAdWithHttpInfo(amazonAdsClientId, sbQueryAdRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_view\&quot;, \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryAdRequest sbQueryAdRequest = new SBQueryAdRequest(); // SBQueryAdRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            ApiResponse<SBAdSuccessResponse> response = apiInstance.sBQueryAdWithHttpInfo(amazonAdsClientId, sbQueryAdRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBQueryAd");
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
| **sbQueryAdRequest** | [**SBQueryAdRequest**](SBQueryAdRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

ApiResponse<[**SBAdSuccessResponse**](SBAdSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryAd 200 response |  -  |
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


## sBUpdateAd

> SBAdMultiStatusResponse sBUpdateAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdRequest)



Update ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateAdRequest sbUpdateAdRequest = new SBUpdateAdRequest(); // SBUpdateAdRequest | 
        try {
            SBAdMultiStatusResponse result = apiInstance.sBUpdateAd(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBUpdateAd");
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
| **sbUpdateAdRequest** | [**SBUpdateAdRequest**](SBUpdateAdRequest.md)|  | [optional] |

### Return type

[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateAd 207 response |  -  |
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

## sBUpdateAdWithHttpInfo

> ApiResponse<SBAdMultiStatusResponse> sBUpdateAd sBUpdateAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdRequest)



Update ads  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;creatives_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.AdsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdsApi apiInstance = new AdsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateAdRequest sbUpdateAdRequest = new SBUpdateAdRequest(); // SBUpdateAdRequest | 
        try {
            ApiResponse<SBAdMultiStatusResponse> response = apiInstance.sBUpdateAdWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateAdRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdsApi#sBUpdateAd");
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
| **sbUpdateAdRequest** | [**SBUpdateAdRequest**](SBUpdateAdRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBAdMultiStatusResponse**](SBAdMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateAd 207 response |  -  |
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

