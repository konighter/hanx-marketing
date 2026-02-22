curl -X POST 'https://advertising-api.amazon.com/testAccounts' \
-H 'amazon-advertising-api-clientid: amzn1.application-oa2-client.51551e09fba74c5eb4bc9bc8db66d8cf' \
-H 'authorization: Bearer Atza|gQD0RepAAwEBADMOocwqNAuhiXlLViFreWbO7sYr5MSvVQZEV3SmrVv4FR6FYw1q_QJjgCA3P3yCba2VBkD0K68spS8i0FFui5sbo6vvDhxIulcBxjoiIZLVF3hTUfMO3jplpA1FbpbdgMdeW2xQyw1ppKffvygHWsd0d8XArE9_lyci1GD-9BCIarK4i_wJ0aVPIYdryjjLJ7HTsaCT07fQoUU-ysI0KhN8HPhIhq8uI7JNdwJKgDQ-9BQ6Gc9_2Yal1Zq83J4O8vyx4TOUz1r3psY6dfm26pJLns709snvMncMf1Q_RENFUNgg-u_R9uJ_pMotNBYsPrD3_XNvPab8ejCizetMmo9vo8X0k4pkHyvBO0j1cDyvQl2OyECtk7kUp6FTbkUoyEc7wNIP87XyHBCk2rdrkMWNZKyTsX1T-4HeYNS-mkLbkH4LpvlbX1dqOkmlgX7Etx0edA4l5om0iNMiv9_QTmjbJm1QeLHXD97etHnf_WNIeHIkPnQ6TmcefTqy8a3Cs51tMXWTkItqY_PMgFV_hp8ir2LtCukjKTmrHrEyQFGBJgLclZhMnBP0HtiOYuzZzFvJeTIfGu_QBcO8e5W2R_YqZ63hXO67j4hZj2slfsLxb4_kj2fA1tbxcxX1s0Y' \
-H 'content-type: application/json' \
-d '{"countryCode": "FR","accountType": "AUTHOR"}'




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
