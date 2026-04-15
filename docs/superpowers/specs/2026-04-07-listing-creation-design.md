# Listing Creation Page (Hybrid Dynamic Form) Design Spec

## Goal Description
The objective is to create a dynamic "Listing Creation" page for cross-border e-commerce. The page will allow users to select a shop via a **Platform -> Shop** cascader, choose a category, and then provide platform-specific attributes through a dynamic sub-form.

## Architecture & Components

| Component | Responsibility |
| :--- | :--- |
| `ListingCreate.vue` | Main entry point. Handles shop selection, category selection, and common product fields (Title, Desc, Images). |
| `AmazonListingDynamicForm.vue` | Platform-specific sub-form for Amazon. Fetches and renders Amazon-specific attributes for the selected category. |
| `ShopCascaderSelect.vue` | Reusable component for selecting Platform and Shop in a hierarchical way. |
| `SpuSelectModal.vue` | Optional tool to select an existing local SPU to pre-fill common fields. |

## Data Flow & State Management

- **`publishRequest` (Parent State)**:
    - `crossPlatform`: Derived from the selected shop.
    - `shopIds`: List of selected shops (usually 1 for creation).
    - `categoryId`: Selected platform category ID.
    - `commonInfo`: { `title`, `description`, `mainImages` }.
    - `dynamicAttributes`: Collection of attributes from the sub-form.

- **Props from Parent to Dynamic Child**:
    - `shopId`: Used for platform-specific API calls.
    - `categoryId`: The selected platform category.
    - `initialData`: Existing attribute values (useful for SPU pre-fill or drafts).

## Interaction Flow

1.  **Shop Selection**: User uses `ShopCascaderSelect`. On change, the parent identifies the `crossPlatform` type.
2.  **Category Selection**: User selects the platform category (Common field in Parent).
3.  **Dynamic Rendering**: Based on `crossPlatform`, the parent renders the correct sub-component:
    ```vue
    <component 
      :is="getDynamicFormComponent(crossPlatform)" 
      :shop-id="formData.shopId"
      :category-id="formData.categoryId"
      v-model="formData.dynamicAttributes" 
    />
    ```
4.  **Optional SPU Linking**: User can click "Import from SPU" to select a local product and pre-fill the Title, Description, and Images.
