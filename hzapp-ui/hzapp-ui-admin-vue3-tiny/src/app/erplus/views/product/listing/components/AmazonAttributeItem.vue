<template>
  <div class="amazon-attribute-item">
    <!-- Custom UI Widgets -->
    <template v-if="field.uiWidget === 'bullet_point_editor'">
      <AmazonBulletPointEditor 
        v-model="internalValue" 
        :field="field" 
        @change="emitChange" 
      />
    </template>

    <template v-else-if="field.uiWidget === 'date-picker' || field.type === 'date'">
      <el-date-picker
        v-model="internalValue"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="选择日期"
        style="width: 100%"
        @change="emitChange"
      />
    </template>

    <template v-else-if="field.uiWidget === 'textarea'">
      <el-input
        v-model="internalValue"
        type="textarea"
        :rows="3"
        :placeholder="field.title"
        @change="emitChange"
      />
    </template>

    <!-- Array Type (e.g. multi-select or tags) -->
    <template v-else-if="field.type === 'array' && field.options?.length > 0">
      <el-select
        v-model="internalValue"
        multiple
        collapse-tags
        filterable
        placeholder="请选择区域"
        style="width: 100%"
        @change="emitChange"
      >
        <el-option
          v-for="opt in field.options"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </template>

    <!-- Enum Type (Select) -->
    <template v-else-if="field.type === 'enum' || field.options?.length > 0">
      <el-select
        v-model="internalValue"
        clearable
        filterable
        :placeholder="field.title"
        style="width: 100%"
        @change="emitChange"
      >
        <el-option
          v-for="opt in field.options"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </template>

    <!-- Number Type -->
    <template v-else-if="field.type === 'number' || field.type === 'integer'">
      <el-input-number
        v-model="internalValue"
        :min="field.extra?.minimum"
        :max="field.extra?.maximum"
        style="width: 100%"
        @change="emitChange"
      />
    </template>

    <!-- Boolean Type -->
    <template v-else-if="field.type === 'boolean'">
      <el-switch v-model="internalValue" @change="emitChange" />
    </template>

    <!-- String/Default Type -->
    <template v-else>
      <el-input
        v-model="internalValue"
        :type="field.extra?.multiline ? 'textarea' : 'text'"
        :placeholder="field.title"
        clearable
        @change="emitChange"
      />
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import AmazonBulletPointEditor from './AmazonBulletPointEditor.vue';

const props = defineProps<{
  modelValue: any;
  field: any; // AmzListingFormFieldVO
}>();

const emit = defineEmits(['update:modelValue', 'change']);

/**
 * Amazon Specific Value Handling
 * The SP-API expects values wrapped in an array/object structure: [{ value: '...' }]
 * OR just the value depending on the schema requirement.
 * To keep it lightweight, we handle the wrapping here or at the save step.
 * For now, we bind to the raw value but wrap it into the expected Amazon format 
 * if the backend indicates it's necessary (handled by the value getter/setter).
 */
const internalValue = computed({
  get: () => props.modelValue,
  set: (newVal) => {
    emit('update:modelValue', newVal);
  }
});

const emitChange = () => {
  emit('change', props.field.id, internalValue.value);
};
</script>

<style scoped>
.amazon-attribute-item {
  width: 100%;
}
</style>
