<template>
  <el-dialog v-model="visible" title="选择分仓方案" width="800px" append-to-body>
    <div v-loading="loading">
      <div v-if="placementOptions.length > 0">
        <div v-for="option in placementOptions" :key="option.placementOptionId"
          class="option-card mb-4 p-4 border rounded hover:bg-gray-50 transition-colors"
          :class="{ 'border-blue-500 bg-blue-50': selectedId === option.placementOptionId }">
          <div class="flex justify-between items-center mb-3">
            <div>
              <span class="font-bold">方案 ID: {{ option.placementOptionId }}</span>
              <el-tag v-if="selectedId === option.placementOptionId" size="small" class="ml-2">已选择</el-tag>
            </div>
            <el-button type="primary" size="small" @click="handleSelect(option)">选择此方案</el-button>
          </div>

          <div class="mb-3 text-sm">
            <span class="text-gray-600">预估费用: </span>
            <span class="text-orange-500 font-bold">
              {{ option.placeFee ? `${option.placeFee.currency} ${option.placeFee.amount}` : '暂无数据' }}
            </span>
          </div>

          <!-- 货件详情 -->
          <div v-if="option.shipmentDetails && option.shipmentDetails.length > 0" class="mt-3">
            <div class="text-sm font-bold text-gray-600 mb-2">
              <Icon icon="ep:box" class="mr-1" />
              货件详情 ({{ option.shipmentDetails.length }} 个货件)
            </div>
            <div class="space-y-2">
              <div v-for="(shipment, index) in option.shipmentDetails" :key="shipment.shipmentId"
                class="bg-white p-3 rounded border border-gray-200">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-xs font-bold text-gray-700">
                    <Icon icon="ep:location" class="mr-1" />
                    货件 #{{ index + 1 }}: {{ shipment.shipmentId }}
                  </span>
                  <el-tag size="small" type="info">
                    {{ shipment.shipmentItems?.length || 0 }} 种商品
                  </el-tag>
                </div>

                <div v-if="shipment.destination" class="text-xs text-gray-500">
                  <div class="flex items-start gap-1">
                    <span class="font-semibold">目的地:</span>
                    <span>{{ shipment.destination.warehouseId }}</span>
                  </div>
                  <div v-if="shipment.destination.address" class="mt-1 pl-4 text-gray-400">
                    {{ shipment.destination.address.name }} -
                    {{ shipment.destination.address.city }},
                    {{ shipment.destination.address.countryCode }}
                  </div>
                </div>

                <!-- 商品列表折叠 -->
                <el-collapse class="mt-2" accordion>
                  <el-collapse-item title="查看商品明细" name="items">
                    <el-table :data="shipment.shipmentItems" size="small" max-height="200" border>
                      <el-table-column label="MSKU" prop="msku" width="120" />
                      <el-table-column label="FNSKU" prop="fnsku" width="120" />
                      <el-table-column label="ASIN" prop="asin" width="100" />
                      <el-table-column label="数量" prop="quantity" width="80" align="center" />
                    </el-table>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="text-center py-10 text-gray-400">
        <Icon icon="ep:warning" :size="48" class="mb-2" />
        <p>暂无可用方案，任务可能正在生成中，请稍后再试。</p>
      </div>
    </div>
    <template #footer>
      <div class="flex justify-between w-full">
        <el-button type="primary" plain :loading="generating" @click="handleGenerate">
          <Icon icon="ep:refresh" class="mr-1" /> 生成新方案
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
const placementOptions = ref<any[]>([])
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
    const res = await AmzInboundApi.listPlacementOptions({ planId: currentPlanId.value })
    placementOptions.value = res || []
  } catch (error) {
    ElMessage.error('获取分仓方案失败')
  } finally {
    loading.value = false
  }
}

/** 生成新方案 */
const handleGenerate = async () => {
  if (!currentPlanId.value) return
  generating.value = true
  try {
    const res = await AmzInboundApi.generatePlacementOptions({ planId: currentPlanId.value })
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
  selectedId.value = option.placementOptionId
  emit('select', option)
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.option-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.option-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-collapse-item__header) {
  font-size: 12px;
  padding: 8px 0;
}

:deep(.el-collapse-item__content) {
  padding: 8px 0;
}
</style>
