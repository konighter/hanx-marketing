# AdExtensionsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**sPCreateAdExtension**](AdExtensionsApi.md#sPCreateAdExtension) | **POST** /adsApi/v1/create/adExtensions |  |
| [**sPCreateAdExtensionWithHttpInfo**](AdExtensionsApi.md#sPCreateAdExtensionWithHttpInfo) | **POST** /adsApi/v1/create/adExtensions |  |
| [**sPQueryAdExtension**](AdExtensionsApi.md#sPQueryAdExtension) | **POST** /adsApi/v1/query/adExtensions |  |
| [**sPQueryAdExtensionWithHttpInfo**](AdExtensionsApi.md#sPQueryAdExtensionWithHttpInfo) | **POST** /adsApi/v1/query/adExtensions |  |
| [**sPUpdateAdExtension**](AdExtensionsApi.md#sPUpdateAdExtension) | **POST** /adsApi/v1/update/adExtensions |  |
| [**sPUpdateAdExtensionWithHttpInfo**](AdExtensionsApi.md#sPUpdateAdExtensionWithHttpInfo) | **POST** /adsApi/v1/update/adExtensions |  |



## sPCreateAdExtension

> SPAdExtensionMultiStatusResponse sPCreateAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spCreateAdExtensionRequest)



Create ad extensions - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPCreateAdExtensionRequest spCreateAdExtensionRequest = new SPCreateAdExtensionRequest(); // SPCreateAdExtensionRequest | 
        try {
            SPAdExtensionMultiStatusResponse result = apiInstance.sPCreateAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spCreateAdExtensionRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPCreateAdExtension");
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
| **spCreateAdExtensionRequest** | [**SPCreateAdExtensionRequest**](SPCreateAdExtensionRequest.md)|  | [optional] |

### Return type

[**SPAdExtensionMultiStatusResponse**](SPAdExtensionMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SPCreateAdExtension 207 response |  -  |
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

## sPCreateAdExtensionWithHttpInfo

> ApiResponse<SPAdExtensionMultiStatusResponse> sPCreateAdExtension sPCreateAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spCreateAdExtensionRequest)



Create ad extensions - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPCreateAdExtensionRequest spCreateAdExtensionRequest = new SPCreateAdExtensionRequest(); // SPCreateAdExtensionRequest | 
        try {
            ApiResponse<SPAdExtensionMultiStatusResponse> response = apiInstance.sPCreateAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spCreateAdExtensionRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPCreateAdExtension");
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
| **spCreateAdExtensionRequest** | [**SPCreateAdExtensionRequest**](SPCreateAdExtensionRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SPAdExtensionMultiStatusResponse**](SPAdExtensionMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SPCreateAdExtension 207 response |  -  |
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


## sPQueryAdExtension

> SPAdExtensionSuccessResponse sPQueryAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spQueryAdExtensionRequest)



Query ad_extension - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPQueryAdExtensionRequest spQueryAdExtensionRequest = new SPQueryAdExtensionRequest(); // SPQueryAdExtensionRequest | 
        try {
            SPAdExtensionSuccessResponse result = apiInstance.sPQueryAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spQueryAdExtensionRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPQueryAdExtension");
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
| **spQueryAdExtensionRequest** | [**SPQueryAdExtensionRequest**](SPQueryAdExtensionRequest.md)|  | [optional] |

### Return type

[**SPAdExtensionSuccessResponse**](SPAdExtensionSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SPQueryAdExtension 200 response |  -  |
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

## sPQueryAdExtensionWithHttpInfo

> ApiResponse<SPAdExtensionSuccessResponse> sPQueryAdExtension sPQueryAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spQueryAdExtensionRequest)



Query ad_extension - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;, \&quot;advertiser_campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPQueryAdExtensionRequest spQueryAdExtensionRequest = new SPQueryAdExtensionRequest(); // SPQueryAdExtensionRequest | 
        try {
            ApiResponse<SPAdExtensionSuccessResponse> response = apiInstance.sPQueryAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spQueryAdExtensionRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPQueryAdExtension");
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
| **spQueryAdExtensionRequest** | [**SPQueryAdExtensionRequest**](SPQueryAdExtensionRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SPAdExtensionSuccessResponse**](SPAdExtensionSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SPQueryAdExtension 200 response |  -  |
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


## sPUpdateAdExtension

> SPAdExtensionMultiStatusResponse sPUpdateAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spUpdateAdExtensionRequest)



Update ad_extension - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPUpdateAdExtensionRequest spUpdateAdExtensionRequest = new SPUpdateAdExtensionRequest(); // SPUpdateAdExtensionRequest | 
        try {
            SPAdExtensionMultiStatusResponse result = apiInstance.sPUpdateAdExtension(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spUpdateAdExtensionRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPUpdateAdExtension");
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
| **spUpdateAdExtensionRequest** | [**SPUpdateAdExtensionRequest**](SPUpdateAdExtensionRequest.md)|  | [optional] |

### Return type

[**SPAdExtensionMultiStatusResponse**](SPAdExtensionMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SPUpdateAdExtension 207 response |  -  |
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

## sPUpdateAdExtensionWithHttpInfo

> ApiResponse<SPAdExtensionMultiStatusResponse> sPUpdateAdExtension sPUpdateAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spUpdateAdExtensionRequest)



Update ad_extension - API is in open beta  **Requires one of these permissions**: [\&quot;advertiser_campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdExtensionsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdExtensionsApi apiInstance = new AdExtensionsApi(defaultClient);
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdvertisingAPIScope = "amazonAdvertisingAPIScope_example"; // String | The identifier of a profile associated with the advertiser account. Use GET method on Profiles resource to list profiles associated with the access token passed in the HTTP Authorization header and choose profile id profileId from the response to pass it as input.
        SPUpdateAdExtensionRequest spUpdateAdExtensionRequest = new SPUpdateAdExtensionRequest(); // SPUpdateAdExtensionRequest | 
        try {
            ApiResponse<SPAdExtensionMultiStatusResponse> response = apiInstance.sPUpdateAdExtensionWithHttpInfo(amazonAdsClientId, amazonAdsAccountId, amazonAdvertisingAPIScope, spUpdateAdExtensionRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdExtensionsApi#sPUpdateAdExtension");
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
| **spUpdateAdExtensionRequest** | [**SPUpdateAdExtensionRequest**](SPUpdateAdExtensionRequest.md)|  | [optional] |

### Return type

ApiResponse<[**SPAdExtensionMultiStatusResponse**](SPAdExtensionMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | SPUpdateAdExtension 207 response |  -  |
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

