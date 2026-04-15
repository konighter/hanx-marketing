<template>
  <div class="amz-composite-wrapper" :class="{ 'is-nested': isNested }">
    <div class="composite-card">
      <!-- Section Header for the Composite Field -->
      <div class="composite-header" v-if="!isNested">
        <span v-if="isRequired" class="required-star">*</span>
        <span class="header-title">{{ field.title }}</span>
        <span v-if="field.name" class="header-technical">{{ field.name }}</span>
      </div>

      <!-- Array Case: List of Objects -->
      <template v-if="isArrayOfObjects">
        <div v-for="(item, index) in internalList" :key="index" class="item-row">
          <!-- Main Content Area (Fields) -->
          <div class="item-fields">
            <AttributeRenderer
              v-for="child in visibleChildren"
              :key="child.id"
              :field="child"
              :model-value="item[getChildKey(child)]"
              :is-nested="true"
              :prop-path="getChildPath(child, index)"
              :dynamic-required="getChildRequired(child, index)"
              @update:model-value="(val) => updateItem(index, child, val)"
            />
          </div>

          <!-- Action Area (Delete) -->
          <div class="item-actions">
            <el-button 
              v-if="internalList.length > 1"
              type="danger" 
              link 
              circle
              :icon="Delete"
              class="delete-icon"
              @click="removeItem(index)"
            />
          </div>
        </div>
        
        <!-- Add Button Aligned with Inputs (Offset by label-width) -->
        <div class="add-action-row">
          <el-button @click="addItem" class="add-btn">
            <el-icon><Plus /></el-icon>
            <span>添加</span>
          </el-button>
        </div>
      </template>

      <template v-else>
        <div class="single-object-container">
          <AttributeRenderer
            v-for="child in visibleChildren"
            :key="child.id"
            :field="child"
            :model-value="internalObject[getChildKey(child)]"
            :is-nested="true"
            :prop-path="getChildPath(child)"
            :dynamic-required="getChildRequired(child)"
            @update:model-value="(val) => updateObject(child, val)"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, type Ref } from 'vue';
import { Plus, Delete } from '@element-plus/icons-vue';
import AttributeRenderer from './AttributeRenderer.vue';

interface Field {
  id: string;
  title: string;
  type: string;
  isComposite?: boolean;
  name?: string;
  children?: Field[];
}

const props = defineProps<{
  field: Field;
  modelValue: any;
  isNested?: boolean;
  parentPath?: string; // full dotted path to this field in the model
}>();

const emit = defineEmits(['update:modelValue']);

// Injected State
const requiredOnly = inject<Ref<boolean>>('amzRequiredOnly');
const requirementMap = inject<Ref<Record<string, boolean>>>('amzLinkageRequirement');

// Provide maps and filter state to all nested renderers (including composite children)
provide('amzLinkageRequirement', requirementMap);
provide('amzRequiredOnly', requiredOnly);

const isRequired = computed(() => {
  if (requirementMap?.value) {
    if (requirementMap.value[props.field.id] !== undefined) {
      return requirementMap.value[props.field.id];
    }
  }
  return !!props.field.required;
});

const isArrayOfObjects = computed(() => props.field.type === 'array');

// Root of the current card's requirement status
const isParentRequired = computed(() => {
  return props.field.required || (requirementMap?.value?.[props.field.id] === true);
});

// Filter children based on "Show Required Only"
const visibleChildren = computed(() => {
  if (!props.field.children) return [];
  if (!requiredOnly?.value) return props.field.children;

  // Rule: A sub-field is only "Mandatory" in the UI if BOTH the parent is required AND the child is required.
  // This prevents optional cards from being cluttered with red stars/mandatory sub-fields.
  if (!isParentRequired.value) return [];

  return props.field.children.filter(child => {
    const fullPath = getChildPath(child);
    // Prioritize dynamic map (even if false) over static required flag
    const dynamicReq = requirementMap?.value?.[fullPath] ?? requirementMap?.value?.[child.id];
    if (dynamicReq !== undefined) return dynamicReq;
    
    return !!child.required;
  });
});

// Helper to determine if a specific child should show as required (for asterisk)
// Helper to determine if a specific child should show as required (for asterisk)
const getChildRequired = (child: Field, index?: number) => {
  const fullPath = getChildPath(child, index);
  
  // 1. Check if there's a dynamic override for this specific path or child ID
  const dynamicReq = requirementMap?.value?.[fullPath] ?? requirementMap?.value?.[child.id];
  if (dynamicReq !== undefined) return dynamicReq;

  // 2. Optimization: If the parent group itself is NOT required, 
  // don't show red stars on children unless we are forced by a dynamic path rule (handled in step 1).
  // This prevents optional cards like 'Recommended Browse Nodes' from showing mandatory children.
  const isParentRequired = props.field.required || (props.field.id && requirementMap?.value?.[props.field.id] === true);
  if (!isParentRequired) {
    return false;
  }

  return !!child.required;
};

const getChildKey = (child: Field) => {
  return child.name || child.id.split('.').pop()!;
};

const getChildPath = (child: Field, index?: number) => {
  const base = props.parentPath || props.field.id;
  const key = getChildKey(child);
  if (index !== undefined) {
    return `${base}.${index}.${key}`;
  }
  return `${base}.${key}`;
};

// Handle Array of Objects
const internalList = computed(() => {
  if (Array.isArray(props.modelValue)) return props.modelValue;
  if (props.modelValue && typeof props.modelValue === 'object') return [props.modelValue];
  return [{}];
});

const addItem = () => {
  const newList = [...internalList.value, {}];
  emit('update:modelValue', newList);
};

const removeItem = (index: number) => {
  const newList = [...internalList.value];
  newList.splice(index, 1);
  emit('update:modelValue', newList);
};

const updateItem = (index: number, child: Field, value: any) => {
  const newList = [...internalList.value];
  const item = { ...newList[index] };
  const key = getChildKey(child);
  item[key] = value;
  newList[index] = item;
  emit('update:modelValue', newList);
};

// Handle Single Object
const internalObject = computed(() => props.modelValue || {});

const updateObject = (child: Field, value: any) => {
  const obj = { ...internalObject.value };
  const key = getChildKey(child);
  obj[key] = value;
  emit('update:modelValue', obj);
};
</script>

<style scoped>
.amz-composite-wrapper {
  margin-bottom: 8px;
  width: 100%;
}

.composite-card {
  background-color: #f9fafc;
  border-radius: 4px;
  padding: 8px 0;
}

/* Section Header Styles */
.composite-header {
  display: flex;
  align-items: baseline;
  gap: 4px; /* 减小间距，让红点更贴近 */
  margin-bottom: 8px;
  padding: 0 0 8px 0;
  border-bottom: 1px solid #f0f2f5;
  margin-left: 20px;
  margin-right: 20px;
}

.header-title {
  font-size: 14px;
  font-weight: bold;
  color: #1a1a1a;
}

.required-star {
  color: #f56c6c;
  font-size: 14px;
  font-family: SimSun, sans-serif;
  margin-right: 4px;
}

.header-technical {
  font-size: 13px;
  color: #999;
  font-weight: normal;
}

/* Array Item Row Styles */
.item-row {
  display: flex;
  align-items: flex-start;
  padding: 2px 0;
  margin-bottom: 0px;
  position: relative;
}

.item-fields {
  flex: 1;
  min-width: 0;
  padding-right: 20px;
}

.item-actions {
  width: 50px;
  display: flex;
  justify-content: center;
  padding-top: 8px;
}

.delete-icon {
  font-size: 18px;
  color: #f56c6c;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.delete-icon:hover {
  opacity: 1;
  color: #f56c6c;
}

/* Single Object Container */
.single-object-container {
  padding: 8px 20px 8px 0;
}

/* Add Button Styles */
.add-action-row {
  margin-top: 8px;
  padding-left: 180px; /* Align with input start */
}

.add-btn {
  height: 32px;
  padding: 0 16px;
  font-size: 13px;
  color: #606266;
  border-color: #dcdfe6;
}

.add-btn :deep(.el-icon) {
  margin-right: 4px;
}

/* Recursive Nesting Tweak */
.is-nested .composite-card {
  background-color: #fff;
  border: 1px dashed #ebeef5;
  padding: 12px 0;
}

.is-nested .composite-header {
  padding-left: 180px;
}
</style>



