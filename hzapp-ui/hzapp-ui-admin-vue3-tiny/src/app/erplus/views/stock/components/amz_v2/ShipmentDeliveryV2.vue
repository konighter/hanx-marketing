<template>
  <div class="shipment-delivery-v2" v-loading="loading">
    <div v-if="hasShipments" class="space-y-6">
      <el-alert title="请核对货件及箱子详情，并为自送货件填写追踪号" type="info" :closable="false" class="mb-4" />

      <div v-for="shipment in confirmedShipments" :key="shipment.shipmentId"
        class="shipment-card bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="shipment-header bg-gray-50 p-4 border-b flex justify-between items-center">
          <div class="flex items-center gap-4">
            <span class="font-bold text-gray-700">货件 ID: {{ shipment.shipmentId }}</span>
            <el-tag type="info">{{ shipment.destination?.warehouseId || '未知' }}</el-tag>
            <el-tag size="small" type="success" effect="plain">
              {{ SHIPPING_SOLUTIONS_MAP[shipment.shippingSolution] || shipment.shippingSolution }}
            </el-tag>
          </div>
          <div class="flex items-center gap-2">
            <el-button v-if="isOwnCarrier(shipment)" type="primary" size="small"
              @click="handleBatchSaveTracking(shipment)" :loading="savingShipmentId === shipment.shipmentId">
              保存追踪号
            </el-button>
          </div>
        </div>

        <div class="p-4 bg-white">
          <el-table :data="shipment.boxes" border size="small" stripe>
            <el-table-column label="箱号" min-width="120">
              <template #default="{ row }">
                <div class="font-bold text-gray-700 leading-tight">{{ row.templateName }}</div>
                <div class="text-[10px] text-gray-400 font-mono">{{ row.boxId }}</div>
              </template>
            </el-table-column>
            <el-table-column label="尺寸(长*宽*高)" width="180" align="center">
              <template #default="{ row }">
                {{ row.dimensions?.length }} x {{ row.dimensions?.width }} x {{ row.dimensions?.height }}
                <span class="text-gray-400 ml-1">{{ row.dimensions?.unitOfMeasurement }}</span>
              </template>
            </el-table-column>
            <el-table-column label="重量" width="100" align="center">
              <template #default="{ row }">
                {{ row.weight?.value }} <span class="text-gray-400">{{ row.weight?.unit }}</span>
              </template>
            </el-table-column>
            <el-table-column label="追踪号" min-width="200">
              <template #default="{ row }">
                <el-input v-if="isOwnCarrier(shipment)" v-model="row.trackingId" placeholder="输入追踪号" size="small"
                  :disabled="readonly" />
                <span v-else class="text-gray-400 text-xs">亚马逊合作承运商自动生成</span>
              </template>
            </el-table-column>
            <el-table-column label="内容摘要" min-width="200">
              <template #default="{ row }">
                <div class="flex flex-wrap gap-1">
                  <el-tag v-for="item in row.items" :key="item.msku" size="small" type="info" effect="plain"
                    class="!px-1">
                    {{ item.msku }} x <span class="font-bold">{{ item.quantity }}</span>
                  </el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- Footer Actions -->
      <div class="flex justify-center gap-4 mt-8 pb-10">
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="p-20 text-center bg-white rounded-lg border-2 border-dashed">
      <Icon icon="ep:truck" :size="64" class="text-gray-200 mb-4" />
      <h3 class="text-gray-500 font-bold">尚未加载可发货信息</h3>
      <p class="text-gray-400 text-sm mt-2">请先完成分仓和贴标配置</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Icon } from '@/components/Icon'
import { SHIPPING_SOLUTIONS_MAP } from './constants'

const props = defineProps({
  shipmentPlan: {
    type: Object,
    required: true
  },
  active: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

const planId = computed(() => props.shipmentPlan?.extralId || '')
const emit = defineEmits(['back', 'next'])

const loading = ref(false)
const savingShipmentId = ref('')
const confirmedShipments = ref<any[]>([])

const hasShipments = computed(() => confirmedShipments.value && confirmedShipments.value.length > 0)

/** 是否为自送货件 */
const isOwnCarrier = (shipment: any) => {
  return shipment.shippingSolution === 'USE_YOUR_OWN_CARRIER'
}

/** 批量保存追踪号 */
const handleBatchSaveTracking = async (shipment: any) => {
  if (!planId.value || !shipment.shipmentId) return

  savingShipmentId.value = shipment.shipmentId
  try {
    const trackingDetails = shipment.boxes.map((box: any) => ({
      packageId: box.boxId || box.packageId,
      trackingId: box.trackingId
    })).filter((item: any) => item.trackingId)

    if (trackingDetails.length === 0) {
      ElMessage.warning('请至少输入一个追踪号')
      return
    }

    await AmzInboundApi.saveTrackingDetails({
      planId: planId.value,
      shipmentId: shipment.shipmentId,
      trackingDetails
    })
    ElMessage.success('追踪号保存成功')
  } catch (error) {
    ElMessage.error('保存追踪号失败')
  } finally {
    savingShipmentId.value = ''
  }
}


const init = async () => {
  if (!planId.value) return
  loading.value = true
  try {
    // 借用 getLabels 获取货件和箱子列表，这可能包含已有的 trackingId
    const res = await AmzInboundApi.getLabels({
      planId: planId.value
    })

    if (res && Array.isArray(res)) {
      confirmedShipments.value = res
    } else if (res && res.shipments) {
      confirmedShipments.value = res.shipments
    }
  } catch (error) {
    console.error('Failed to init delivery page', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.active) init()
})

watch(() => props.active, (val) => {
  if (val) init()
})
</script>

<style scoped>
.shipment-card {
  transition: all 0.3s ease;
}

.shipment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.shipment-header {
  background: linear-gradient(to right, #f8fafc, #ffffff);
}
</style>
