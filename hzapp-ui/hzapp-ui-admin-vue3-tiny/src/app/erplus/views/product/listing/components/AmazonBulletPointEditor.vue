<template>
  <div class="amazon-bullet-point-editor">
    <div v-for="(item, index) in list" :key="index" class="editor-row">
      <el-input
        v-model="list[index]"
        :placeholder="`五点描述 ${index + 1}`"
        clearable
        @change="handleSync"
      >
        <template #prepend>{{ index + 1 }}</template>
      </el-input>
      <el-button 
        type="danger" 
        plain
        size="small" 
        class="ml-2"
        @click="removeItem(index)"
      >删除</el-button>
    </div>
    <el-button 
      class="mt-2"
      type="primary" 
      plain 
      style="width: 100%" 
      @click="addItem"
      :disabled="list.length >= 5"
    >+ 添加描述项 (最多5项)</el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';

const props = defineProps<{
  modelValue: any;
  field: any;
}>();

const emit = defineEmits(['update:modelValue', 'change']);

const list = ref<string[]>([]);

const initList = () => {
    if (Array.isArray(props.modelValue)) {
      list.value = props.modelValue.map(v => typeof v === 'string' ? v : v?.value || '').filter(Boolean);
    } else if (typeof props.modelValue === 'string' && props.modelValue) {
      list.value = [props.modelValue];
    } else {
      list.value = [];
    }
    // ensure at least 1 row
    if (list.value.length === 0) list.value.push('');
};

initList();

const handleSync = () => {
   const validItems = list.value.filter(s => s.trim().length > 0);
   
   // 尝试从现有数据中获取元数据 (如 language_tag, marketplace_id)
   const meta = (Array.isArray(props.modelValue) && props.modelValue.length > 0) 
      ? { ...props.modelValue[0] } 
      : {};
   delete (meta as any).value; // 移除旧的 value
   
   const payload = validItems.map(s => ({
       ...meta,
       value: s.trim()
   }));
   
   emit('update:modelValue', payload);
   emit('change', props.field.id, payload);
};

const addItem = () => {
    if (list.value.length < 5) list.value.push('');
};

const removeItem = (idx: number) => {
    if (list.value.length > 1) {
        list.value.splice(idx, 1);
        handleSync();
    } else {
        list.value[0] = '';
        handleSync();
    }
};

watch(() => props.modelValue, (newVal) => {
    if (newVal === undefined) return;
    const currentLen = list.value.filter(s => s.trim().length > 0).length;
    const newLen = Array.isArray(newVal) ? newVal.length : (newVal ? 1 : 0);
    if (currentLen !== newLen) {
        initList();
    }
}, { deep: true });

</script>

<style scoped>
.amazon-bullet-point-editor {
    width: 100%;
}
.editor-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
}
.ml-2 {
    margin-left: 8px;
}
.mt-2 {
    margin-top: 8px;
}
</style>
