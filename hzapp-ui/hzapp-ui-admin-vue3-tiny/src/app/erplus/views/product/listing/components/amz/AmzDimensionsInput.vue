<template>
  <div class="amz-dimensions-input" v-if="field">

    <div class="dimension-group">
      <!-- 三个/多个数值输入框并排 -->
      <div v-for="(key, index) in valueKeys" :key="key" class="dimension-item">
        <el-input-number
          v-model="values[key]"
          :precision="2"
          :step="0.1"
          :min="0"
          :controls="false"
          :placeholder="getPlaceholder(key)"
          class="val-input"
          :class="{ 'is-first': index === 0, 'is-last': index === valueKeys.length - 1, 'is-middle': index > 0 && index < valueKeys.length - 1 }"
          @change="updateModel"
        />
      </div>
    </div>
    <!-- 共享单位选择器 -->
    <el-select
      v-model="sharedUnit"
      placeholder="单位"
      class="unit-selector"
      @change="updateModel"
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

// defineOptions({ name: 'AmzDimensionsInput' });

interface Field {
  id: string;
  children?: any[];
  title?: string;
}

const props = defineProps<{
  modelValue: any;
  field: Field;
}>();

const emit = defineEmits(['update:modelValue']);

console.log(`[AmzDimensionsInput] Init for ${props.field?.id}`, props.field);

// 内部状态
const values = ref<Record<string, number | undefined>>({});
const sharedUnit = ref<string>('');

// 稳健的属性名提取：优先使用 name，否则从 ID 后缀提取
const getPropName = (c: any) => {
  if (!c) return '';
  if (c.name) return c.name;
  if (!c.id) return '';
  const parts = c.id.split('.');
  return parts[parts.length - 1];
};

// 识别哪些是数值维度（长宽高/直径等）
const valueKeys = computed(() => {
  if (!props.field?.children) return [];
  const allowed = ['length', 'width', 'height', 'depth', 'diameter'];
  return props.field.children
    .filter(c => allowed.includes(getPropName(c)))
    .map(c => getPropName(c));
});

// 从子字段中提取单位选项（通常所有子字段共用一套单位枚举）
const unitOptions = computed(() => {
  if (!props.field?.children) return [];
  const firstDim = props.field.children.find(c => valueKeys.value.includes(getPropName(c)));
  if (!firstDim?.children) return [];
  const unitField = firstDim.children.find(c => getPropName(c) === 'unit');
  return unitField?.options || [];
});

const getPlaceholder = (key: string) => {
  const map: Record<string, string> = {
    length: '长', width: '宽', height: '高', depth: '深', diameter: '直径'
  };
  return map[key] || key;
};

// 从模型数据同步到内部状态
const syncFromModel = () => {
  let model = props.modelValue;
  if (Array.isArray(model)) {
    model = model[0] || {};
  } else {
    model = model || {};
  }
  
  valueKeys.value.forEach(key => {
    const val = model[key]?.value;
    if (values.value[key] !== val) values.value[key] = val;
  });
  
  const firstKey = valueKeys.value[0];
  if (firstKey && model[firstKey]?.unit) {
    if (sharedUnit.value !== model[firstKey].unit) sharedUnit.value = model[firstKey].unit;
  }
};

onMounted(syncFromModel);
watch(() => props.modelValue, syncFromModel, { deep: true });

const updateModel = () => {
  const isArray = Array.isArray(props.modelValue);
  let baseObj = isArray ? (props.modelValue[0] || {}) : (props.modelValue || {});
  
  const newObj = { ...baseObj };
  valueKeys.value.forEach(key => {
    newObj[key] = {
      value: values.value[key] || 0,
      unit: sharedUnit.value
    };
  });
  
  emit('update:modelValue', isArray ? [newObj] : newObj);
};
</script>

<style scoped>
.amz-dimensions-input {
  display: flex;
  gap: 8px;
  width: 100%;
  align-items: flex-start;
  flex-wrap: wrap;
}
.dimension-group {
  display: flex;
  flex: 3;
}
.dimension-item {
  flex: 1;
  min-width: 100px;
}
.dimension-item + .dimension-item {
  margin-left: -1px;
}
.dimension-item :deep(.el-input__wrapper) {
  border-radius: 0;
  box-shadow: 0 0 0 1px var(--el-border-color) inset !important;
  z-index: 1;
}
.dimension-item :deep(.el-input__wrapper.is-focus) {
  z-index: 2;
}
.dimension-item:first-child :deep(.el-input__wrapper) {
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}
.dimension-item:last-child :deep(.el-input__wrapper) {
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}

.unit-selector {
  flex: 1;
  min-width: 90px;
}
</style>
