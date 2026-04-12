import { defineAsyncComponent, type Component } from 'vue';

/**
 * Registry for Amazon-specific UI components.
 * This decoupled mapping allows the backend to suggest a 'uiWidget' name,
 * which this registry resolves to an actual Vue component.
 */
export interface WidgetRegistry {
  [key: string]: Component;
}

export const AmzWidgetRegistry: WidgetRegistry = {
  // Base Components (usually provided by Element Plus or project-wide)
  'input': defineAsyncComponent(() => import('element-plus').then(m => m.ElInput)),
  'input-number': defineAsyncComponent(() => import('element-plus').then(m => m.ElInputNumber)),
  'textarea': defineAsyncComponent(() => import('element-plus').then(m => m.ElInput)),
  'select': defineAsyncComponent(() => import('element-plus').then(m => m.ElSelect)),
  'date-picker': defineAsyncComponent(() => import('element-plus').then(m => m.ElDatePicker)),
  'switch': defineAsyncComponent(() => import('element-plus').then(m => m.ElSwitch)),

  // Custom Amazon Business Components
  'bullet-points': defineAsyncComponent(() => import('./AmzArrayEditor.vue')),
  'dimensions': defineAsyncComponent(() => import('./AmzCompositeWrapper.vue')),
  'composite': defineAsyncComponent(() => import('./AmzCompositeWrapper.vue')),
  'array': defineAsyncComponent(() => import('./AmzArrayEditor.vue')),
  'weight-input': defineAsyncComponent(() => import('./AmzWeightInput.vue')),
  'dimensions-input': defineAsyncComponent(() => import('./AmzDimensionsInput.vue')),
  'dimension': defineAsyncComponent(() => import('./AmzDimensionsInput.vue')),
  'purchasable-offer': defineAsyncComponent(() => import('./AmzPurchasableOffer.vue')),
  'fulfillment-availability': defineAsyncComponent(() => import('./AmzFulfillmentAvailability.vue')),
};

/**
 * Resolves the appropriate component for a given field configuration.
 */
export function resolveComponent(uiWidget: string | undefined, type: string, isComposite: boolean, hasOptions?: boolean): string | Component {
  if (uiWidget && AmzWidgetRegistry[uiWidget]) {
    return AmzWidgetRegistry[uiWidget];
  }

  // If options are explicitly provided, default to select regardless of type
  if (hasOptions && !isComposite) {
    return 'el-select';
  }

  // Fallback defaults based on data type
  if (isComposite) {
    return AmzWidgetRegistry['composite'];
  }

  if (type === 'array') {
    return AmzWidgetRegistry['array'];
  }

  if (type === 'number') {
    return 'el-input-number';
  }

  if (type === 'boolean') {
    return 'el-switch';
  }

  if (type === 'enum') {
    return 'el-select';
  }

  return 'el-input';
}
