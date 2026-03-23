<template>
  <div>
    <el-alert type="info" :closable="false" class="mb-16px">
      <template #title>
        <span>群组消息映射：配置不同消息分类发送到不同飞书群组的 Webhook。未配置映射的消息将直接通过默认机器人发送。</span>
      </template>
    </el-alert>

    <!-- 群组映射配置 -->
    <el-form-item label="群组映射">
      <div class="w-full">
        <div v-for="(item, index) in groupMappings" :key="index" class="flex items-center gap-8px mb-8px">
          <el-select v-model="item.category" placeholder="消息分类" class="!w-180px" filterable allow-create>
            <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
          <el-input v-model="item.webhook" placeholder="群组 Webhook 地址" class="flex-1" />
          <el-button type="danger" :icon="Delete" circle @click="removeMapping(index)" />
        </div>
        <el-button type="primary" plain @click="addMapping" :icon="Plus" size="small">
          添加映射
        </el-button>
      </div>
    </el-form-item>

    <!-- 签名密钥 (可选) -->
    <el-form-item label="签名密钥">
      <el-input v-model="secret" placeholder="可选，飞书自定义机器人签名校验密钥" />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { Delete, Plus } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue: Record<string, any>
}>()

const emit = defineEmits(['update:modelValue'])

// 预定义消息分类选项
const categoryOptions = [
  { label: 'ORDER_CHANGE (订单变更)', value: 'ORDER_CHANGE' },
  { label: 'BUDGET_ALERT (预算告警)', value: 'BUDGET_ALERT' },
  { label: 'STOCK_WARNING (库存预警)', value: 'STOCK_WARNING' },
  { label: 'REPORT_READY (报表就绪)', value: 'REPORT_READY' },
  { label: 'SYSTEM_ALERT (系统告警)', value: 'SYSTEM_ALERT' }
]

const secret = ref('')
const groupMappings = ref<Array<{ category: string; webhook: string }>>([])

// 仅初始化一次：从 props 读取
const initFromProps = () => {
  const val = props.modelValue
  if (val && Object.keys(val).length > 0) {
    secret.value = val.secret || ''
    const mapping = val.groupMapping || {}
    groupMappings.value = Object.entries(mapping).map(([category, webhook]) => ({
      category,
      webhook: webhook as string
    }))
  }
}

onMounted(() => {
  initFromProps()
})

// 向父组件同步
const emitUpdate = () => {
  const groupMapping: Record<string, string> = {}
  groupMappings.value.forEach(item => {
    if (item.category && item.webhook) {
      groupMapping[item.category] = item.webhook
    }
  })
  emit('update:modelValue', { secret: secret.value, groupMapping })
}

watch(secret, emitUpdate)
watch(groupMappings, emitUpdate, { deep: true })

const addMapping = () => {
  groupMappings.value.push({ category: '', webhook: '' })
}

const removeMapping = (index: number) => {
  groupMappings.value.splice(index, 1)
}
</script>

