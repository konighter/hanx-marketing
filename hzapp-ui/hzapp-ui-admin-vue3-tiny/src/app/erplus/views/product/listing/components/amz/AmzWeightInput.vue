<template>
  <div class="amz-weight-input" v-if="field">
    <el-input-number
      v-model="internalValue"
      :precision="2"
      :step="0.1"
      :min="0"
      :controls="false"
      placeholder="数值"
      class="value-input"
    />
    <el-select
      v-model="internalUnit"
      placeholder="单位"
      class="unit-select"
      filterable
    >
      <el-option
        v-for="opt in unitOptions"
        :key="opt.value"
        :label="opt.label"
        :value="opt.value"
      />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';

// 显式导出组件名 (兼容性写法)
// defineOptions({ name: 'AmzWeightInput' });

interface Field {
  id: string;
  children?: any[];
  options?: any[];
}

const props = defineProps<{
  modelValue: any;
  field: Field;
}>();

const emit = defineEmits(['update:modelValue']);

console.log(`[AmzWeightInput] Init for ${props.field?.id}`, props.field);

const value = ref<number | undefined>();
const unit = ref<string>('');

// 稳健的属性名提取逻辑：优先使用 name，否则从 ID 后缀提取
const getPropName = (c: any) => {
  if (!c) return '';
  if (c.name) return c.name;
  if (!c.id) return '';
  const parts = c.id.split('.');
  return parts[parts.length - 1];
};

// 提取单位选项
const unitOptions = computed(() => {
  if (!props.field?.children) return [];
  const unitField = props.field.children.find(c => getPropName(c) === 'unit');
  return unitField?.options || [];
});

// 同步模型数据到内部状态
const syncFromModel = () => {
  let model = props.modelValue;
  // 处理数组包装 (maxItems: 1)
  if (Array.isArray(model)) {
    model = model[0] || {};
  } else {
    model = model || {};
  }
  
  // 仅在值确实变化时更新，防止循环触发
  if (value.value !== model.value) value.value = model.value;
  if (unit.value !== model.unit) unit.value = model.unit;
};

onMounted(syncFromModel);
watch(() => props.modelValue, syncFromModel, { deep: true });

const updateModel = () => {
  const isArray = Array.isArray(props.modelValue);
  let baseObj = isArray ? (props.modelValue[0] || {}) : (props.modelValue || {});
  
  const newObj = {
    ...baseObj,
    value: value.value,
    unit: unit.value
  };
  
  emit('update:modelValue', isArray ? [newObj] : newObj);
};

const internalValue = computed({
  get: () => value.value,
  set: (val) => {
    value.value = val;
    updateModel();
  }
});

const internalUnit = computed({
  get: () => unit.value,
  set: (val) => {
    unit.value = val;
    updateModel();
  }
});
</script>

<style scoped>
.amz-weight-input {
  display: flex;
  gap: 4px;
  width: 100%;
}

.value-input {
  flex: 2;
}

.unit-select {
  flex: 1;
  min-width: 100px;
}

:deep(.el-input-number.is-controls-right .el-input__wrapper) {
  padding-left: 8px;
  padding-right: 32px;
}
</style>
