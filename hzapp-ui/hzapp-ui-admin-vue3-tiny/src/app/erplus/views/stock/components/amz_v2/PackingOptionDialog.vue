<template>
  <el-dialog v-model="visible" title="选择装箱方案" width="650px" append-to-body>
    <div v-loading="loading" class="max-h-[60vh] overflow-y-auto pr-2">
      <div v-if="packingOptions.length > 0">
        <div
v-for="option in packingOptions" :key="option.packingOptionId"
          class="option-card mb-4 p-4 border rounded hover:bg-gray-50 transition-colors"
          :class="{ 'border-blue-500 bg-blue-50': selectedId === option.packingOptionId }">
          <div class="flex justify-between items-center">
            <div>
              <span class="font-bold">方案 ID: {{ option.packingOptionId }}</span>
              <el-tag v-if="selectedId === option.packingOptionId" size="small" class="ml-2">已选择</el-tag>
            </div>
            <el-button type="primary" size="small" @click="handleSelect(option)">选择此方案</el-button>
          </div>
          <div class="mt-2 text-sm text-gray-500 flex justify-between">
            <span>预估费用: <span class="text-orange-500 font-bold">{{
              option.fees && option.fees.length > 0 ? option.fees[0].totalFee : '暂无数据'
                }}</span></span>
            <span class="text-gray-400">分组数量: {{ Object.keys(option.groupItems || {}).length }}</span>
          </div>

          <!-- 分组详情展示 -->
          <div v-if="option.groupItems" class="mt-3">
            <el-collapse accordion>
              <div
v-for="(group, key) in option.groupItems" :key="key"
                class="mb-2 bg-gray-50 rounded border border-dashed border-gray-200 overflow-hidden">
                <el-collapse-item :name="key">
                  <template #title>
                    <div class="flex justify-between items-center w-full px-2 text-xs font-bold text-gray-600">
                      <span class="flex items-center gap-1">
                        <Icon icon="ep:box" :size="12" />
                        组 #{{ key }}
                      </span>
                      <span class="mr-4 text-gray-400 font-normal">
                        {{ group[0]?.boxLength }}x{{ group[0]?.boxWidth }}x{{ group[0]?.boxHeight }}cm | {{
                          group[0]?.boxWeight }}kg | {{ group.length }} SKU
                      </span>
                    </div>
                  </template>
                  <div class="px-2 pb-2">
                    <el-table :data="group" size="small" border :header-cell-style="{ background: '#f5f7fa' }">
                      <el-table-column label="图片" width="60" align="center">
                        <template #default="{ row }">
                          <el-image :src="row.image" class="w-8 h-8 rounded" fit="cover" />
                        </template>
                      </el-table-column>
                      <el-table-column label="SKU" min-width="120">
                        <template #default="{ row }">
                          <div class="flex flex-col">
                            <span class="truncate text-xs text-gray-700 font-medium" :title="row.msku">{{ row.msku
                              }}</span>
                            <span v-if="row.asin" class="text-[10px] text-gray-400">ASIN: {{ row.asin }}</span>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column label="预处理" min-width="120">
                        <template #default="{ row }">
                          <div
v-if="row.prepInstructions && row.prepInstructions.length > 0"
                            class="flex flex-wrap gap-1">
                            <el-tooltip
v-for="(prep, index) in row.prepInstructions" :key="index" placement="top"
                              effect="light">
                              <template #content>
                                <div class="text-xs">
                                  费用: <span class="text-orange-500 font-bold">{{ prep.fee?.amount }} {{
                                    prep.fee?.currency }}</span>
                                </div>
                              </template>
                              <el-tag size="small" type="warning" effect="plain" class="!text-[10px] !h-4 !px-1">
                                {{ prep.prepType }}
                              </el-tag>
                            </el-tooltip>
                          </div>
                          <span v-else class="text-gray-300 text-[10px]">无</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="数量" width="70" align="center">
                        <template #default="{ row }">
                          <span>{{ row.quantity }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </el-collapse-item>
              </div>
            </el-collapse>
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
