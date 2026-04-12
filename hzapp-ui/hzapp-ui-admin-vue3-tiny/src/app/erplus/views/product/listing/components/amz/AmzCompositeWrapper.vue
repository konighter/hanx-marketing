<template>
  <div class="amz-composite-wrapper" :class="{ 'is-nested': isNested }">
    <div class="composite-card">
      <!-- Section Header for the Composite Field -->
      <div class="composite-header" v-if="!isNested">
        <span class="header-title">{{ field.title }}</span>
        <span v-if="field.name" class="header-technical">{{ field.name }}</span>
      </div>

      <!-- Array Case: List of Objects -->
      <template v-if="isArrayOfObjects">
        <div v-for="(item, index) in internalList" :key="index" class="item-row">
          <!-- Main Content Area (Fields) -->
          <div class="item-fields">
            <AttributeRenderer
              v-for="child in field.children"
              :key="child.id"
              :field="child"
              :model-value="item[child.name || child.id.split('.').pop()]"
              :is-nested="true"
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

      <!-- Single Object Case -->
      <template v-else>
        <div class="single-object-container">
          <AttributeRenderer
            v-for="child in field.children"
            :key="child.id"
            :field="child"
            :model-value="internalObject[child.name || child.id.split('.').pop()]"
            :is-nested="true"
            @update:model-value="(val) => updateObject(child, val)"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
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
}>();

const emit = defineEmits(['update:modelValue']);

const isArrayOfObjects = computed(() => props.field.type === 'array');

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
  const key = child.name || child.id.split('.').pop()!;
  item[key] = value;
  newList[index] = item;
  emit('update:modelValue', newList);
};

// Handle Single Object
const internalObject = computed(() => props.modelValue || {});

const updateObject = (child: Field, value: any) => {
  const obj = { ...internalObject.value };
  const key = child.name || child.id.split('.').pop()!;
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
  gap: 12px;
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



