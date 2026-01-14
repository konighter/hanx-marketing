<template>
  <el-dialog v-model="visible" title="选择装箱方案" width="600px" append-to-body>
    <div v-loading="loading">
      <div v-if="packingOptions.length > 0">
        <div v-for="option in packingOptions" :key="option.packingOptionId"
          class="option-card mb-4 p-4 border rounded hover:bg-gray-50 transition-colors"
          :class="{ 'border-blue-500 bg-blue-50': selectedId === option.packingOptionId }">
          <div class="flex justify-between items-center">
            <div>
              <span class="font-bold">方案 ID: {{ option.packingOptionId }}</span>
              <el-tag v-if="selectedId === option.packingOptionId" size="small" class="ml-2">已选择</el-tag>
            </div>
            <el-button type="primary" size="small" @click="handleSelect(option)">选择此方案</el-button>
          </div>
          <div class="mt-2 text-sm text-gray-500">
            <span>预估费用: </span>
            <span class="text-orange-500 font-bold">{{ option.fees || '暂无数据' }}</span>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="text-center py-10 text-gray-400">
        暂无可用方案，任务可能正在生成中，请稍后再试。
      </div>
    </div>
    <template #footer>
      <div class="flex justify-between w-full">
        <el-button type="primary" plain :loading="generating" @click="handleGenerate">
          <Icon icon="ep:plus" class="mr-1" /> 生成新方案
        </el-button>
        <div class="flex gap-2">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" :loading="loading" @click="fetchOptions">刷新列表</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, defineExpose, defineEmits } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'
import { Icon } from '@/components/Icon'

const emit = defineEmits(['select'])

const visible = ref(false)
const loading = ref(false)
const generating = ref(false)
const packingOptions = ref<any[]>([])
const selectedId = ref('')
const currentPlanId = ref('')

/** 打开弹窗并加载方案 */
const open = (planId: string, selectedOptionId?: string) => {
  currentPlanId.value = planId
  selectedId.value = selectedOptionId || ''
  visible.value = true
  fetchOptions()
}

/** 获取方案列表 */
const fetchOptions = async () => {
  if (!currentPlanId.value) return
  loading.value = true
  try {
    const res = await AmzInboundApi.listPackingOptions(currentPlanId.value)
    packingOptions.value = res || []
  } catch (error) {
    ElMessage.error('获取装箱方案失败')
  } finally {
    loading.value = false
  }
}

/** 生成新方案 */
const handleGenerate = async () => {
  if (!currentPlanId.value) return
  generating.value = true
  try {
    const res = await AmzInboundApi.generatePackingOptions({ planId: currentPlanId.value })
    if (res && res.length > 0) {
      ElMessage.success('方案生成中，请稍后刷新')
      await fetchOptions()
    } else {
      ElMessage.warning('方案正在生成中，请稍后刷新列表')
    }
  } catch (error) {
    ElMessage.error('请求生成方案失败')
  } finally {
    generating.value = false
  }
}

/** 选择方案 */
const handleSelect = (option: any) => {
  selectedId.value = option.packingOptionId
  emit('select', option)
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.option-card {
  cursor: pointer;
}
</style>
