<template>
  <div class="amz-composite-wrapper" :class="{ 'is-nested': isNested }">
    <!-- Array Case -->
    <template v-if="isArrayOfObjects">
      <div v-for="(item, index) in internalList" :key="index" class="columnar-row group-row">
        <!-- Left: Attribute Title + Index -->
        <div class="title-column">
          <div class="title-text">
            <span class="main-label">{{ field.title }}</span>
            <span class="index-tag">Item #{{ index + 1 }}</span>
            <span v-if="field.name" class="technical-name">{{ field.name }}</span>
          </div>
          <div class="column-actions" v-if="internalList.length > 1">
            <el-button 
              type="danger" 
              link 
              size="small"
              icon="Delete"
              @click="removeItem(index)"
            >
              删除此项
            </el-button>
          </div>
        </div>

        <!-- Right: Fields -->
        <div class="field-column">
          <div class="children-stack">
            <AttributeRenderer
              v-for="child in field.children"
              :key="child.id"
              :field="child"
              :model-value="item[child.name || child.id.split('.').pop()]"
              @update:model-value="(val) => updateItem(index, child, val)"
            />
          </div>
        </div>
      </div>
      
      <!-- Add Button Row -->
      <div class="add-row">
        <el-button type="primary" plain icon="Plus" @click="addItem">
          添加 {{ field.title }}
        </el-button>
      </div>
    </template>

    <!-- Single Object Case -->
    <template v-else>
      <div class="columnar-row">
        <!-- Left: Attribute Title -->
        <div class="title-column">
          <div class="title-text">
            <span class="main-label">{{ field.title }}</span>
            <span v-if="field.name" class="technical-name">{{ field.name }}</span>
          </div>
        </div>

        <!-- Right: Fields -->
        <div class="field-column">
          <div class="children-stack">
            <AttributeRenderer
              v-for="child in field.children"
              :key="child.id"
              :field="child"
              :model-value="internalObject[child.name || child.id.split('.').pop()]"
              @update:model-value="(val) => updateObject(child, val)"
            />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
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
const internalList = computed(() => Array.isArray(props.modelValue) ? props.modelValue : (props.modelValue ? [props.modelValue] : [{}]));

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
  margin-bottom: 32px;
}

.columnar-row {
  display: flex;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
  overflow: hidden;
  margin-bottom: 16px;
}

.group-row {
  border-left: 3px solid #409eff;
}

.title-column {
  width: 220px;
  min-width: 220px;
  padding: 24px 32px;
  background-color: #f8f9fb;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-shrink: 0;
  text-align: right;
}

.title-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.main-label {
  font-size: 15px;
  font-weight: 700;
  color: #1f2d3d;
  line-height: 1.4;
  text-align: right;
}

.index-tag {
  display: inline-block;
  font-size: 12px;
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  width: fit-content;
}

.technical-name {
  font-size: 12px;
  color: #909399;
  font-family: inherit;
  word-break: break-all;
}

.field-column {
  flex: 1;
  padding: 32px 40px;
  background-color: #fff;
  min-width: 0;
}

.children-stack {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.column-actions {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.add-row {
  margin-top: -8px;
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

/* Recursive Nesting */
.is-nested .columnar-row {
  border-color: #f0f2f5;
}

.is-nested .title-column {
  width: 180px;
  min-width: 180px;
  background-color: #fafbfc;
}
</style>
