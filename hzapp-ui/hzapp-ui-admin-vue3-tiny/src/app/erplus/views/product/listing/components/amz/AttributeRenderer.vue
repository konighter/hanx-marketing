<template>
  <div class="amz-attribute-renderer" v-if="isVisible" :class="{ 'is-nested': isNested }">
    <!-- Case 1: Composite Fields (Recursive) -->
    <!-- We skip this if a uiWidget is explicitly requested for a composite field (e.g. dimensions) -->
    <template v-if="field.isComposite && !field.uiWidget">
      <AmzCompositeWrapper
        :field="field"
        :model-value="modelValue"
        :is-nested="isNested"
        @update:model-value="handleUpdate"
      />
    </template>

    <!-- Case 2: Scalar Arrays (simple list of strings/numbers) -->
    <template v-else-if="field.type === 'array' && !field.uiWidget">
      <AmzArrayEditor
        :field="field"
        :model-value="modelValue"
        @update:model-value="handleUpdate"
      />
    </template>

    <!-- Case 3: Standard Fields or Custom Widgets -->
    <template v-else>
      <el-form-item
        :prop="field.id"
        :required="isRequired"
        :label-width="['purchasable-offer', 'fulfillment-availability'].includes(field.uiWidget) ? '0px' : undefined"
        class="attribute-item"
      >
        <!-- Custom Label for dual-language support -->
        <template #label v-if="field.title && !['purchasable-offer', 'fulfillment-availability'].includes(field.uiWidget)">
          <div class="custom-label">
            <div class="label-text">
              <span class="label-primary">{{ formattedLabel.primary }}</span>
              <el-tooltip
                v-if="field.description"
                :content="field.description"
                placement="top"
              >
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
            <span v-if="formattedLabel.secondary" class="label-secondary">{{ formattedLabel.secondary }}</span>
          </div>
        </template>

        <component
          :is="resolvedComponent"
          v-model="internalValue"
          :field="field"
          v-bind="componentProps"
          placeholder="Please enter"
          clearable
          filterable
          :class="{ 'field-input': true, 'no-label-widget': ['purchasable-offer', 'fulfillment-availability'].includes(field.uiWidget) }"
        >
          <!-- Options for select type -->
          <template v-if="field.options && field.options.length">
            <el-option
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </template>
        </component>
      </el-form-item>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, type Ref } from 'vue';
import { InfoFilled } from '@element-plus/icons-vue';
import { resolveComponent } from './AmzWidgetRegistry';
import AmzCompositeWrapper from './AmzCompositeWrapper.vue';
import AmzArrayEditor from './AmzArrayEditor.vue';

interface Field {
  id: string;
  title: string;
  type: string;
  isComposite?: boolean;
  uiWidget?: string;
  required?: boolean;
  hidden?: boolean;
  allowCustomEnum?: boolean;
  description?: string;
  options?: Array<{ label: string; value: any }>;
  children?: Field[];
}

const props = defineProps<{
  field: Field;
  modelValue: any;
  isNested?: boolean;
  dynamicVisible?: boolean;
  dynamicRequired?: boolean;
}>();

const emit = defineEmits(['update:modelValue']);

// Injected Linkage State (from AmzDynamicForm)
const visibilityMap = inject<Ref<Record<string, boolean>>>('amzLinkageVisibility');
const requirementMap = inject<Ref<Record<string, boolean>>>('amzLinkageRequirement');

const isVisible = computed(() => !props.field.hidden);

const isRequired = computed(() => {
  if (props.dynamicRequired !== undefined) return props.dynamicRequired;
  if (requirementMap?.value && requirementMap.value[props.field.id] !== undefined) {
    return requirementMap.value[props.field.id];
  }
  return !!props.field.required;
});

// Resolve the component based on the registry
const resolvedComponent = computed(() => {
  const component = resolveComponent(props.field.uiWidget, props.field.type, !!props.field.isComposite, !!props.field.options?.length);
  return component;
});

// Format label: Split "English (Chinese)" or "Chinese (English)" into two lines
const formattedLabel = computed(() => {
  const title = props.field.title || '';
  // Match "Primary (Secondary)" or "Primary（Secondary）"
  const match = title.match(/^(.+?)\s*[\(（](.+?)[\)）]$/);
  if (match) {
    return {
      primary: match[1].trim(),
      secondary: match[2].trim()
    };
  }
  return { primary: title, secondary: '' };
});

// Component specific props (e.g. rows for textarea)
const componentProps = computed(() => {
  const extraProps: any = {};
  if (props.field.uiWidget === 'textarea') {
    extraProps.type = 'textarea';
    extraProps.rows = 3;
  }
  // Support open enums (allow manual input)
  if (props.field.type === 'enum' && props.field.allowCustomEnum) {
    extraProps.filterable = true;
    extraProps.allowCreate = true;
    extraProps.defaultFirstOption = true;
  }
  return extraProps;
});

// Internal value management for leaf nodes
const internalValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

const handleUpdate = (val: any) => {
  emit('update:modelValue', val);
};
</script>

<style scoped>
.amz-attribute-renderer {
  margin-bottom: 2px;
}

.attribute-item {
  margin-bottom: 8px !important;
}

.custom-label {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
  text-align: right;
  padding-right: 8px;
  width: 100%;
}

.label-text {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
}

.label-primary {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  white-space: normal;
}

.info-icon {
  font-size: 14px;
  color: #c0c4cc;
  cursor: help;
}

.label-secondary {
  font-size: 11px;
  color: #909399;
  font-weight: normal;
  white-space: normal;
}

.field-input {
  width: 100%;
}

/* Align labels properly when nested */
:deep(.el-form-item__label) {
  display: flex !important;
  justify-content: flex-end !important;
  align-items: flex-start !important;
  padding-top: 5px !important;
  height: auto !important;
}

.is-nested .attribute-item {
  margin-bottom: 8px !important;
}

/* Specific density for nested items */
.is-nested :deep(.el-form-item__content) {
  min-height: 28px;
}
</style>
