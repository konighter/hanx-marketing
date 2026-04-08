# Amazon Listing UI Schema Refactor Design Spec

## Status: Draft
## Date: 2026-04-08

## 1. Problem Statement

The current Amazon listing UI configuration in `AmazonUiSchema.ts` is fragmented into multiple whitelists (`AmazonPriceWhitelist`, `AmazonIdentifierWhitelist`), regex-based group matching, and ad-hoc field overrides. This makes it difficult to:
- Ensure specific fields (like battery compliance) are always visible.
- Manage field ordering and grouping reliably.
- Implement simple field dependencies without complex backend linkage rules.
- Maintain the list of core vs. optional fields.

## 2. Proposed Solution

Refactor the UI schema into a **declarative, group-centric configuration** that provides a single source of truth for:
1. **Grouping**: Explicitly mapping field IDs to UI groups.
2. **Visibility**: Controlling which fields are "Core" (always shown) vs. "Optional".
3. **Local Linkage**: Implementing simple visibility rules (e.g., `Field B` appears only if `Field A` is `true`).

## 3. Data Architecture

### 3.1 `AmazonFieldConfig` Interface

```typescript
export interface AmazonFieldConfig {
  id: string;               // Exact ID or suffix (e.g., 'item_name', 'batteries_required')
  label?: string;           // UI display name override
  order?: number;           // Sort priority within the group
  alwaysShow?: boolean;     // FORCE display in main group (replaces Whitelists)
  required?: boolean;       // Local override for validation
  uiWidget?: string;        // Component type hint
  span?: number;            // Layout grid span (12 or 24)
  dependsOn?: {             // Simple local linkage
    field: string;          // Parent field ID
    value: any | any[];     // Show if parent field matches this value
  };
}
```

### 3.2 `AmazonGroupConfig` Interface

```typescript
export interface AmazonGroupConfig {
  name: string;
  fields: AmazonFieldConfig[];
}
```

## 4. Logical Flow

### 4.1 Field Categorization & Visibility
When `AmazonListingDynamicForm.vue` loads the schema:
1. It iterates through all fields provided by the backend.
2. If a field matches a record in `AmazonUiSchemaConfig`:
   - It is assigned to that group.
   - If `alwaysShow: true` is set, the field's `optional` property is forced to `false`.
3. If a field is **NOT** in the config but the backend marks it as `required: true`:
   - It is assigned to a fallback group: **"其他必填信息 (Other Required Info)"**.
4. Otherwise, the field remains in the "Optional Info" list.

### 4.2 Local Linkage Evaluation
The `isFieldVisible` function will check both:
- **Backend Visibility**: `visibilityMap` populated from Amazon SP-API linkage rules.
- **Local Visibility**: `dependsOn` conditions defined in the UI schema.

## 5. UI Changes

- **Consistent Grouping**: Groups will appear in the order defined in `AmazonUiSchemaConfig`.
- **Improved Hierarchy**: "Basic Info", "Price & Inventory", and "Compliance" will be clearly demarcated.
- **Refined Anchor Navigation**: The side navigation will automatically reflect these structured groups.

## 6. Verification Plan

### 6.1 Automated Checks
- Verify that `alwaysShow: true` fields appear in the main groups regardless of Amazon's `minItems: 0` (optional) status.
- Verify that `dependsOn` correctly toggles child fields when parent values change.

### 6.2 Manual Review
- Test with "ASHTRAY" and "ELECTRONICS" categories to ensure battery fields show up correctly in the "Compliance and Battery" group.
