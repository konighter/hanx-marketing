# TargetsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**sBCreateTarget**](TargetsApi.md#sBCreateTarget) | **POST** /adsApi/v1/create/targets |  |
| [**sBCreateTargetWithHttpInfo**](TargetsApi.md#sBCreateTargetWithHttpInfo) | **POST** /adsApi/v1/create/targets |  |
| [**sBDeleteTarget**](TargetsApi.md#sBDeleteTarget) | **POST** /adsApi/v1/delete/targets |  |
| [**sBDeleteTargetWithHttpInfo**](TargetsApi.md#sBDeleteTargetWithHttpInfo) | **POST** /adsApi/v1/delete/targets |  |
| [**sBQueryTarget**](TargetsApi.md#sBQueryTarget) | **POST** /adsApi/v1/query/targets |  |
| [**sBQueryTargetWithHttpInfo**](TargetsApi.md#sBQueryTargetWithHttpInfo) | **POST** /adsApi/v1/query/targets |  |
| [**sBUpdateTarget**](TargetsApi.md#sBUpdateTarget) | **POST** /adsApi/v1/update/targets |  |
| [**sBUpdateTargetWithHttpInfo**](TargetsApi.md#sBUpdateTargetWithHttpInfo) | **POST** /adsApi/v1/update/targets |  |



## sBCreateTarget

> SBTargetMultiStatusResponse sBCreateTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateTargetRequest)



Create target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateTargetRequest sbCreateTargetRequest = new SBCreateTargetRequest(); // SBCreateTargetRequest | 
        try {
            SBTargetMultiStatusResponse result = apiInstance.sBCreateTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateTargetRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBCreateTarget");
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
| **sbCreateTargetRequest** | [**SBCreateTargetRequest**](SBCreateTargetRequest.md)|  | [optional] |

### Return type

[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateTarget 207 response |  -  |
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

## sBCreateTargetWithHttpInfo

> ApiResponse<SBTargetMultiStatusResponse> sBCreateTarget sBCreateTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateTargetRequest)



Create target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateTargetRequest sbCreateTargetRequest = new SBCreateTargetRequest(); // SBCreateTargetRequest | 
        try {
            ApiResponse<SBTargetMultiStatusResponse> response = apiInstance.sBCreateTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateTargetRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBCreateTarget");
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
| **sbCreateTargetRequest** | [**SBCreateTargetRequest**](SBCreateTargetRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateTarget 207 response |  -  |
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


## sBDeleteTarget

> SBTargetMultiStatusResponse sBDeleteTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteTargetRequest)



Delete target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteTargetRequest sbDeleteTargetRequest = new SBDeleteTargetRequest(); // SBDeleteTargetRequest | 
        try {
            SBTargetMultiStatusResponse result = apiInstance.sBDeleteTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteTargetRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBDeleteTarget");
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
| **sbDeleteTargetRequest** | [**SBDeleteTargetRequest**](SBDeleteTargetRequest.md)|  | [optional] |

### Return type

[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteTarget 207 response |  -  |
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

## sBDeleteTargetWithHttpInfo

> ApiResponse<SBTargetMultiStatusResponse> sBDeleteTarget sBDeleteTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteTargetRequest)



Delete target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteTargetRequest sbDeleteTargetRequest = new SBDeleteTargetRequest(); // SBDeleteTargetRequest | 
        try {
            ApiResponse<SBTargetMultiStatusResponse> response = apiInstance.sBDeleteTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteTargetRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBDeleteTarget");
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
| **sbDeleteTargetRequest** | [**SBDeleteTargetRequest**](SBDeleteTargetRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteTarget 207 response |  -  |
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


## sBQueryTarget

> SBTargetSuccessResponse sBQueryTarget(amazonAdsClientId, sbQueryTargetRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryTargetRequest sbQueryTargetRequest = new SBQueryTargetRequest(); // SBQueryTargetRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            SBTargetSuccessResponse result = apiInstance.sBQueryTarget(amazonAdsClientId, sbQueryTargetRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBQueryTarget");
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
| **sbQueryTargetRequest** | [**SBQueryTargetRequest**](SBQueryTargetRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

[**SBTargetSuccessResponse**](SBTargetSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryTarget 200 response |  -  |
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

## sBQueryTargetWithHttpInfo

> ApiResponse<SBTargetSuccessResponse> sBQueryTarget sBQueryTargetWithHttpInfo(amazonAdsClientId, sbQueryTargetRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



List target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryTargetRequest sbQueryTargetRequest = new SBQueryTargetRequest(); // SBQueryTargetRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            ApiResponse<SBTargetSuccessResponse> response = apiInstance.sBQueryTargetWithHttpInfo(amazonAdsClientId, sbQueryTargetRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBQueryTarget");
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
| **sbQueryTargetRequest** | [**SBQueryTargetRequest**](SBQueryTargetRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

ApiResponse<[**SBTargetSuccessResponse**](SBTargetSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryTarget 200 response |  -  |
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


## sBUpdateTarget

> SBTargetMultiStatusResponse sBUpdateTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateTargetRequest)



Update target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateTargetRequest sbUpdateTargetRequest = new SBUpdateTargetRequest(); // SBUpdateTargetRequest | 
        try {
            SBTargetMultiStatusResponse result = apiInstance.sBUpdateTarget(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateTargetRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBUpdateTarget");
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
| **sbUpdateTargetRequest** | [**SBUpdateTargetRequest**](SBUpdateTargetRequest.md)|  | [optional] |

### Return type

[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateTarget 207 response |  -  |
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

## sBUpdateTargetWithHttpInfo

> ApiResponse<SBTargetMultiStatusResponse> sBUpdateTarget sBUpdateTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateTargetRequest)



Update target  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.TargetsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        TargetsApi apiInstance = new TargetsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateTargetRequest sbUpdateTargetRequest = new SBUpdateTargetRequest(); // SBUpdateTargetRequest | 
        try {
            ApiResponse<SBTargetMultiStatusResponse> response = apiInstance.sBUpdateTargetWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateTargetRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling TargetsApi#sBUpdateTarget");
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
| **sbUpdateTargetRequest** | [**SBUpdateTargetRequest**](SBUpdateTargetRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBTargetMultiStatusResponse**](SBTargetMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateTarget 207 response |  -  |
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

