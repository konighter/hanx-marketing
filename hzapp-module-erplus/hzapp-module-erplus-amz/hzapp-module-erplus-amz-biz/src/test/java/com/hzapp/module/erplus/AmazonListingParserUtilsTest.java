package com.hzapp.module.erplus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.amz.spapi.utils.AmazonListingParserUtils;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.model.common.ProductAttributeModel;
import com.hzltd.module.erplus.spapi.model.product.MultiMarketProductModel;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonListingParserUtilsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testParseAmazonListing() throws Exception {
        String json = "{\n" +
                "  \"sku\" : \"BG-002\",\n" +
                "  \"summaries\" : [ {\n" +
                "    \"marketplaceId\" : \"ATVPDKIKX0DER\",\n" +
                "    \"asin\" : \"B0GVM6TV7V\",\n" +
                "    \"itemName\" : \"NewOasis 557PCS Charms Lip Gloss Keychain Making Kit\"\n" +
                "  } ],\n" +
                "  \"attributes\" : {\n" +
                "    \"color\" : [ {\n" +
                "      \"value\" : \"Gold-557pcs kit\",\n" +
                "      \"marketplace_id\" : \"ATVPDKIKX0DER\"\n" +
                "    } ],\n" +
                "    \"purchasable_offer\" : [ {\n" +
                "      \"our_price\" : [ {\n" +
                "        \"schedule\" : [ {\n" +
                "          \"value_with_tax\" : 21.99\n" +
                "        } ]\n" +
                "      } ],\n" +
                "      \"discounted_price\" : [ {\n" +
                "        \"schedule\" : [ {\n" +
                "          \"value_with_tax\" : 18.99,\n" +
                "          \"start_at\" : \"2024-01-01T00:00:00Z\",\n" +
                "          \"end_at\" : \"2024-12-31T23:59:59Z\"\n" +
                "        } ]\n" +
                "      } ],\n" +
                "      \"currency\" : \"USD\",\n" +
                "      \"audience\" : \"ALL\",\n" +
                "      \"marketplace_id\" : \"ATVPDKIKX0DER\"\n" +
                "    } ],\n" +
                "    \"bullet_point\" : [ {\n" +
                "      \"value\" : \"Point 1\",\n" +
                "      \"marketplace_id\" : \"ATVPDKIKX0DER\"\n" +
                "    }, {\n" +
                "      \"value\" : \"Point 2\",\n" +
                "      \"marketplace_id\" : \"ATVPDKIKX0DER\"\n" +
                "    } ],\n" +
                "    \"brand\" : [ {\n" +
                "      \"value\" : \"NewOasis\",\n" +
                "      \"marketplace_id\" : \"ATVPDKIKX0DER\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"fulfillmentAvailability\" : [ {\n" +
                "    \"fulfillmentChannelCode\" : \"AMAZON\",\n" +
                "    \"quantity\" : 10\n" +
                "  } ]\n" +
                "}";

        JsonNode itemNode = mapper.readTree(json);
        MultiMarketProductModel result = AmazonListingParserUtils.parse(itemNode);

        assertNotNull(result);
        assertTrue(result.containsKey("ATVPDKIKX0DER"));

        ProductModel product = result.get("ATVPDKIKX0DER");
        assertEquals("BG-002", product.getSellerSku());
        assertEquals("B0GVM6TV7V", product.getProductCode());
        assertEquals("NewOasis 557PCS Charms Lip Gloss Keychain Making Kit", product.getProductName());

        // Test Fulfillment (AMAZON -> FBA)
        assertEquals(FulfillTypeEnum.FBA, product.getFulfillType());

        // Test Pricing
        assertNotNull(product.getPrices());
        assertEquals(2, product.getPrices().size());
        
        // our_price (ALL)
        assertEquals(new BigDecimal("21.99"), product.getPrices().get(0).getAmount());
        assertEquals("ALL", product.getPrices().get(0).getType());

        // discounted_price (discounted)
        assertEquals(new BigDecimal("18.99"), product.getPrices().get(1).getAmount());
        assertEquals("discounted", product.getPrices().get(1).getType());
        assertNotNull(product.getPrices().get(1).getStartAt());
        assertNotNull(product.getPrices().get(1).getEndAt());
        
        // Test Brand
        assertEquals("NewOasis", product.getBrand());

        // Test Bullet Points (Array preservation)
        assertNotNull(product.getProductAttributes());
        assertTrue(product.getProductAttributes().stream().anyMatch(a -> "bullet_point".equals(a.getAttrName())));
        ProductAttributeModel bulletAttr = product.getProductAttributes().stream()
                .filter(a -> "bullet_point".equals(a.getAttrName()))
                .findFirst().get();
        assertEquals(2, bulletAttr.getAttrValues().size());
        assertEquals("Point 1", bulletAttr.getAttrValues().get(0).getCustomerValue());
        assertEquals("Point 2", bulletAttr.getAttrValues().get(1).getCustomerValue());
    }
}
