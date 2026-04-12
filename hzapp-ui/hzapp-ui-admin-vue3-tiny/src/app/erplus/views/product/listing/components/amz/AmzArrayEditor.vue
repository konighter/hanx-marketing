<template>
  <div class="amz-array-editor">
    <el-form-item
      :label="field.title"
      :required="field.required"
      class="array-form-item"
    >
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
            <el-button icon="Delete" @click="removeItem(index)" />
          </template>
        </el-input>
      </div>
      
      <el-button 
        type="primary" 
        link 
        icon="Plus" 
        @click="addItem"
        class="add-btn"
      >
        Add {{ field.title }}
      </el-button>
      
      <div v-if="field.description" class="field-desc">
        {{ field.description }}
      </div>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Field {
  id: string;
  title: string;
  required?: boolean;
  description?: string;
}

const props = defineProps<{
  field: Field;
  modelValue: any;
}>();

const emit = defineEmits(['update:modelValue']);

const internalList = computed(() => {
  if (Array.isArray(props.modelValue)) {
    return props.modelValue;
  }
  return [];
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
  margin-bottom: 12px;
}
.array-row {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}
.row-input {
  flex: 1;
}
.add-btn {
  padding: 0;
  margin-top: 4px;
}
.field-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}
.array-form-item :deep(.el-form-item__content) {
  display: block;
}
</style>
