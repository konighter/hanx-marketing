# Amazon Listing Dynamic Form V2 Design Spec

## Status: Draft
## Date: 2026-04-08

## 1. Problem Statement

The current Amazon listing dynamic form implementation splits Amazon-specific behavior across `ListingCreate.vue`, `AmazonListingDynamicForm.vue`, and `AmazonUiSchema.ts`.

The main issue is that field grouping is hard-coded inside `AmazonListingDynamicForm.vue` through `AmazonGroups`, while field-level UI overrides are defined separately in `AmazonUiSchema.ts`. This creates three practical problems:

1. Grouping logic is not reusable and cannot be consumed by another renderer.
2. Navigation groups and rendered groups are derived inside the component instead of from a shared data source.
3. Creating `AmazonListingDynamicFormV2` would otherwise require copying grouping rules and increasing divergence.

The requested change is to introduce a unified grouping configuration and build a new `AmazonListingDynamicFormV2` that renders from that configuration while preserving current feature parity.

## 2. Scope

### In Scope

1. Add a unified Amazon grouping configuration as the single source of truth for group order, titles, and field-to-group matching.
2. Add `AmazonListingDynamicFormV2` that consumes the unified grouping configuration.
3. Preserve current feature parity with `AmazonListingDynamicForm.vue`, including:
   - backend schema loading
   - field filtering and preprocessing
   - `purchasable_offer` special handling
   - optional field area
   - linkage-driven visibility and requirement updates
   - exposed `validate()` and `getSubmitData()` contract
   - `schema-groups-updated` output for `ListingCreate.vue`
4. Keep old and new form components available side by side and make the entry point switchable.

### Out of Scope

1. Refactoring `form-v2`.
2. Replacing the existing field-level UI override model in `AmazonUiSchema.ts`.
3. Generalizing the solution for non-Amazon platforms in this iteration.

## 3. Existing Architecture Summary

### 3.1 Entry Layer

`form/ListingCreate.vue` is the current dynamic listing entry. It is responsible for:

1. shop and category selection
2. common listing fields
3. choosing the platform dynamic form component
4. receiving `schema-groups-updated` and rendering the left-side anchor navigation
5. delegating validation and submit-data preparation to the child form

### 3.2 Amazon Dynamic Form Layer

`components/AmazonListingDynamicForm.vue` currently performs all Amazon-specific logic in one place:

1. load backend schema from `/erplus/amz/listing/schema`
2. filter or rewrite fields before rendering
3. apply UI overrides from `AmazonUiSchema.ts`
4. group fields via `AmazonGroups`
5. manage linkage rules and custom visibility logic
6. emit grouped anchor data back to `ListingCreate.vue`

### 3.3 Configuration Layer

`components/AmazonUiSchema.ts` currently contains:

1. field-level UI overrides (`label`, `span`, `uiWidget`, `placeholder`, `tooltip`, `order`)
2. group definitions via `AmazonGroups`
3. several Amazon-specific allowlists and blacklists used during preprocessing

The main structural weakness is that group resolution is embedded in rendering code rather than exposed as a reusable configuration + builder pipeline.

## 4. Proposed Solution

Adopt a minimal structural refactor:

1. Extract a dedicated Amazon grouping configuration.
2. Add a pure grouping builder that converts backend `fields[]` into renderable groups.
3. Build `AmazonListingDynamicFormV2.vue` on top of the existing preprocessing, linkage, and special-block behavior.
4. Keep field-level UI overrides in `AmazonUiSchema.ts` for this iteration.
5. Keep old and new components in parallel and allow the entry page to switch between them.

This keeps the change focused on the requested grouping improvement instead of rewriting the entire Amazon dynamic form engine.

## 5. Target Architecture

## 5.1 `ListingCreate.vue`

Responsibility remains unchanged:

1. collect common listing data
2. render the selected dynamic Amazon component
3. receive `schema-groups-updated`
4. call child `validate()` and `getSubmitData()`

The only change is that it must support choosing between `AmazonListingDynamicForm.vue` and `AmazonListingDynamicFormV2.vue` through a local switchable integration path.

## 5.2 Unified Grouping Configuration

Introduce a dedicated config module for Amazon grouping.

Example shape:

```ts
export interface AmazonFieldGroupRule {
  key: string
  title: string
  matchMode?: 'includes' | 'prefix' | 'suffix' | 'exact'
  match: string[]
  order: number
  fallback?: boolean
}

export interface AmazonFieldGroupingConfig {
  groups: AmazonFieldGroupRule[]
}
```

This configuration is responsible only for:

1. group order
2. group title
3. field matching strategy
4. fallback group behavior

It does not own field-level UI overrides.

## 5.3 Group Builder

Introduce a pure function layer, for example:

```ts
buildAmazonFieldGroups(fields, groupingConfig)
```

Output shape:

```ts
type AmazonResolvedFieldGroup = {
  key: string
  title: string
  anchorId: string
  fields: any[]
}
```

This builder becomes the shared source for:

1. group rendering in V2
2. anchor navigation data emitted to `ListingCreate.vue`

## 5.4 `AmazonListingDynamicFormV2.vue`

`AmazonListingDynamicFormV2.vue` becomes the new rendering shell. It reuses the proven business behavior from the old component, but moves grouping to the unified configuration + builder pipeline.

It must preserve:

1. schema loading
2. preprocessing and filtering
3. UI overrides from `AmazonUiSchema.ts`
4. optional field management
5. `purchasable_offer` block rendering
6. visibility and requirement linkage
7. date linkage behavior
8. `validate()` and `getSubmitData()` exposure

## 6. Logical Flow in V2

The V2 pipeline should run in this order:

1. Load backend schema.
2. Preprocess schema fields.
3. Apply field-level UI overrides.
4. Resolve groups through the unified grouping builder.
5. Render normal fields by group.
6. Inject special blocks where required.
7. Maintain linkage-driven state updates and submit helpers.

### 6.1 Schema Preprocessing

V2 must preserve the current Amazon-specific preprocessing behaviors:

1. filter image-related fields
2. filter variation-only creation fields where current logic already excludes them
3. separate `purchasable_offer.*` fields into the dedicated block
4. keep special hidden-field behavior such as `street_date`
5. preserve price and identifier allowlist/blacklist semantics

### 6.2 Field-Level UI Overrides

V2 should continue to apply `getUiConfigForField()` after preprocessing.

That keeps these concerns stable:

1. label overrides
2. widget overrides
3. grid span
4. field order
5. placeholder and tooltip logic

This avoids a large one-shot rewrite of `AmazonUiSchema.ts`.

### 6.3 Group Resolution

Once fields are normalized and UI-adjusted, V2 resolves groups with the unified group config.

Grouping rules should:

1. evaluate groups in configured order
2. assign the first matching group only
3. place unmatched fields into the fallback group
4. sort fields within each group by existing field `order`, then current required-first fallback behavior

### 6.4 Navigation Output

The `schema-groups-updated` event should be derived from the resolved group result rather than recomputed separately.

This ensures the left-side navigation and rendered sections always remain in sync.

### 6.5 Optional Fields

The optional-field area remains a distinct rendering region in V2.

It is not merged into the standard resolved groups during this iteration because:

1. current UX already treats optional fields as a separate add-on area
2. feature parity is a hard requirement
3. merging it into normal groups would expand scope and increase regression risk

The optional section should still emit a dedicated anchor entry when it contains available content.

### 6.6 `purchasable_offer` Special Block

The dedicated `AmazonPurchasableOffer` rendering must remain in V2.

The block should continue to appear in the configured sales-related group rather than being flattened into generic field rendering.

This preserves:

1. specialized UI
2. current data mapping behavior
3. reduced risk for pricing-related regressions

## 7. Integration Strategy

The old component and V2 must coexist.

Recommended first-stage integration:

1. keep `AmazonListingDynamicForm.vue`
2. add `AmazonListingDynamicFormV2.vue`
3. let `ListingCreate.vue` choose which one to render through a local switchable condition
4. keep the child contract identical for both components

Required external contract:

1. `v-model`
2. `shop-id`
3. `product-type`
4. `schema-groups-updated`
5. exposed `validate()`
6. exposed `getSubmitData()`

## 8. Error Handling

V2 should preserve current user-visible failure behavior:

1. schema load failure shows an error message
2. validation failure blocks submit
3. missing or empty product type short-circuits schema load safely

Additionally, the group builder should be defensive:

1. invalid grouping config should not crash the page
2. fields without matches should always fall back into the fallback group
3. missing optional metadata should not change field visibility semantics

## 9. Verification Plan

## 9.1 Static Verification

Use `schema/example_resp.json` as a stable fixture to compare old and new grouping output.

Verify:

1. same number of main rendered fields after preprocessing
2. same number of optional fields
3. same number of anchor items
4. same `purchasable_offer` extraction behavior

## 9.2 Behavioral Verification

Compare old and new components for these scenarios:

1. `fulfillment_channel_code` toggles FBM/FBA-specific fields correctly
2. `supplier_declared_has_product_identifier_exemption` toggles identifier visibility correctly
3. `merchant_release_date` continues to drive dependent date fields
4. `validate()` returns the same validation behavior
5. `getSubmitData()` keeps the same payload contract

## 9.3 Manual UI Verification

In the entry page, verify:

1. left-side anchors match visible groups exactly
2. switching between old and new components does not break parent submit behavior
3. optional field add/remove behavior is unchanged
4. pricing section placement remains correct

## 10. Risks and Trade-offs

### Risk 1: Grouping parity drift

If the new grouping builder does not match current implicit behavior, the rendered order or anchor layout could change.

Mitigation:

1. keep group resolution simple and deterministic
2. compare V1 and V2 output using the schema fixture

### Risk 2: Hidden coupling with special logic

The old component contains non-grouping logic mixed into rendering flow. Extracting grouping without respecting those assumptions could break visibility or pricing behavior.

Mitigation:

1. keep preprocessing and linkage logic behaviorally identical in the first pass
2. refactor grouping only, not the entire business pipeline

### Risk 3: Over-designing the configuration

Trying to fold grouping and all field UI metadata into one mega config would slow delivery and increase migration cost.

Mitigation:

1. keep grouping config focused on grouping only
2. continue reusing `AmazonUiSchema.ts` for field-level concerns in this iteration

## 11. Recommended Implementation Order

1. extract unified Amazon grouping config
2. implement pure group builder and fixture-based verification
3. implement `AmazonListingDynamicFormV2.vue` with preserved old behavior
4. integrate V2 into `ListingCreate.vue` with switchable coexistence
5. compare V1/V2 behavior using the fixture and manual checks

## 12. Decision Summary

This design adopts a narrow but durable refactor:

1. grouping becomes configuration-driven
2. V2 becomes the new Amazon renderer
3. field-level UI overrides stay where they are for now
4. existing advanced behavior remains intact
5. rollout is controlled through coexistence instead of direct replacement
