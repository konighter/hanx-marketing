# Design Spec: Universal Amazon SP-API Listings Items Parser

## 1. Overview
The goal is to design and implement a universal parsing logic for the Amazon SP-API `Listings Items` response. The parser must transform complex, marketplace-linked JSON into our internal `ProductModel`.

## 2. Requirements
- **Multi-Marketplace Support**: A single item JSON can contain data for multiple marketplaces. The parser must generate a `MultiMarketProductModel`.
- **Dynamic Attribute Mapping**: Amazon attributes vary by product type. The parser must dynamically find and normalize these attributes.
- **Rules**:
    - **Array Persistence**: Attributes that are arrays (e.g., `bullet_point`, `generic_keyword`) must remain as arrays/lists in the internal model.
    - **Fulfillment Normalization**: 
        - The `fulfillType` is a single value in our system.
        - **Rule**: If `fulfillmentChannelCode` starts with `AMAZON_` (e.g., `AMAZON_NA`, `AMAZON_EU`), set `fulfillType` to `FBA`. Otherwise, set to the default (FBM).
    - **Error Handling**: If an entry (marketplace specific) is missing mandatory fields (`asin`, `itemName`), that entry should be skipped.

## 3. Architecture

### 3.1 Data Flow
Raw JSON (ListingsItem) -> `AmazonProductParser` -> `MultiMarketProductModel` -> `saveOrUpdateCrossPlatformProduct`

### 3.2 Parser Logic (`AmazonProductParser`)
1. **Iterate Summaries**: For each entry in `summaries`, extract `marketplaceId`.
2. **Contextual Search**: For each `marketId`:
    - **Validate**: Ensure `asin` and `itemName` exist in the summary. If not, skip this market.
    - **Map Summaries**: ASIN, Title, Main Image, Status.
    - **Map Attributes**:
        - Use a helper `findValues(attributes, key, marketId)` to get all values for a key.
        - Prioritize values with matching `marketplace_id` if available.
    - **Map Fulfillment**: Check `fulfillmentAvailability` for codes starting with `AMAZON_`.
    - **Map Pricing**: Extract `our_price` from `purchasable_offer` under `attributes`.

## 4. Key Transformations

| Target Field (`ProductModel`) | Source Logic |
| :--- | :--- |
| `sellerSku` | `$.sku` |
| `productCode` | `$.summaries[m].asin` |
| `productName` | `$.summaries[m].itemName` |
| `brand` | `$.attributes.brand[m].value` |
| `productAttributes` | List of `ProductAttributeModel` containing `bullet_point`, `color`, etc. |
| `fulfillType` | `AMAZON` if any `fulfillmentChannelCode` starts with `AMAZON_` |
| `status` | Map strings like `DISCOVERABLE` to `CrossListingStatus` |

## 5. Implementation Notes
- Use `Jackson` / `JsonNode` for parsing the dynamic `attributes` section.
- Implementation will reside in a new utility class `AmazonListingParserUtils`.
- **Priority**: Always prefer data from `summaries` for Title/Image as it's the more reliable source in the Listings Items API.
