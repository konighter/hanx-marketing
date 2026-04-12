<template>
  <div class="amz-array-editor" :class="{ 'is-nested': isNested }">
    <el-form-item
      :required="field.required"
      class="array-form-item"
    >
      <!-- Custom Label for dual-language support -->
      <template #label v-if="field.title">
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

      <div 
        v-for="(item, index) in internalList" 
        :key="index" 
        class="array-row"
      >
        <el-input
          v-model="internalList[index]"
          placeholder="Please enter"
          @input="handleInput"
          clearable
          class="row-input"
        >
          <template #append>
            <el-button :icon="Delete" @click="removeItem(index)" class="delete-btn" />
          </template>
        </el-input>
      </div>
      
      <div class="action-row">
        <el-button @click="addItem" class="add-btn">
          <el-icon><Plus /></el-icon>
          <span>添加</span>
        </el-button>
      </div>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { InfoFilled, Plus, Delete } from '@element-plus/icons-vue';

interface Field {
  id: string;
  title: string;
  required?: boolean;
  description?: string;
}

const props = defineProps<{
  field: Field;
  modelValue: any;
  isNested?: boolean;
}>();

const emit = defineEmits(['update:modelValue']);

const internalList = computed(() => {
  if (Array.isArray(props.modelValue)) {
    return props.modelValue;
  }
  return [];
});

// Format label: Split "English (Chinese)" or "Chinese (English)" into two lines
const formattedLabel = computed(() => {
  const title = props.field.title || '';
  const match = title.match(/^(.+?)\s*[\(（](.+?)[\)）]$/);
  if (match) {
    return {
      primary: match[1].trim(),
      secondary: match[2].trim()
    };
  }
  return { primary: title, secondary: '' };
});

const handleInput = () => {
  emit('update:modelValue', [...internalList.value]);
};

const addItem = () => {
  const newList = [...internalList.value, ''];
  emit('update:modelValue', newList);
};

const removeItem = (index: number) => {
  const newList = [...internalList.value];
  newList.splice(index, 1);
  emit('update:modelValue', newList);
};
</script>

<style scoped>
.amz-array-editor {
  margin-bottom: 2px;
}

.array-form-item {
  margin-bottom: 24px !important;
}

.array-row {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.row-input {
  flex: 1;
}

.delete-btn {
  color: #f56c6c;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.delete-btn:hover {
  opacity: 1;
  color: #f56c6c;
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

/* Custom Label Styles matching AttributeRenderer */
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
}

:deep(.el-form-item__label) {
  display: flex !important;
  justify-content: flex-end !important;
  align-items: flex-start !important;
  padding-top: 5px !important;
}

.array-form-item :deep(.el-form-item__content) {
  display: block;
}

.action-row {
  display: flex;
  align-items: flex-start;
  margin-top: 4px;
}
</style>


