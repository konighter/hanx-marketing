# Multi-Platform Product Listing Mechanism Design

## 1. Objective
Establish a unified, scalable mechanism for product data entry across multiple eCommerce platforms (e.g., Amazon, Shopee, TikTok Shop). The system must balance the convenience of "write-once-publish-anywhere" against the strict, platform-specific schema requirements of modern API integrations (like Amazon SP-API).

## 2. Architectural Direction: Pragmatic Hybrid (A + C)
A pure ERP mapping approach is difficult to maintain given how often platform schemas change. A pure schema-driven approach forces users to do repetitive data-entry. 

We will adopt a **Pragmatic Hybrid Architecture**:
*   **ERP Global Takeover (Option C)**: Globally shared attributes (SKU, Main Images, Title) are managed exclusively by the ERP Master Product data pool.
*   **Schema-Driven Custom Components (Option A)**: Platform-specific structures (e.g., 5-point descriptions, platform-specific package dimensions) are dynamically rendered using specialized Vue components that inject into the platform's dynamic schema form.

## 3. System Components & Responsibilities

### 3.1. ERP Master Product (Shared Data Pool)
*   **Scope**: Base SKU, Brand, Title, Main Images, Basic Selling Price, Root Variations.
*   **UI Placement**: Fixed section at the top/left of the listing UI.
*   **Function**: Acts as the singular source of truth. When publishing to a platform, the backend automatically interpolates this master data into the target platform's required schema payload (e.g., mapping ERP main image URL to Amazon's `main_product_image_locator.0.media_location`).

### 3.2. Schema Service (Backend Engine)
*   **Scope**: Parses platform-specific JSON schemas (like Amazon SP-API definitions).
*   **Function**: 
    1.  Recursively flattens complex nested JSON requirements into a flat list of `AmzListingFormFieldVO`.
    2.  Calculates and assigns Linkage Rules (`if-then` dependencies for `hidden` and `required`).
    3.  **New Ability**: Identifies and tags "Composite Fields" (e.g., `item_dimensions`) or "Master Overridden Fields" so the frontend knows how to render them (or skip rendering them entirely).

### 3.3. Dynamic Tabbed Form (Frontend Layout)
*   **Scope**: Collects all remaining platform-specific data.
*   **UI Placement**: Segmented into bottom/right Tabs by Platform (e.g., [ Amazon ] [ Shopee] [ TikTok ]).
*   **Function**: 
    *   Iterates through the fields provided by the `SchemaService`.
    *   **Fallback Rendering**: Renders standard `el-input`, `el-switch`, or `el-select` for plain strings and enums.
    *   **Custom Widget Injection**: When identifying a "Composite Field" (e.g., `bullet_point`), it bypasses the native inputs and mounts a specialized Vue component like `<PlatformBulletPointEditor />` to provide a superior user experience (e.g., drag-and-drop lists) instead of rendering 5 flat text boxes.

### 3.4. Optional Attributes Management (Dynamic Key-Value Table)
*   **Problem**: Platform schemas often contain dozens to hundreds of obscure, optional properties (e.g., specialized compliance metrics, targeted demographics) that clutter the UI and overwhelm users if rendered natively.
*   **Solution**: Universally required attributes remain static in their logical inline groups. However, non-essential, optional properties are hidden by default.
*   **UI Mechanism**: A dedicated "Additional Information" card utilizes a Dynamic Key-Value Table.
    *   Users click "Add Row" to attach a new property.
    *   The left column provides a searchable dropdown populated with all unused optional attributes defined in the schema.
    *   The right column mounts the input structure (text box, select picker) matching the selected attribute's type.
    *   This ensures maximum data-entry power with zero interface bloat.

## 4. Work Flow & Data Pipeline
1.  **Selection**: User selects an internal ERP Category.
2.  **Mapping Initialization**: System maps the ERP Category to the targets (e.g., Amazon `ABIS_BOOK`, Shopee Category ID).
3.  **Data Fetch**: `SchemaService` loads schemas and returns flattened attributes, excluding attributes that are managed by the ERP Master.
4.  **UI Render**: 
    *   Master Form displays standard inputs.
    *   Dynamic Form initializes its two-column inline layout, grouping fields logically (e.g., Compliance, Descriptions).
    *   Composite arrays trigger Custom Vue Widgets.
5.  **Submission (Unflattening & Interpolation)**:
    *   Frontend extracts dynamic fields, un-flattens the flat-keys back into raw JSON object nesting.
    *   Backend intercepts the payload, injects the Master Product data into the required nodes, and submits the finalized transaction to the external Marketplace APIs.

## 5. Security & Validation
*   Strong frontend linkage observation ensures required field dependencies update live.
*   The final composed payload must pass a secondary backend validation standard against the cached JSON schema before triggering an external network call to prevent marketplace rejection limits.
