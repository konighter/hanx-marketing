package com.hzltd.module.amz.spapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.erplus.spapi.enums.CrossListingStatus;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.model.common.Image;
import com.hzltd.module.erplus.spapi.model.common.PriceModel;
import com.hzltd.module.erplus.spapi.model.common.ProductAttributeModel;
import com.hzltd.module.erplus.spapi.model.product.IssueModel;
import com.hzltd.module.erplus.spapi.model.product.MultiMarketProductModel;
import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AmazonListingParserUtils {

    public static MultiMarketProductModel parse(JsonNode itemNode) {
        MultiMarketProductModel multiMarketProduct = new MultiMarketProductModel();
        if (itemNode == null || !itemNode.has("sku")) {
            return multiMarketProduct;
        }

        String sku = itemNode.get("sku").asText();
        JsonNode summaries = itemNode.get("summaries");
        if (summaries == null || !summaries.isArray()) {
            return multiMarketProduct;
        }

        for (JsonNode summary : summaries) {
            String marketId = summary.get("marketplaceId").asText();
            String asin = summary.has("asin") ? summary.get("asin").asText() : null;
            String itemName = summary.has("itemName") ? summary.get("itemName").asText() : null;

            // Skip if mandatory fields are missing
            if (asin == null || itemName == null) {
                log.warn("Skipping marketplace {} for SKU {} due to missing ASIN or ItemName", marketId, sku);
                continue;
            }

            ProductModel product = multiMarketProduct.getOrCreate(marketId);
            product.setMarketId(marketId);
            product.setProductCode(asin);
            product.setProductName(itemName);
            product.setSellerSku(sku);

            // Handle fnSku and Category
            if (summary.has("fnSku")) {
                product.setFnSku(summary.get("fnSku").asText());
            }
            if (summary.has("productType")) {
                product.setCategory(summary.get("productType").asText());
            }

            // Handle Timestamps
            if (summary.has("createdDate")) {
                product.setCreateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(summary.get("createdDate").asLong()), ZoneOffset.UTC));
            }
            if (summary.has("lastUpdatedDate")) {
                product.setUpdateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(summary.get("lastUpdatedDate").asLong()), ZoneOffset.UTC));
            }

            // Handle Image
            if (summary.has("mainImage")) {
                JsonNode mainImg = summary.get("mainImage");
                if (mainImg.has("link")) {
                    product.setMainImage(new Image().setUrl(mainImg.get("link").asText()));
                }
            }

            // Handle Status
            if (summary.has("status") && summary.get("status").isArray() && !summary.get("status").isEmpty()) {
                String amzStatus = summary.get("status").get(0).asText();
                product.setStatus(mapStatus(amzStatus));
            }

            // Handle Attributes
            if (itemNode.has("attributes")) {
                parseAttributes(product, itemNode.get("attributes"), marketId);
            }

            // Handle Fulfillment
            if (itemNode.has("fulfillmentAvailability")) {
                parseFulfillment(product, itemNode.get("fulfillmentAvailability"));
            }

            // Handle Offers (Dynamic from itemNode)
            if (itemNode.has("offers")) {
                parseOffers(product, itemNode.get("offers"), marketId);
            }
        }

        // Handle Issues (Global per item)
        if (itemNode.has("issues")) {
            parseIssues(multiMarketProduct, itemNode.get("issues"));
        }

        return multiMarketProduct;
    }

    private static void parseAttributes(ProductModel product, JsonNode attributes, String marketId) {
        // Extract Brand
        JsonNode brandNode = findAttribute(attributes, "brand", marketId);
        if (brandNode != null && brandNode.has("value")) {
            product.setBrand(brandNode.get("value").asText());
        }

        // Extract Description
        JsonNode descNode = findAttribute(attributes, "product_description", marketId);
        if (descNode != null && descNode.has("value")) {
            product.setProductDescription(descNode.get("value").asText());
        }

        // Bullet points (Preserve as array)
        JsonNode bulletNode = attributes.get("bullet_point");
        if (bulletNode != null && bulletNode.isArray()) {
            ProductAttributeModel bulletAttr = new ProductAttributeModel();
            bulletAttr.setAttrName("bullet_point");
            List<ProductAttributeModel.AttrValue> values = new ArrayList<>();
            for (JsonNode bullet : bulletNode) {
                if (!bullet.has("marketplace_id") || marketId.equals(bullet.get("marketplace_id").asText())) {
                    ProductAttributeModel.AttrValue val = new ProductAttributeModel.AttrValue();
                    val.setCustomerValue(bullet.get("value").asText());
                    values.add(val);
                }
            }
            if (!values.isEmpty()) {
                bulletAttr.setAttrValues(values);
                product.addProductAttribute(bulletAttr);
            }
        }

        // Extract Price (purchasable_offer -> our_price)
        JsonNode offerNode = findAttribute(attributes, "purchasable_offer", marketId);
        if (offerNode != null && offerNode.has("our_price")) {
            JsonNode ourPrices = offerNode.get("our_price");
            if (ourPrices.isArray() && !ourPrices.isEmpty()) {
                JsonNode schedule = ourPrices.get(0).get("schedule");
                if (schedule != null && schedule.isArray() && !schedule.isEmpty()) {
                    JsonNode priceVal = schedule.get(0).get("value_with_tax");
                    if (priceVal != null) {
                        PriceModel price = new PriceModel();
                        price.setAmount(new BigDecimal(priceVal.asText()));
                        product.addPrice(price);
                    }
                }
            }
        }
    }

    private static void parseFulfillment(ProductModel product, JsonNode fulfillNodes) {
        if (fulfillNodes == null || !fulfillNodes.isArray()) return;
        boolean isFba = false;
        for (JsonNode node : fulfillNodes) {
            if (node.has("fulfillmentChannelCode")) {
                String code = node.get("fulfillmentChannelCode").asText();
                if (code != null && (code.startsWith("AMAZON_") || "AMAZON".equals(code))) {
                    isFba = true;
                    break;
                }
            }
        }
        product.setFulfillType(isFba ? FulfillTypeEnum.FBA : FulfillTypeEnum.FBM);
    }

    private static JsonNode findAttribute(JsonNode attributes, String key, String marketId) {
        JsonNode attrNode = attributes.get(key);
        if (attrNode == null || !attrNode.isArray() || attrNode.isEmpty()) {
            return null;
        }
        for (JsonNode val : attrNode) {
            if (val.has("marketplace_id") && marketId.equals(val.get("marketplace_id").asText())) {
                return val;
            }
        }
        return attrNode.get(0);
    }

    private static void parseIssues(MultiMarketProductModel multiMarketProduct, JsonNode issueNodes) {
        if (!issueNodes.isArray()) return;
        List<IssueModel> issues = new ArrayList<>();
        for (JsonNode issueNode : issueNodes) {
            IssueModel issue = new IssueModel();
            issue.setCode(issueNode.has("code") ? issueNode.get("code").asText() : null);
            issue.setMessage(issueNode.has("message") ? issueNode.get("message").asText() : null);
            issue.setSeverity(issueNode.has("severity") ? issueNode.get("severity").asText() : null);
            issues.add(issue);
        }
        multiMarketProduct.values().forEach(p -> p.setIssues(issues));
    }

    private static void parseOffers(ProductModel product, JsonNode offerNodes, String marketId) {
        if (!offerNodes.isArray()) return;
        for (JsonNode offerNode : offerNodes) {
            if (offerNode.has("marketplaceId") && marketId.equals(offerNode.get("marketplaceId").asText())) {
                PriceModel offer = new PriceModel();
                if (offerNode.has("price")) {
                    JsonNode price = offerNode.get("price");
                    offer.setCurrency(price.has("currencyCode") ? price.get("currencyCode").asText() : null);
                    offer.setAmount(price.has("amount") ? new BigDecimal(price.get("amount").asText()) : null);
                }
                offer.setType(offerNode.has("offerType") ? offerNode.get("offerType").asText() : null);
                product.addOffer(offer);
            }
        }
    }

    private static CrossListingStatus mapStatus(String amzStatus) {
        if ("DISCOVERABLE".equalsIgnoreCase(amzStatus) || "BUYABLE".equalsIgnoreCase(amzStatus)) {
            return CrossListingStatus.ACTIVATE;
        }
        return CrossListingStatus.PENDING;
    }
}
