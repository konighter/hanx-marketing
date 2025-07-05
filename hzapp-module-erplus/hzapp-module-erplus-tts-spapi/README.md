# Java SDK

### Prerequisites

Before integrating TikTok Shop API SDK into your project and making your first API call with the SDK, you must [create a test seller account](https://partner.tiktokshop.com/docv2/page/6789f75a38b3f103167690dc) and [generate a test access token](https://partner.tiktokshop.com/docv2/page/6789f75d2dccb8030e8dece5).

### Integrate Java SDK

Online version of this document: [https://partner.tiktokshop.com/docv2/page/67c83e0799a75104986ae498](Tiktok Shop API SDK)

#### Prerequisites

Ensure your project meets all of the following conditions:

- Java 1.8+
- Maven (3.8.3+)/Gradle (7.2+)

#### Installation

##### For Maven Users

Follow the steps to install the SDK to your existing Java project:

1. Unzip the downloaded package to obtain the source code.
2. Compile the source code locally by executing `mvn clean install`. If there are no errors, the compilation is successful.
3. Add this dependency to your project's POM file and replace `1.0.0` with the version number of the SDK you downloaded.

```XML
<dependency>
  <groupId>com.tiktokshop</groupId>
  <artifactId>open-sdk-java</artifactId>
  <version>1.0.0</version>
  <scope>compile</scope>
</dependency>
```

##### For Gradle Users

Add the following dependency to your `build.gradle` and replace `1.0.0` with the version number of the SDK you downloaded.

```Groovy
  repositories {
    mavenCentral()     // Needed if the 'open-sdk-java' jar has been published to maven central.
    mavenLocal()       // Needed if the 'open-sdk-java' jar has been published to the local maven repo.
  }

  dependencies {
     implementation "com.tiktokshop:open-sdk-java:1.0.0"
  }
```

##### For Others

Generate the JAR by executing:

```Shell
mvn clean package
```

Manually install the following JARs:

- `target/open-sdk-java-1.0.0.jar`
- `target/lib/*.jar`

> Replace `1.0.0` with the version number of the SDK you downloaded.

#### Initialize API request instance

Initialize an API request instance.

```Java
// See [Generate a test access token](https://partner.tiktokshop.com/docv2/page/6789f75d2dccb8030e8dece5)
String accessToken = "TTP_LYt9rAAAAAAKCbT2r9sgYO4E-xQ4E"; 
String contentType = "application/json";
// Shop cipher. See [Search Products](https://partner.tiktokshop.com/docv2/page/67b837b685619104a6846369?external_id=67b837b685619104a6846369#Request_Query)
String cipher = "TTP_PNhwyg23293m93mckl2";
ApiClient defaultClient = Configuration.getDefaultApiClient()
    // To get your AppKey and Secret, see [Step 6 - Create a TikTok Shop App (OAuth client)](https://partner.tiktokshop.com/docv2/page/6789f74e23ae4b030c389e76#Back%20To%20Top)
    .setAppkey("67twuie3j")
    .setSecret("f82ef6e304jr94ma21009493mv92")
    .setBasePath("https://open-api.tiktokglobalshop.com");
ProductV202502Api apiInstance = new ProductV202502Api(defaultClient);
SearchProductsRequestBody searchProductsRequestBody = new SearchProductsRequestBody();
// Refer to the details about the request body of [Search Product 202502](https://partner.tiktokshop.com/docv2/page/67b837b685619104a6846369?external_id=67b837b685619104a6846369)
searchProductsRequestBody.setStatus("ALL");
```

#### Make API Request and Access API Response

Every time you make an API request, TTSPC sends you back a response. Access the response and handle possible errors.

```Java
try {
    SearchProductsResponse result = apiInstance.product202502ProductsSearchPost(1, accessToken, contentType, null, cipher, searchProductsRequestBody);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ProductV202502Api#product202502ProductsSearchPost");
    System.err.println("Status code: " + e.getCode());
    System.err.println("Reason: " + e.getResponseBody());
    System.err.println("Response headers: " + e.getResponseHeaders());
    e.printStackTrace();
}
```

If the API request succeeds, you will get a result which resembles the following content:

```Java
{
  "code": 0,
  "data": {
    "next_page_token": "b2Zmc2V0PTAK",
    "products": [
      {
        "audit": {
          "status": "AUDITING"
        },
        "create_time": 1234567890,
        "id": "1729592969712207008",
        "integrated_platform_statuses": [
          {
            "platform": "TOKOPEDIA",
            "status": "PLATFORM_DEACTIVATED"
          }
        /* ... */
}
```