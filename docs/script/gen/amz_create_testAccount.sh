curl -X POST 'https://advertising-api.amazon.com/testAccounts' \
-H 'amazon-advertising-api-clientid: amzn1.application-oa2-client.51551e09fba74c5eb4bc9bc8db66d8cf' \
-H 'authorization: Bearer Atza|gQD0RepAAwEBADMOocwqNAuhiXlLViFreWbO7sYr5MSvVQZEV3SmrVv4FR6FYw1q_QJjgCA3P3yCba2VBkD0K68spS8i0FFui5sbo6vvDhxIulcBxjoiIZLVF3hTUfMO3jplpA1FbpbdgMdeW2xQyw1ppKffvygHWsd0d8XArE9_lyci1GD-9BCIarK4i_wJ0aVPIYdryjjLJ7HTsaCT07fQoUU-ysI0KhN8HPhIhq8uI7JNdwJKgDQ-9BQ6Gc9_2Yal1Zq83J4O8vyx4TOUz1r3psY6dfm26pJLns709snvMncMf1Q_RENFUNgg-u_R9uJ_pMotNBYsPrD3_XNvPab8ejCizetMmo9vo8X0k4pkHyvBO0j1cDyvQl2OyECtk7kUp6FTbkUoyEc7wNIP87XyHBCk2rdrkMWNZKyTsX1T-4HeYNS-mkLbkH4LpvlbX1dqOkmlgX7Etx0edA4l5om0iNMiv9_QTmjbJm1QeLHXD97etHnf_WNIeHIkPnQ6TmcefTqy8a3Cs51tMXWTkItqY_PMgFV_hp8ir2LtCukjKTmrHrEyQFGBJgLclZhMnBP0HtiOYuzZzFvJeTIfGu_QBcO8e5W2R_YqZ63hXO67j4hZj2slfsLxb4_kj2fA1tbxcxX1s0Y' \
-H 'content-type: application/json' \
-d '{"countryCode": "FR","accountType": "AUTHOR"}'



curl -X POST 'https://advertising-api.amazon.com/adsApi/v1/query/campaigns' \
-H 'amazon-advertising-api-clientid: amzn1.application-oa2-client.51551e09fba74c5eb4bc9bc8db66d8cf' \
-H 'Amazon-Advertising-API-Scope: 1510514840531949' \
-H 'Amazon-Ads-AccountId: amzn1.ads-account.g.46urv0w7o9cejxduu598uh7vs' \
-H 'authorization: Bearer Atza|gQDd-FnpAwEBAJeJsUw0t6Hm5aMWTCHQkE4U_4EDx3BPFpzgu-BXKyS8ZOTSE89q1rcGmsO2Eh6Ca24PrJdnvdCVRuQgiK1rwxPujXXfKuT1XREV-0C7BDtb-32bwzx3yvkFOVVBPNe3kpmqnZzSjXlUkATMupJMm1DuTEpx9_rYMEMjzXk5hD9jSjFXD4ti6-r4LJUaGx2sGYbiAjKXWTsMw-ZPiBv4TukJs8S-iErLjwUTbLEP6EAmZpOKRsEVvd_OKD3t6exasAu5fdqQdaAT6xa30edcop0i-mT4pF6u_oXKAGUOMtCfIkKi9QdY-F_9efk_C_Ja2vvfSzCsJ3Xd5572k1ruiAoPdHsJb4MNL_J-wCIwmMsyXGouVnKG6U1VuImU0KtjVjvO9MbGIpxuobcEd5je0WtJRmOI9nFXwpuOMHttoiq0ad3js0A90lmkJccY4adPNNyiP8NkkN3icK7SGrq5H073_M99miMG4Dw9Sm9Gg-01U2UCBz0XG7ouwoF_WggVFBZKUpOXj-KHEZrJ3hbIAuDUEoNQb0GOiATct0BsAiyn2Nh5d4kWtWKr0ezYxJ81zmdRLasnqlSSfisIiSBSJXY5rlMjlsejHlRw0pjkP8wWJsJhMcnP3wtEyrVE' \
-H 'content-type: application/json' \
-d '{
    "adProductFilter": {
    "include": ["SPONSORED_PRODUCTS"]
    },
    "maxResults": 1000,
    }'




curl -X GET \
https://advertising-api.amazon.com/testAccounts?requestId=KC81GXZ71EFV80CXX3FJ \
-H 'amazon-advertising-api-clientid: amzn1.application-oa2-client.51551e09fba74c5eb4bc9bc8db66d8cf' \
-H 'authorization: Bearer Atza|gQD0RepAAwEBADMOocwqNAuhiXlLViFreWbO7sYr5MSvVQZEV3SmrVv4FR6FYw1q_QJjgCA3P3yCba2VBkD0K68spS8i0FFui5sbo6vvDhxIulcBxjoiIZLVF3hTUfMO3jplpA1FbpbdgMdeW2xQyw1ppKffvygHWsd0d8XArE9_lyci1GD-9BCIarK4i_wJ0aVPIYdryjjLJ7HTsaCT07fQoUU-ysI0KhN8HPhIhq8uI7JNdwJKgDQ-9BQ6Gc9_2Yal1Zq83J4O8vyx4TOUz1r3psY6dfm26pJLns709snvMncMf1Q_RENFUNgg-u_R9uJ_pMotNBYsPrD3_XNvPab8ejCizetMmo9vo8X0k4pkHyvBO0j1cDyvQl2OyECtk7kUp6FTbkUoyEc7wNIP87XyHBCk2rdrkMWNZKyTsX1T-4HeYNS-mkLbkH4LpvlbX1dqOkmlgX7Etx0edA4l5om0iNMiv9_QTmjbJm1QeLHXD97etHnf_WNIeHIkPnQ6TmcefTqy8a3Cs51tMXWTkItqY_PMgFV_hp8ir2LtCukjKTmrHrEyQFGBJgLclZhMnBP0HtiOYuzZzFvJeTIfGu_QBcO8e5W2R_YqZ63hXO67j4hZj2slfsLxb4_kj2fA1tbxcxX1s0Y'




{
  "adsAccounts" : [
  {
    "accountName" : "3P_US_AUTHOR_TestAccount2026-02-21T12:23:12Z",
    "adsAccountId" : "amzn1.ads-account.g.226iehxry0un9azip9jejll0s",
    "alternateIds" : [
    {
      "countryCode" : "US",
      "profileId" : 1122516150482654
    },
    {
      "countryCode" : "US",
      "entityId" : "ENTITY3ERYPKLMBRN9B"
    }
    ],

    "countryCodes" : [ "US" ],
    "status" : "CREATED"
  }
  ]
}



[

{
  "profileId" : 1122516150482654,
  "countryCode" : "US",
  "currencyCode" : "USD",
  "timezone" : "America/Los_Angeles",
  "accountInfo" : {
    "marketplaceStringId" : "ATVPDKIKX0DER",
    "id" : "ENTITY3ERYPKLMBRN9B",
    "type" : "vendor",
    "name" : "3P_US_AUTHOR_TestAccount2026-02-21T12:23:12Z",
    "subType" : "KDP_AUTHOR",
    "validPaymentMethod" : true
  }
}

]



{
  "id" : null,
  "name" : null,
  "type" : null,
  "subType" : null,
  "accountName" : "hanzhan",
  "adsAccountId" : "amzn1.ads-account.g.46urv0w7o9cejxduu598uh7vs",
  "alternateIds" : [ {
    "profileId" : "723502758791071",
    "countryCode" : "CA"
  }, {
    "profileId" : "1852173230602337",
    "countryCode" : "CA"
  }, {
    "profileId" : "1510514840531949",
    "countryCode" : "US"
  }, {
    "profileId" : "667704933758557",
    "countryCode" : "US"
  }, {
    "profileId" : "36770612627578",
    "countryCode" : "MX"
  }, {
    "entityId" : "ENTITY2JKAUIP6C2QW9",
    "countryCode" : "CA"
  }, {
    "entityId" : "ENTITY2Q6XPQHVD52EH",
    "countryCode" : "US"
  }, {
    "entityId" : "ENTITY2A4DOTMTXKUTU",
    "countryCode" : "MX"
  } ],
  "countryCodes" : [ "CA", "US", "MX" ],
  "marketplaceStringId" : null
}
