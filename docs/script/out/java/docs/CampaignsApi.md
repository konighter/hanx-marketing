# CampaignsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**sBCreateCampaign**](CampaignsApi.md#sBCreateCampaign) | **POST** /adsApi/v1/create/campaigns |  |
| [**sBCreateCampaignWithHttpInfo**](CampaignsApi.md#sBCreateCampaignWithHttpInfo) | **POST** /adsApi/v1/create/campaigns |  |
| [**sBDeleteCampaign**](CampaignsApi.md#sBDeleteCampaign) | **POST** /adsApi/v1/delete/campaigns |  |
| [**sBDeleteCampaignWithHttpInfo**](CampaignsApi.md#sBDeleteCampaignWithHttpInfo) | **POST** /adsApi/v1/delete/campaigns |  |
| [**sBQueryCampaign**](CampaignsApi.md#sBQueryCampaign) | **POST** /adsApi/v1/query/campaigns |  |
| [**sBQueryCampaignWithHttpInfo**](CampaignsApi.md#sBQueryCampaignWithHttpInfo) | **POST** /adsApi/v1/query/campaigns |  |
| [**sBUpdateCampaign**](CampaignsApi.md#sBUpdateCampaign) | **POST** /adsApi/v1/update/campaigns |  |
| [**sBUpdateCampaignWithHttpInfo**](CampaignsApi.md#sBUpdateCampaignWithHttpInfo) | **POST** /adsApi/v1/update/campaigns |  |



## sBCreateCampaign

> SBCampaignMultiStatusResponse sBCreateCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateCampaignRequest)



Create campaigns  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateCampaignRequest sbCreateCampaignRequest = new SBCreateCampaignRequest(); // SBCreateCampaignRequest | 
        try {
            SBCampaignMultiStatusResponse result = apiInstance.sBCreateCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateCampaignRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBCreateCampaign");
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
| **sbCreateCampaignRequest** | [**SBCreateCampaignRequest**](SBCreateCampaignRequest.md)|  | [optional] |

### Return type

[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateCampaign 207 response |  -  |
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

## sBCreateCampaignWithHttpInfo

> ApiResponse<SBCampaignMultiStatusResponse> sBCreateCampaign sBCreateCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateCampaignRequest)



Create campaigns  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBCreateCampaignRequest sbCreateCampaignRequest = new SBCreateCampaignRequest(); // SBCreateCampaignRequest | 
        try {
            ApiResponse<SBCampaignMultiStatusResponse> response = apiInstance.sBCreateCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbCreateCampaignRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBCreateCampaign");
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
| **sbCreateCampaignRequest** | [**SBCreateCampaignRequest**](SBCreateCampaignRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBCreateCampaign 207 response |  -  |
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


## sBDeleteCampaign

> SBCampaignMultiStatusResponse sBDeleteCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteCampaignRequest)



Delete campaigns  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteCampaignRequest sbDeleteCampaignRequest = new SBDeleteCampaignRequest(); // SBDeleteCampaignRequest | 
        try {
            SBCampaignMultiStatusResponse result = apiInstance.sBDeleteCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteCampaignRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBDeleteCampaign");
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
| **sbDeleteCampaignRequest** | [**SBDeleteCampaignRequest**](SBDeleteCampaignRequest.md)|  | [optional] |

### Return type

[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteCampaign 207 response |  -  |
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

## sBDeleteCampaignWithHttpInfo

> ApiResponse<SBCampaignMultiStatusResponse> sBDeleteCampaign sBDeleteCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteCampaignRequest)



Delete campaigns  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBDeleteCampaignRequest sbDeleteCampaignRequest = new SBDeleteCampaignRequest(); // SBDeleteCampaignRequest | 
        try {
            ApiResponse<SBCampaignMultiStatusResponse> response = apiInstance.sBDeleteCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbDeleteCampaignRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBDeleteCampaign");
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
| **sbDeleteCampaignRequest** | [**SBDeleteCampaignRequest**](SBDeleteCampaignRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBDeleteCampaign 207 response |  -  |
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


## sBQueryCampaign

> SBCampaignSuccessResponse sBQueryCampaign(amazonAdsClientId, sbQueryCampaignRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



Query campaign  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryCampaignRequest sbQueryCampaignRequest = new SBQueryCampaignRequest(); // SBQueryCampaignRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            SBCampaignSuccessResponse result = apiInstance.sBQueryCampaign(amazonAdsClientId, sbQueryCampaignRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBQueryCampaign");
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
| **sbQueryCampaignRequest** | [**SBQueryCampaignRequest**](SBQueryCampaignRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

[**SBCampaignSuccessResponse**](SBCampaignSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryCampaign 200 response |  -  |
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

## sBQueryCampaignWithHttpInfo

> ApiResponse<SBCampaignSuccessResponse> sBQueryCampaign sBQueryCampaignWithHttpInfo(amazonAdsClientId, sbQueryCampaignRequest, amazonAdsAccountId, amazonAdvertisingAPIScope)



Query campaign  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;dsp_campaign_view\&quot;, \&quot;campaign_view\&quot;,  \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        SBQueryCampaignRequest sbQueryCampaignRequest = new SBQueryCampaignRequest(); // SBQueryCampaignRequest | 
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        try {
            ApiResponse<SBCampaignSuccessResponse> response = apiInstance.sBQueryCampaignWithHttpInfo(amazonAdsClientId, sbQueryCampaignRequest, amazonAdsAccountId, amazonAdvertisingAPIScope);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBQueryCampaign");
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
| **sbQueryCampaignRequest** | [**SBQueryCampaignRequest**](SBQueryCampaignRequest.md)|  | |
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | [optional] |
| **amazonAdvertisingAPIScope** | **String**| The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input. | [optional] |

### Return type

ApiResponse<[**SBCampaignSuccessResponse**](SBCampaignSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SBQueryCampaign 200 response |  -  |
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


## sBUpdateCampaign

> SBCampaignMultiStatusResponse sBUpdateCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateCampaignRequest)



Update campaign  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateCampaignRequest sbUpdateCampaignRequest = new SBUpdateCampaignRequest(); // SBUpdateCampaignRequest | 
        try {
            SBCampaignMultiStatusResponse result = apiInstance.sBUpdateCampaign(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateCampaignRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBUpdateCampaign");
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
| **sbUpdateCampaignRequest** | [**SBUpdateCampaignRequest**](SBUpdateCampaignRequest.md)|  | [optional] |

### Return type

[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateCampaign 207 response |  -  |
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

## sBUpdateCampaignWithHttpInfo

> ApiResponse<SBCampaignMultiStatusResponse> sBUpdateCampaign sBUpdateCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateCampaignRequest)



Update campaign  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;campaign_edit\&quot;, \&quot;dsp_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sb.CampaignsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        CampaignsApi apiInstance = new CampaignsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SBUpdateCampaignRequest sbUpdateCampaignRequest = new SBUpdateCampaignRequest(); // SBUpdateCampaignRequest | 
        try {
            ApiResponse<SBCampaignMultiStatusResponse> response = apiInstance.sBUpdateCampaignWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, sbUpdateCampaignRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling CampaignsApi#sBUpdateCampaign");
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
| **sbUpdateCampaignRequest** | [**SBUpdateCampaignRequest**](SBUpdateCampaignRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SBCampaignMultiStatusResponse**](SBCampaignMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SBUpdateCampaign 207 response |  -  |
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

