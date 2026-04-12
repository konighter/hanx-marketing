# Amazon Listing Schema Pipeline & Unified Rendering Design

## 1. Problem Statement

The current Amazon listing implementation in `AmazonListingSchemaService` is a monolithic, hard-to-extend parser. It suffers from:
- **Aggressive Flattening**: Deeply nested structures are forced into flat dot-notated keys, breaking UI logic for complex attributes like `purchasable_offer`.
- **Hardcoded Logic**: Core field logic (Bullet points, Price) is scattered and hardcoded.
- **Fragile Linkage**: Dependency rules are evaluated using fragile string parsing and different logic on frontend vs. backend.
- **Inflexible UI**: No way to customize grouping or ordering per `productType`.

## 2. Proposed Architecture: The 4-Stage Pipeline

We will refactor the schema parsing into a decoupled pipeline that allows per-productType customization and standardized core fields.

### Stage 1: Schema Loading & Optimization
- **SchemaLoader**: Fetches JSON Schema from DB.
- **Caffeine Cache**: Implements an LRU cache (productType -> JsonNode) to avoid redundant parsing of large (200KB+) schemas.

### Stage 2: Structural Transformation (Property Flattening)
- **FlattenStrategy**: Uses a Strategy pattern to decide how deep to flatten.
  - *DefaultStrategy*: Flattens most fields.
  - *ComplexStrategy*: Keeps objects like `purchasable_offer` or `item_dimensions` as nested objects (`isComposite` in `CategoryAttributeModel`).
- **CoreFieldExtractor**: Identifies "Standard" Amazon paths and maps them to a **Standard Core Model** based on `CategoryAttributeModel`.

### Stage 3: Dynamic Linkage Resolution (Visitor Pattern)
- **LinkageRuleResolver**: Traverses the `allOf/if/then` branches using the Visitor pattern.
- **Standardized Rules**: Generates `LinkageRuleVO` (with `LogicExpressionVO`) that the frontend can execute without custom JavaScript strings.
- **Path Aliasing**: Automatically translates Amazon internal paths to ERP Core Model paths (e.g., `purchasable_offer...` -> `core.price`).

### Stage 4: UI Overlay & Grouping
- **UiOverlayMerger**: Merges Amazon's schema with ERP-defined UI metadata.
- **Grouping Logic**: Groups fields into categories like "Basic Info", "Offer", "Compliance", etc., mimicking the high-quality UX of "Lingxing".
- **UI Filtering (Core Feature)**: Implements a "Required Fields" toggle that uses a **Full Hide** strategy—removing optional fields from the DOM to provide a clean, focused experience.
- **Sticky Navigation**: Provides a primary navigation sidebar for jumping between field groups.
- **Optional Field Manager**: A searchable interface allowing users to selectively add optional attributes that are hidden by default.
- **Policy Overrides**: Allows overriding `uiWidget`, `order`, and `groupName` per `productType` via JSON config.

## 5. Handling Complex Structures (Array & Object)

To ensure the UI matches the reference designs:

### 5.1 Nested Objects (e.g., Dimensions)
- **Visual Separation**: Rendered inside a `CompositeFieldWrapper` with a distinct title and background/border to demarcate the area.
- **Depth Control**: The `ComplexStrategy` ensures these aren't flattened into scalar fields if they form a logical business unit.

### 5.2 Array Handling
- **Scalar Arrays** (e.g., Warranty Description): Rendered as a simple list of input fields with "Add" and "Remove" (cross icon) actions per item.
- **Object Arrays** (e.g., Pesticide Marking): Rendered as a list of `CompositeFieldWrapper` blocks. Each block has its own delete button, with a global "Add" button at the bottom of the section.

## 6. Data Model Refinement

### 3.1 Core Model Expansion
We will extend `CategoryAttributeModel` to support nested structures:
- `isComposite`: Boolean indicating if the field contains sub-attributes.
- `compositeAttributes`: List of `CategoryAttributeModel` for nested fields.

### 3.2 Standard Field Mapping Registry
A registry of paths for core fields:
- `item_name` -> Title
- `bullet_point` -> Bullet Points
- `purchasable_offer` -> Price & Offer
- `fulfillment_availability` -> Inventory/Stock
- `main_image_locator` -> Images

## 4. Frontend: DynamicForm V3

- **WidgetRegistry**: Maps `uiWidget` strings to Vue components.
- **AttributeRenderer**: A recursive component that renders `CategoryAttributeModel` trees.
- **Standard Components**: High-level components for `PurchasableOffer`, `BulletPoints`, etc., that bind to the Standard Core Model.

## 5. Verification Plan

### Automated Testing
- Unit tests for `PropertyFlattenStrategy` with `3D_PRINTER` and `LUGAGGE` schemas.
- Integration tests for `LinkageRuleResolver` to ensure `LogicExpressionVO` is correctly generated for FBA/FBM switches.

### Manual Verification
- Verify the "All Fields" vs. "Required Fields" filter functionality in the new UI.
- Confirm that `purchasable_offer` renders as a structured form rather than a giant flat list.
