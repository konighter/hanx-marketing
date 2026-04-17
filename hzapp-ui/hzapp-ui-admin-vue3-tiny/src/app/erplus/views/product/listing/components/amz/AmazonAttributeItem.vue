<template>
  <div class="amazon-attribute-item">
    <!-- 1. Nested Object / Group -->
    <template v-if="(field.type === 'object' || !field.type) && field.children && field.children.length > 0">
      <div :class="['nested-container', { 'is-grid': depth > 0 || (depth === 0 && field.type === 'object'), 'has-custom-layout': !!layoutRows }]" :depth="depth">
        <!-- Case A: Explicit Custom Layout (Rows) -->
        <template v-if="layoutRows">
          <div v-for="(row, rIndex) in layoutRows" :key="rIndex" class="custom-layout-row">
            <div 
              v-for="child in row" 
              :key="child.id" 
              class="nested-row custom-col"
              :style="{ flex: `1 1 ${getFlexBasis(child, row)}` }"
            >
              <div v-if="row.length > 0" class="nested-label">
                <span :class="{ 'is-required': isFieldRequired(child.id) }">{{ getCustomLabel(child, rIndex, row.indexOf(child)) || getDisplayTitle(child) }}</span>
                <el-tooltip v-if="child.description" :content="child.description" placement="top">
                  <i class="el-icon-info" style="margin-left: 4px; cursor: help;"></i>
                </el-tooltip>
              </div>
              <div class="nested-content">
                <amazon-attribute-item 
                  :model-value="internalValue[child.name]"
                  :field="child" 
                  :is-field-visible="isFieldVisible"
                  :is-field-required="isFieldRequired"
                  :depth="depth + 1"
                  @update:model-value="(val) => handleChildUpdate(child.name, val)"
                  @change="emitChange"
                />
              </div>
            </div>
          </div>
        </template>

        <template v-else v-for="child in visibleChildren" :key="child.id">
          <div 
            class="nested-row"
            :style="{ flex: `1 1 ${getFlexBasis(child, [child])}` }"
          >
            <div v-if="visibleChildren.length > 1" class="nested-label">
              <span :class="{ 'is-required': isFieldRequired(child.id) }">{{ getDisplayTitle(child) }}</span>
              <el-tooltip v-if="child.description" :content="child.description" placement="top">
                <i class="el-icon-info" style="margin-left: 4px; cursor: help;"></i>
              </el-tooltip>
            </div>
            <div class="nested-content">
              <amazon-attribute-item 
                :model-value="internalValue[child.name]"
                :field="child" 
                :is-field-visible="isFieldVisible"
                :is-field-required="isFieldRequired"
                :depth="depth + 1"
                @update:model-value="(val) => handleChildUpdate(child.name, val)"
                @change="emitChange"
              />
            </div>
          </div>
        </template>
      </div>
    </template>

    <!-- 2. Array Type (True Array) -->
    <template v-else-if="field.type === 'array' && !field.options?.length">
      <div class="array-container">
        <div v-for="(item, index) in internalValue" :key="index" class="array-item">
          <div class="array-item-content">
            <!-- Complex Array: Array of objects with multiple fields -->
            <template v-if="field.children && field.children.length > 0">
              <amazon-attribute-item 
                :model-value="internalValue[index]"
                :field="{ ...field, type: 'object' }" 
                :is-field-visible="isFieldVisible"
                :is-field-required="isFieldRequired"
                :depth="depth + 1"
                @update:model-value="(val) => handleArrayItemUpdate(index, val)"
                @change="emitChange"
              />
            </template>
            <!-- Simple Array: Array of strings/numbers -->
            <el-input 
              v-else
              :model-value="internalValue[index]" 
              placeholder="请输入内容" 
              @update:model-value="(val) => handleArrayItemUpdate(index, val)"
            />
          </div>
          <el-button 
            type="text" 
            :icon="Delete" 
            class="delete-btn" 
            :disabled="internalValue.length <= 1"
            @click="removeItem(index)" 
          />
        </div>
        <el-button 
          type="dashed" 
          size="small" 
          :icon="Plus" 
          style="width: 100%" 
          @click="addItem"
        >
          添加 {{ field.title }}
        </el-button>
      </div>
    </template>

    <!-- 3. Standard Widgets (Leaf Nodes) -->
    <template v-else>
      <template v-if="field.uiWidget === 'date-picker' || field.type === 'date'">
        <el-date-picker
          v-model="internalValue"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择日期"
          style="width: 100%"
          @change="emitChange"
        />
      </template>

      <template v-else-if="field.uiWidget === 'textarea'">
        <el-input
          v-model="internalValue"
          type="textarea"
          :rows="3"
          :placeholder="field.title"
          @change="emitChange"
        />
      </template>

      <!-- Enum/Select with Options -->
      <template v-else-if="field.options?.length > 0">
        <el-select
          v-model="internalValue"
          :multiple="field.type === 'array'"
          collapse-tags
          clearable
          filterable
          :placeholder="field.placeholder || field.title"
          style="width: 100%"
          @change="emitChange"
        >
          <el-option
            v-for="opt in field.options"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
      </template>

      <!-- Number Type -->
      <template v-else-if="field.type === 'number' || field.type === 'integer'">
        <el-input-number
          v-model="internalValue"
          :min="field.extra?.minimum"
          :max="field.extra?.maximum"
          style="width: 100%"
          @change="emitChange"
        />
      </template>

      <!-- Boolean Type -->
      <template v-else-if="field.type === 'boolean'">
        <el-switch v-model="internalValue" @change="emitChange" />
      </template>

      <!-- String/Default Type -->
      <template v-else>
        <el-input
          v-model="internalValue"
          :type="field.extra?.multiline ? 'textarea' : 'text'"
          :placeholder="field.title"
          clearable
          style="width: 100%"
          @change="emitChange"
        />
      </template>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { Delete, Plus } from '@element-plus/icons-vue';
import { getUiConfigForField } from './AmazonUiSchema';

// Configuration for specific attribute IDs that require custom row-based layout
const LAYOUT_CONFIG = {
  'purchasable_offer': {
    rows: [
      ['our_price', 'currency'],
      ['start_at', 'end_at'],
      ['map_price', 'minimum_seller_allowed_price', 'maximum_seller_allowed_price'],
      ['discounted_price']
    ],
    labels: [
      ['Price', 'Currency'],
      ['Start At', 'End At'],
      ['MAP', 'Min Price', 'Max Price'],
      ['Discounted']
    ]
  },
  'list_price': {
    rows: [['currency', 'value_with_tax']],
    labels: [['Currency', 'With Tax']]
  },
  'child_parent_sku_relationship': {
    rows: [['child_relationship_type', 'parent_sku']],
    labels: [['Relationship Type', 'Parent SKU']]
  },
  'schedule': {
    rows: [['value_with_tax', 'start_at', 'end_at']],
    labels: [['Price', 'Start', 'End']]
  },
  'item_dimensions': {
    rows: [['length'], ['width'], ['height']],
    labels: [['Length'], ['Width'], ['Height']]
  },
  'item_depth_width_height': {
    rows: [['depth'], ['height'], ['width']],
    labels: [['Depth'], ['Height'], ['Width']]
  },
  'item_length_width_height': {
    rows: [['length'], ['width'], ['height']],
    labels: [['Length'], ['Width'], ['Height']]
  },
  'item_dimensions_front_to_back': {
    rows: [['length'], ['width'], ['height']],
    labels: [['Length'], ['Width'], ['Height']]
  },
  'item_depth_front_to_back': {
    rows: [['value', 'unit']],
    labels: [['Value', 'Unit']]
  },
  'item_package_dimensions': {
    rows: [['length'], ['width'], ['height']],
    labels: [['Length'], ['Width'], ['Height']]
  },
  'main_product_image_locator': {
    rows: [['media_location'], ['marketplace_id']]
  },
  'depth': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'height': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'length': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'width': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'item_package_weight': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'item_weight': { rows: [['value', 'unit']], labels: [['Value', 'Unit']] },
  'voltage': { rows: [['value', 'unit']], labels: [['Voltage', 'Unit']] },
  'input_voltage': { rows: [['value', 'unit']], labels: [['Voltage', 'Unit']] },
  'operating_voltage': { rows: [['value', 'unit']], labels: [['Voltage', 'Unit']] },
  'wattage': { rows: [['value', 'unit']], labels: [['Wattage', 'Unit']] },
  'amperage': { rows: [['value', 'unit']], labels: [['Amperage', 'Unit']] },
  'color': { rows: [['standardized_values', 'value']], labels: [['Map', 'Color']] },
  'standard_color': { rows: [['standardized_values', 'value']], labels: [['Map', 'Color']] },
  'size': { rows: [['standardized_values', 'value']], labels: [['Map', 'Size']] },
  'supplemental_condition_information': {
    rows: [
      ['supplemental_condition_type', 'condition_value'],
      ['accessories', 'cosmetic'],
      ['packaging', 'source_type'],
      ['functional_condition', 'battery_life_percentage'],
      ['features', 'marketplace_id']
    ]
  }
};

const props = withDefaults(defineProps<{
  modelValue: any;
  field: any; // AmzListingFormFieldVO
  isFieldVisible?: (id: string) => boolean;
  isFieldRequired?: (id: string) => boolean;
  depth?: number;
}>(), {
  isFieldVisible: () => true,
  isFieldRequired: () => false,
  depth: 0
});

const emit = defineEmits(['update:modelValue', 'change']);

const visibleChildren = computed(() => {
  if (!props.field.children) return [];
  return props.field.children
    .filter(c => props.isFieldVisible(c.id))
    .map(c => {
      const uiConfig = getUiConfigForField(c.id);
      return { 
        ...c, 
        // Merge UI overrides while keeping original name/id/type
        title: uiConfig.label || c.title,
        span: uiConfig.span || c.span,
        uiWidget: uiConfig.uiWidget || c.uiWidget,
        order: uiConfig.order !== undefined ? uiConfig.order : c.order
      };
    });
});

const layoutRows = computed(() => {
  // Normalize ID (e.g. item_dimensions.0 -> item_dimensions) for layout lookup
  // Fix: Strip all array indices like .0, .1 etc. to match base config IDs
  const normalizedId = props.field.id.replace(/\.\d+/g, '');
  const config = (LAYOUT_CONFIG as any)[normalizedId] || (LAYOUT_CONFIG as any)[props.field.id] || (LAYOUT_CONFIG as any)[props.field.name];
  
  if (!config || !visibleChildren.value.length) return null;

  const rowDefinitions = Array.isArray(config) ? config : config.rows;
  if (!rowDefinitions) return null;

  const rows: any[][] = [];
  const handledIds = new Set<string>();

  // 1. Map explicit groups to actual field objects
  rowDefinitions.forEach((groupNames: string[]) => {
    const row = groupNames
      .map(name => visibleChildren.value.find(c => c.name === name))
      .filter((c): c is any => !!c && props.isFieldVisible(c.id));
    
    if (row.length > 0) {
      rows.push(row);
      row.forEach(c => handledIds.add(c.id));
    }
  });

  // 2. Wrap remaining children into default rows of 1 (forced vertical stack)
  const remaining = visibleChildren.value.filter(c => !handledIds.has(c.id));
  if (remaining.length > 0) {
    for (let i = 0; i < remaining.length; i += 1) {
      rows.push(remaining.slice(i, i + 1));
    }
  }

  return rows;
});

const getCustomLabel = (child: any, rowIndex: number, colIndex: number) => {
  const config = (LAYOUT_CONFIG as any)[props.field.id] || (LAYOUT_CONFIG as any)[props.field.name];
  if (config && config.labels && config.labels[rowIndex]) {
    return config.labels[rowIndex][colIndex];
  }
  return null;
};

/**
 * Shorten labels by removing redundant parent titles as prefixes
 */
const getDisplayTitle = (child: any) => {
  let title = child.title || '';
  const parentTitle = props.field.title;
  if (parentTitle && title.startsWith(parentTitle)) {
    // Strip parent title and common separators
    const suffix = title.substring(parentTitle.length).trim();
    if (suffix) {
      // Capitalize first letter (e.g., "our price" -> "Our price")
      return suffix.charAt(0).toUpperCase() + suffix.slice(1);
    }
  }
  return title;
};

/**
 * Calculate flex-basis based on field span (default 12)
 */
const getFlexBasis = (child: any, row: any[]) => {
  // 1. If the row only has 1 item, it MUST be full width (100%)
  if (row.length === 1) {
    return '100%';
  }
  
  // 2. Determine span: prefer user span, else calculate based on row density
  const span = child.span || (row.length === 2 ? 12 : 8);
  
  // 3. If spans are explicitly defined and valid, use the percentage ratio
  const totalSpan = row.reduce((sum, c) => sum + (c.span || 0), 0);
  if (totalSpan > 0 && totalSpan <= 24) {
    return `calc(${(span / 24) * 100}% - 12px)`;
  }
  
  // 4. Default: split equally to fill the flex container
  const share = Math.floor(100 / row.length);
  return `calc(${share}% - 12px)`;
};

/**
 * Amazon Specific Value Handling
 */
const internalValue = computed({
  get: () => {
    if (props.field.extra?.isAmazonWrapper && props.modelValue && typeof props.modelValue === 'object') {
      return props.modelValue.value;
    }
    const val = props.modelValue;
    if (props.field.type === 'array') {
      const arrayVal = Array.isArray(val) ? val : [];
      if (arrayVal.length === 0) {
        // Initialize with one default item
        if (props.field.children?.length) {
          const newItem = {};
          props.field.children.forEach(c => {
            newItem[c.name] = c.defaultValue ?? null;
          });
          return [newItem];
        } else {
          const defaultValue = props.field.type === 'number' || props.field.type === 'integer' ? 0 : '';
          return [defaultValue];
        }
      }
      return arrayVal;
    }
    if (val !== undefined && val !== null) return val;
    // Return appropriate empty structure
    return props.field.children?.length ? {} : null;
  },
  set: (newVal) => {
    if (props.field.extra?.isAmazonWrapper) {
      const current = typeof props.modelValue === 'object' && props.modelValue !== null 
        ? { ...props.modelValue } 
        : { marketplace_id: '' }; 
      current.value = newVal;
      emit('update:modelValue', current);
    } else {
      emit('update:modelValue', newVal);
    }
  }
});

const handleChildUpdate = (name: string, val: any) => {
  const current = typeof internalValue.value === 'object' && internalValue.value !== null 
    ? { ...internalValue.value } 
    : {};
  current[name] = val;
  internalValue.value = current;
  emitChange();
};

const handleArrayItemUpdate = (index: number, val: any) => {
  const current = Array.isArray(internalValue.value) ? [...internalValue.value] : [];
  current[index] = val;
  internalValue.value = current;
  emitChange();
};

const emitChange = () => {
  emit('change', props.field.id, internalValue.value);
};

const addItem = () => {
  const current = Array.isArray(internalValue.value) ? [...internalValue.value] : [];
  if (props.field.children?.length) {
    const newItem = {};
    props.field.children.forEach(c => {
      newItem[c.name] = c.defaultValue ?? null;
    });
    current.push(newItem);
  } else {
    // For simple arrays (strings/numbers), initialize with an empty string or 0
    const defaultValue = props.field.type === 'number' || props.field.type === 'integer' ? 0 : '';
    current.push(defaultValue);
  }
  internalValue.value = current;
  emitChange();
};

const removeItem = (index: number) => {
  const current = Array.isArray(internalValue.value) ? [...internalValue.value] : [];
  current.splice(index, 1);
  internalValue.value = current;
  emitChange();
};
</script>

<style scoped>
.amazon-attribute-item {
  width: 100%;
}

.nested-container {
  width: 100%;
  margin: 12px 0;
  border-left: 2px solid #e4e7ed;
  padding-left: 16px;
}

/* Generic Grid Layout & Custom Layouts: NO BORDERS, NO PADDING */
.nested-container.is-grid,
.has-custom-layout .nested-container,
.nested-container.is-grid .nested-container,
.nested-container {
  display: flex !important;
  flex-wrap: wrap !important;
  border-left: none !important;
  padding-left: 0 !important;
  margin: 4px 0 !important;
  gap: 16px !important; /* Slightly tighter gap */
}

:deep(.el-date-editor.el-input),
:deep(.el-date-editor.el-input__wrapper),
:deep(.el-select),
:deep(.el-input-number) {
  width: 100% !important;
}

.nested-row {
  margin-bottom: 12px;
  width: 100%; 
  display: block;
}

.nested-container.is-grid .nested-row {
  max-width: 100%;
}

@media (min-width: 1200px) {
  .nested-container.is-grid .nested-row {
    /* Remove hardcoded max-width to allow span: 24 to occupy full width */
  }
}

/* Custom Row-based Layout */
.custom-layout-row {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  margin-bottom: 12px;
  gap: 16px;
}

.custom-col {
  margin-bottom: 0 !important;
}

.nested-content {
  width: 100%;
}

.nested-label {
  font-size: 12px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
}

.is-required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.array-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.array-item-content {
  flex: 1;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f78989;
}
</style>
