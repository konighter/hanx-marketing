<template>
  <div class="amz-attribute-renderer" v-if="!field.hidden">
    <!-- Case 1: Composite Fields (Nested Objects or Object Arrays) -->
    <template v-if="field.isComposite">
      <AmzCompositeWrapper
        :field="field"
        :model-value="modelValue"
        @update:model-value="handleUpdate"
      />
    </template>

    <!-- Case 2: Scalar Arrays (list of strings/numbers) -->
    <template v-else-if="field.type === 'array'">
      <AmzArrayEditor
        :field="field"
        :model-value="modelValue"
        @update:model-value="handleUpdate"
      />
    </template>

    <!-- Case 3: Leaf Node Fields (Inputs, Selects, etc.) -->
    <template v-else>
      <el-form-item
        :label="field.title"
        :prop="field.id"
        :required="field.required"
      >
        <component
          :is="resolvedComponent"
          v-model="internalValue"
          v-bind="componentProps"
          placeholder="Please enter"
          clearable
          filterable
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
        
        <div v-if="field.description" class="field-desc">
          {{ field.description }}
        </div>
      </el-form-item>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, ref } from 'vue';
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
  description?: string;
  options?: Array<{ label: string; value: any }>;
  children?: Field[];
}

const props = defineProps<{
  field: Field;
  modelValue: any;
}>();

const emit = defineEmits(['update:modelValue']);

// Resolve the component based on the registry
const resolvedComponent = computed(() => 
  resolveComponent(props.field.uiWidget, props.field.type, !!props.field.isComposite)
);

// Component specific props (e.g. rows for textarea)
const componentProps = computed(() => {
  const extraProps: any = {};
  if (props.field.uiWidget === 'textarea') {
    extraProps.type = 'textarea';
    extraProps.rows = 3;
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
  margin-bottom: 12px;
}
.field-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}
</style>
