<template>
  <div>
    <el-alert type="warning" :closable="false" class="mb-16px">
      <template #title>
        <span>企业微信渠道配置（开发中，敬请期待）</span>
      </template>
    </el-alert>

    <el-form-item label="应用 AgentId">
      <el-input v-model="localConfig.agentId" placeholder="企业微信应用 AgentId" />
    </el-form-item>

    <el-form-item label="接收人">
      <el-input v-model="localConfig.toUser" placeholder="接收成员 ID，多个用 | 分隔" />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  modelValue: Record<string, any>
}>()

const emit = defineEmits(['update:modelValue'])

const localConfig = ref<Record<string, any>>({
  agentId: '',
  toUser: ''
})

watch(() => props.modelValue, (val) => {
  if (val && Object.keys(val).length > 0) {
    localConfig.value = { ...val }
  }
}, { immediate: true })

watch(localConfig, (val) => {
  emit('update:modelValue', { ...val })
}, { deep: true })
</script>
