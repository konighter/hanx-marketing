# Design Spec: Unified Amazon SP-API Listings Items Lifecycle

## 1. Overview
The goal is to implement a unified CRUD (Create, Read, Update, Delete) strategy for Amazon SP-API Listings Items across all `productType`s. The system will leverage existing JSON schemas stored in the database to provide dynamic validation and platform-specific attribute handling while maintaining a standardized "Common Core" for cross-platform business logic.

## 2. Architecture: Common / Private Split
To balance cross-platform consistency with Amazon's complexity, we use a hybrid data model.

### 2.1 Common Core (The "Public" Model)
- **Model**: `ProductModel`
- **Source**: Parsed from the Amazon `attributes` and `summaries` by `AmazonListingParserUtils`.
- **Purpose**: Global searching, multi-platform linking (ASIN/SKU mapping), and high-level inventory/price management.
- **Fields**: SKU, ASIN, MarketplaceID, Title, Main Image, Price, Inventory, FulfillType, Status.

### 2.2 Platform Private (The "Black Box" Attributes)
- **Model**: Hybrid storage.
  - **Key-Value**: `erplus_cross_product_attrs` stores flattened key attributes.
  - **Full Blob**: `erplus_cross_product.extra` stores the rich, nested Amazon JSON.
- **Source**: Original JSON structure from Amazon SP-API.
- **Purpose**: Custom frontend property editing and platform-specific metadata.
- **Validation**: Enforced via the JSON Schema retrieved from `erplus_cross_meta_category_attribute.extra`. 
  - **Constraints**: Enforces `maxLength`, `enum`, `minimum/maximum`, and nested object layouts.
  - **Conditional Logic**: Supports `if-then` (or `allOf/oneOf`) dependencies (e.g., if Category A is selected, Category B attributes become mandatory).

## 3. Lifecycle Workflows

### 3.1 Search & Sync (Read Path)
1. **Fetch**: SP-API -> `searchListingsItems`.
2. **Parse Core**: `AmazonListingParserUtils` maps fields to `ProductModel`.
3. **Persist**: 
    - `listing_item` (Core Info)
    - `listing_attribute_amazon` (Opaque JSON Blob).

### 3.2 Create & Update (Write Path)
1. **UI**: `AmazonDynamicForm` renders based on the `CategoryAttributeModel` (parsed from extra).
2. **Validation**: `ListingSchemaValidator` checks the payload against the logic rules (including conditionals).
3. **Transformation**: Updates are converted to **JSON Patch** format.
4. **Variations**: System automatically generates the `relationships` block for Parent/Child based on the internal `RelationShipModel`.

## 4. Key Components

### 4.1 `ListingSchemaValidator` (Backend)
New service utilizing `networknt/json-schema-validator` to handle Draft 7 logic and field-level error mapping.

### 4.2 `AmazonSchemaSyncTask` (Backend)
Scheduled job to refresh schemas from Amazon SP-API Definitions V2020-09-01.

### 4.3 `AmazonDynamicForm` (Frontend)
Reactive form engine for Vue 3 that handles:
- Grouped display by `groupName`.
- Conditional field visibility (e.g., Country of Origin selection triggers legal disclaimer fields).
- Integrated validation display.

## 5. Multi-Variant Strategy
- **Parent**: System enforces `VARIATION_PARENT` with forbidden physical attributes.
- **Child**: System enforces `VARIATION_CHILD` and requires a matching `variation_theme`.
- **Relationship Generation**: Backend maps common `RelationShipModel` to Amazon's `relationships` attribute array at the API layer.

## 6. Success Criteria
- Support for all 400+ Amazon `productType`s via a single metadata-driven logic.
- Robust handling of complex schema constraints without UI hardcoding.
- Accurate cross-platform SKU linking via extracted core attributes.
