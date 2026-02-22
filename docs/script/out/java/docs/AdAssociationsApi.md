# AdAssociationsApi

All URIs are relative to *https://advertising-api.amazon.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createAdAssociation**](AdAssociationsApi.md#createAdAssociation) | **POST** /adsApi/v1/create/adAssociations |  |
| [**createAdAssociationWithHttpInfo**](AdAssociationsApi.md#createAdAssociationWithHttpInfo) | **POST** /adsApi/v1/create/adAssociations |  |
| [**deleteAdAssociation**](AdAssociationsApi.md#deleteAdAssociation) | **POST** /adsApi/v1/delete/adAssociations |  |
| [**deleteAdAssociationWithHttpInfo**](AdAssociationsApi.md#deleteAdAssociationWithHttpInfo) | **POST** /adsApi/v1/delete/adAssociations |  |
| [**queryAdAssociation**](AdAssociationsApi.md#queryAdAssociation) | **POST** /adsApi/v1/query/adAssociations |  |
| [**queryAdAssociationWithHttpInfo**](AdAssociationsApi.md#queryAdAssociationWithHttpInfo) | **POST** /adsApi/v1/query/adAssociations |  |
| [**updateAdAssociation**](AdAssociationsApi.md#updateAdAssociation) | **POST** /adsApi/v1/update/adAssociations |  |
| [**updateAdAssociationWithHttpInfo**](AdAssociationsApi.md#updateAdAssociationWithHttpInfo) | **POST** /adsApi/v1/update/adAssociations |  |



## createAdAssociation

> AdAssociationMultiStatusResponse createAdAssociation(amazonAdsAccountId, amazonAdsClientId, createAdAssociationRequest)



Create Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        CreateAdAssociationRequest createAdAssociationRequest = new CreateAdAssociationRequest(); // CreateAdAssociationRequest | 
        try {
            AdAssociationMultiStatusResponse result = apiInstance.createAdAssociation(amazonAdsAccountId, amazonAdsClientId, createAdAssociationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#createAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **createAdAssociationRequest** | [**CreateAdAssociationRequest**](CreateAdAssociationRequest.md)|  | [optional] |

### Return type

[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | CreateAdAssociation 207 response |  -  |
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

## createAdAssociationWithHttpInfo

> ApiResponse<AdAssociationMultiStatusResponse> createAdAssociation createAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, createAdAssociationRequest)



Create Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        CreateAdAssociationRequest createAdAssociationRequest = new CreateAdAssociationRequest(); // CreateAdAssociationRequest | 
        try {
            ApiResponse<AdAssociationMultiStatusResponse> response = apiInstance.createAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, createAdAssociationRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#createAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **createAdAssociationRequest** | [**CreateAdAssociationRequest**](CreateAdAssociationRequest.md)|  | [optional] |

### Return type

ApiResponse<[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | CreateAdAssociation 207 response |  -  |
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


## deleteAdAssociation

> AdAssociationMultiStatusResponse deleteAdAssociation(amazonAdsAccountId, amazonAdsClientId, deleteAdAssociationRequest)



Delete Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        DeleteAdAssociationRequest deleteAdAssociationRequest = new DeleteAdAssociationRequest(); // DeleteAdAssociationRequest | 
        try {
            AdAssociationMultiStatusResponse result = apiInstance.deleteAdAssociation(amazonAdsAccountId, amazonAdsClientId, deleteAdAssociationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#deleteAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **deleteAdAssociationRequest** | [**DeleteAdAssociationRequest**](DeleteAdAssociationRequest.md)|  | [optional] |

### Return type

[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | DeleteAdAssociation 207 response |  -  |
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

## deleteAdAssociationWithHttpInfo

> ApiResponse<AdAssociationMultiStatusResponse> deleteAdAssociation deleteAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, deleteAdAssociationRequest)



Delete Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        DeleteAdAssociationRequest deleteAdAssociationRequest = new DeleteAdAssociationRequest(); // DeleteAdAssociationRequest | 
        try {
            ApiResponse<AdAssociationMultiStatusResponse> response = apiInstance.deleteAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, deleteAdAssociationRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#deleteAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **deleteAdAssociationRequest** | [**DeleteAdAssociationRequest**](DeleteAdAssociationRequest.md)|  | [optional] |

### Return type

ApiResponse<[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | DeleteAdAssociation 207 response |  -  |
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


## queryAdAssociation

> AdAssociationSuccessResponse queryAdAssociation(amazonAdsAccountId, amazonAdsClientId, queryAdAssociationRequest)



Query Ad Association  **Requires one of these permissions**: [\&quot;creatives_view\&quot;, \&quot;campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        QueryAdAssociationRequest queryAdAssociationRequest = new QueryAdAssociationRequest(); // QueryAdAssociationRequest | 
        try {
            AdAssociationSuccessResponse result = apiInstance.queryAdAssociation(amazonAdsAccountId, amazonAdsClientId, queryAdAssociationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#queryAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **queryAdAssociationRequest** | [**QueryAdAssociationRequest**](QueryAdAssociationRequest.md)|  | [optional] |

### Return type

[**AdAssociationSuccessResponse**](AdAssociationSuccessResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | QueryAdAssociation 200 response |  -  |
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

## queryAdAssociationWithHttpInfo

> ApiResponse<AdAssociationSuccessResponse> queryAdAssociation queryAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, queryAdAssociationRequest)



Query Ad Association  **Requires one of these permissions**: [\&quot;creatives_view\&quot;, \&quot;campaign_view\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        QueryAdAssociationRequest queryAdAssociationRequest = new QueryAdAssociationRequest(); // QueryAdAssociationRequest | 
        try {
            ApiResponse<AdAssociationSuccessResponse> response = apiInstance.queryAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, queryAdAssociationRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#queryAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **queryAdAssociationRequest** | [**QueryAdAssociationRequest**](QueryAdAssociationRequest.md)|  | [optional] |

### Return type

ApiResponse<[**AdAssociationSuccessResponse**](AdAssociationSuccessResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | QueryAdAssociation 200 response |  -  |
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


## updateAdAssociation

> AdAssociationMultiStatusResponse updateAdAssociation(amazonAdsAccountId, amazonAdsClientId, updateAdAssociationRequest)



Update Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        UpdateAdAssociationRequest updateAdAssociationRequest = new UpdateAdAssociationRequest(); // UpdateAdAssociationRequest | 
        try {
            AdAssociationMultiStatusResponse result = apiInstance.updateAdAssociation(amazonAdsAccountId, amazonAdsClientId, updateAdAssociationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#updateAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **updateAdAssociationRequest** | [**UpdateAdAssociationRequest**](UpdateAdAssociationRequest.md)|  | [optional] |

### Return type

[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | UpdateAdAssociation 207 response |  -  |
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

## updateAdAssociationWithHttpInfo

> ApiResponse<AdAssociationMultiStatusResponse> updateAdAssociation updateAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, updateAdAssociationRequest)



Update Ad Association  **Requires one of these permissions**: [\&quot;campaign_edit\&quot;]

### Example

```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiResponse;
import com.hzltd.module.erplus.adv.adapter.amazon.client.Configuration;
import com.hzltd.module.erplus.adv.adapter.amazon.client.auth.*;
import com.hzltd.module.erplus.adv.adapter.amazon.client.models.*;
import com.hzltd.module.erplus.adv.adapter.amazon.api.all.AdAssociationsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://advertising-api.amazon.com");
        
        // Configure OAuth2 access token for authorization: OAuth2
        OAuth OAuth2 = (OAuth) defaultClient.getAuthentication("OAuth2");
        OAuth2.setAccessToken("YOUR ACCESS TOKEN");

        AdAssociationsApi apiInstance = new AdAssociationsApi(defaultClient);
        String amazonAdsAccountId = "amazonAdsAccountId_example"; // String | The identifier of an Amazon Ads Advertiser Account.
        String amazonAdsClientId = "amazonAdsClientId_example"; // String | The identifier of a client associated with a 'Login with Amazon' account.
        UpdateAdAssociationRequest updateAdAssociationRequest = new UpdateAdAssociationRequest(); // UpdateAdAssociationRequest | 
        try {
            ApiResponse<AdAssociationMultiStatusResponse> response = apiInstance.updateAdAssociationWithHttpInfo(amazonAdsAccountId, amazonAdsClientId, updateAdAssociationRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdAssociationsApi#updateAdAssociation");
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
| **amazonAdsAccountId** | **String**| The identifier of an Amazon Ads Advertiser Account. | |
| **amazonAdsClientId** | **String**| The identifier of a client associated with a &#39;Login with Amazon&#39; account. | |
| **updateAdAssociationRequest** | [**UpdateAdAssociationRequest**](UpdateAdAssociationRequest.md)|  | [optional] |

### Return type

ApiResponse<[**AdAssociationMultiStatusResponse**](AdAssociationMultiStatusResponse.md)>


### Authorization

[OAuth2](../README.md#OAuth2)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **207** | UpdateAdAssociation 207 response |  -  |
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

