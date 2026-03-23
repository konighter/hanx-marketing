<template>
  <div>
    <el-alert type="warning" :closable="false" class="mb-16px">
      <template #title>
        <span>钉钉渠道配置（开发中，敬请期待）</span>
      </template>
    </el-alert>

    <el-form-item label="签名密钥">
      <el-input v-model="localConfig.secret" placeholder="钉钉自定义机器人 Secret" />
    </el-form-item>

    <el-form-item label="关键词">
      <el-input v-model="localConfig.keyword" placeholder="自定义关键词（安全设置）" />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  modelValue: Record<string, any>
}>()

const emit = defineEmits(['update:modelValue'])

const localConfig = ref<Record<string, any>>({
  secret: '',
  keyword: ''
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
