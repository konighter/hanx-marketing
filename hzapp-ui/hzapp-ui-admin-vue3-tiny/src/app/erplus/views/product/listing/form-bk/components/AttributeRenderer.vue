<template>
  <component :is="ComponentMap[schema.type]" v-model="modelValue" v-bind="schema.props" class="w-100!">
    <template v-if="schema.type === 'SELECT'" #default>
      <el-option v-for="opt in schema.options" :key="opt.value" :label="opt.valueName" :value="opt.value" />
    </template>
  </component>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ComponentMap } from '../registry'
import type { AttributeSchema } from '../adapter'

const props = defineProps<{
  schema: AttributeSchema
  modelValue: any
}>()

const emit = defineEmits(['update:modelValue'])

const modelValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
</script>
