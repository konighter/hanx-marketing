<template>
  <div class="shipment-placement-v2">
    <!-- Header: Action Bar -->
    <div class="flex justify-between items-center mb-6 bg-white rounded shadow-sm">
      <div class="flex items-center gap-2">
        <div v-if="placementOptionConfirmed && hasShipments">
          <el-form inline>
            <el-form-item v-if="selectedPlacementOptionId" class="mb-0 mr-4">
              <div class="flex items-center gap-4 text-sm bg-gray-50 px-3 py-1 rounded border border-gray-200">
                <div class="flex items-center gap-2">
                  <span class="text-gray-500">方案 ID:</span>
                  <span class="font-mono font-bold text-blue-600">{{ selectedPlacementOptionId }}</span>
                </div>
                <div class="w-px h-4 bg-gray-300"></div>
                <div class="flex items-center gap-2" v-if="selectedPlacementOption?.placeFee">
                  <span class="text-gray-500">预估费用:</span>
                  <span class="font-bold text-red-500">
                    {{ selectedPlacementOption.placeFee.currency }} {{ selectedPlacementOption.placeFee.amount }}
                  </span>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="计划发货时间" class="mb-0">
              <el-date-picker
v-model="shipmentDate" type="datetime" placeholder="选择发货时间" class="w-full"
                format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD" :disabled-date="disabledDate"
                :disabled="readonly" />
            </el-form-item>
            <el-form-item class="mb-0" v-if="!readonly">
              <el-button type="primary" @click="handleTransportGenerate" plain :loading="generating">
                <Icon icon="ep:list" class="mr-1" />获取物流配置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

      </div>
      <div class="flex gap-2 items-center">
        <el-form-item v-if="!readonly">
          <el-button type="primary" plain @click="openPlacementDialog">
            <Icon icon="ep:list" class="mr-1" /> 选择/更换分仓方案
          </el-button>
        </el-form-item>

        <!-- <el-button type="primary" :loading="generating" @click="handleGenerate">
          <Icon icon="ep:refresh" class="mr-1" /> 生成新方案
        </el-button> -->
      </div>
    </div>

    <!-- Main Content -->
    <div v-loading="loading">
      <!-- 已确认方案：显示货件和运输选项 -->
      <div v-if="placementOptionConfirmed && hasShipments" class="space-y-6">
        <el-alert title="请为每个货件选择承运商和运输方式" type="info" :closable="false" class="mb-4" />

        <div
v-for="shipment in confirmedShipments" :key="shipment.shipmentId"
          class="shipment-card bg-white rounded-lg shadow-sm border-l-4 border-blue-500 overflow-hidden">
          <div class="shipment-header bg-gray-50 p-4 border-b">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-4">
                <span class="font-bold text-gray-700">货件 ID: {{ shipment.shipmentId }}</span>
                <el-tag v-if="shipment.confirmed" type="success">运输已确认</el-tag>
                <el-tag v-else type="warning">待确认</el-tag>
              </div>
            </div>
            <div class="mt-2 text-sm text-gray-600">
              <div class="flex items-center gap-2">
                <Icon icon="ep:location" />
                <span>目的地: {{ shipment.destination?.warehouseId || '未知' }}</span>
              </div>
              <div v-if="shipment.destination?.address" class="mt-1 text-xs text-gray-500">
                {{ shipment.destination.address.name }} -
                {{ shipment.destination.address.addressLine1 }},
                {{ shipment.destination.address.city }},
                {{ shipment.destination.address.postalCode }},
                {{ shipment.destination.address.countryCode }}
              </div>
            </div>
          </div>

          <div class="p-4">
            <!-- 运输方式配置 -->
            <!-- 运输方式配置 -->
            <el-form
ref="shipmentForms" :model="shipment" :rules="rules" label-width="100px"
              v-if="Object.keys(transportationData).length > 0">
              <div class="grid grid-cols-3 gap-4 mb-0">
                <el-form-item label="运输方式" class="mb-0">
                  <el-select
v-model="shipment.shippingMode" placeholder="请选择运输方式" class="w-full" clearable
                    :disabled="readonly">
                    <el-option
v-for="mode in SHIPPING_MODES" :key="mode.value" :label="mode.label"
                      :value="mode.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="承运商" class="mb-0">
                  <el-select
v-model="shipment.shippingSolution" placeholder="请选择承运商类型" class="w-full" clearable
                    :disabled="readonly">
                    <el-option
v-for="sol in SHIPPING_SOLUTIONS" :key="sol.value" :label="sol.label"
                      :value="sol.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="送货窗口" class="mb-0" prop="deliveryWindowOptionId">
                  <el-select
v-model="shipment.deliveryWindowOptionId" placeholder="请选择送货窗口" class="w-full" clearable
                    :disabled="readonly">
                    <el-option
v-for="window in getDeliveryWindows(shipment)" :key="window.value" :label="window.label"
                      :value="window.value" class="!h-auto !py-2">
                      <div class="flex justify-between items-center w-full">
                        <span>{{ window.label }}</span>
                        <el-tag
v-if="window.availabilityType" size="small"
                          :type="window.availabilityType === 'AVAILABLE' ? 'success' : 'warning'" effect="plain">
                          {{ AVAILABILITY_TYPES_MAP[window.availabilityType] || window.availabilityType }}
                        </el-tag>
                      </div>
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>

              <el-form-item label="运输渠道" prop="transportationOptionId">
                <el-select
v-model="shipment.transportationOptionId" placeholder="选择运输选项" class="w-full"
                  @change="onTransportChange(shipment)" clearable :disabled="readonly">
                  <el-option
v-for="option in getFilteredTransportOptions(shipment)" :key="option.value"
                    :label="option.label" :value="option.value" class="!h-auto !py-2">
                    <div class="flex flex-col gap-1">
                      <div class="flex justify-between items-center">
                        <span class="font-bold text-gray-700">
                          {{ option.carrierName }}
                          <span v-if="option.carrierAlphaCode" class="text-xs text-gray-400 font-normal">({{
                            option.carrierAlphaCode }})</span>
                        </span>
                        <span class="text-red-500 font-bold">
                          {{ option.currency }} {{ option.amount }}
                        </span>
                      </div>
                      <div class="flex justify-between items-center text-xs text-gray-500">
                        <span>{{ SHIPPING_MODES_MAP[option.shippingMode] || option.shippingMode }}</span>
                        <el-tag size="small" type="info" effect="plain" class="scale-90 origin-right">
                          {{ SHIPPING_SOLUTIONS_MAP[option.shippingSolution] || option.shippingSolution }}
                        </el-tag>
                      </div>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>

            </el-form>

            <!-- 商品列表 -->
            <div class="mb-4">
              <el-collapse>
                <el-collapse-item name="items">
                  <template #title>
                    <span class="text-sm font-bold text-gray-600">货件商品 ({{ shipment.shipmentItems?.length || 0
                      }}种)</span>
                  </template>
                  <el-table :data="shipment.shipmentItems" border stripe size="small" max-height="300">
                    <el-table-column label="MSKU" prop="msku" min-width="150" />
                    <el-table-column label="FNSKU" prop="fnsku" min-width="150" />
                    <el-table-column label="ASIN" prop="asin" width="120" />
                    <el-table-column label="数量" prop="quantity" width="100" align="center" />
                  </el-table>
                </el-collapse-item>
              </el-collapse>
            </div>

            <!-- 运输选项 -->


          </div>
        </div>

        <!-- Footer Actions -->
        <div class="flex justify-center gap-4 mt-8 pb-10">
          <el-button
type="primary" :disabled="!allShipmentsConfirmed" :loading="submitting" @click="submitPlacement"
            v-if="!readonly">
            确认并提交分仓方案
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state p-20 text-center bg-white rounded-lg border-2 border-dashed">
        <Icon icon="ep:map-location" :size="64" class="text-gray-200 mb-4" />
        <h3 class="text-gray-500 font-bold">尚未加载分仓方案</h3>
        <p class="text-gray-400 text-sm mt-2">{{ readonly ? '尚未配置分仓方案' : '请点击右上角按钮以生成或选择一个分仓方案' }}</p>
        <el-button
type="primary" class="mt-6" @click="handleGenerate" :loading="generating"
          v-if="!readonly">生成分仓方案</el-button>
      </div>
    </div>

    <!-- 方案选择弹窗 -->
    <PlacementOptionDialog ref="placementDialogRef" @select="handlePlacementSelected" />
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { ref, computed, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Icon } from '@/components/Icon'
import PlacementOptionDialog from './PlacementOptionDialog.vue'
import { SHIPPING_MODES, SHIPPING_SOLUTIONS, SHIPPING_MODES_MAP, SHIPPING_SOLUTIONS_MAP, AVAILABILITY_TYPES_MAP } from './constants'

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

// 从shipmentPlan中提取Amazon inboundPlanId
const planId = computed(() => props.shipmentPlan?.extralId || '')

const emit = defineEmits(['back', 'next'])

const loading = ref(false)
const generating = ref(false)
const submitting = ref(false)
const placementOptionConfirmed = ref(false)
const selectedPlacementOptionId = ref('')
const selectedPlacementOption = ref<any>(null)
const confirmedShipments = ref<any[]>([])
const placementDialogRef = ref()
const shipmentForms = ref<any[]>([])
const shipmentDate = ref('')
const transportationData = ref<Record<string, any>>({})

const rules = {
  deliveryWindowOptionId: [{ required: true, message: '请选择送货窗口', trigger: 'change' }],
  transportationOptionId: [{ required: true, message: '请选择运输渠道', trigger: 'change' }]
}



const hasShipments = computed(() => {
  return confirmedShipments.value && confirmedShipments.value.length > 0
})

const allShipmentsConfirmed = computed(() => {
  return hasShipments.value && confirmedShipments.value.every(s => s.confirmed)
})

/** 禁用过去的日期 */
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

/** 打开方案选择弹窗 */
const openPlacementDialog = () => {
  placementDialogRef.value?.open(planId.value, selectedPlacementOptionId.value)
}

/** 生成分仓方案 */
const handleGenerate = async () => {
  generating.value = true
  try {
    const res = await AmzInboundApi.generatePlacementOptions({ planId: planId.value })
    if (res && res.length > 0) {
      ElMessage.success('分仓方案生成成功，请配置物流信息')
      openPlacementDialog()
    } else {
      ElMessage.warning('方案正在生成中，请稍后刷新列表')
    }
  } catch (error) {
    ElMessage.error('生成分仓方案失败')
  } finally {
    generating.value = false
  }
}

/** 选择方案后的回调 */
const handlePlacementSelected = async (option: any) => {
  loading.value = true
  try {
    // 确认分仓方案
    // await AmzInboundApi.confirmPlacementOption({
    //   planId: planId.value,
    //   placementOptionId: option.placementOptionId
    // })

    selectedPlacementOptionId.value = option.placementOptionId
    selectedPlacementOption.value = option
    placementOptionConfirmed.value = true

    // 初始化货件列表
    confirmedShipments.value = (option.shipmentDetails || []).map((shipment: any) => ({
      ...shipment,
      transportationOptionId: '',
      shipmentDate: shipmentDate.value,
      shippingMode: 'GROUND_SMALL_PARCEL',
      shippingSolution: 'USE_YOUR_OWN_CARRIER',
      deliveryWindowOptionId: '',
      confirmed: false,
      estimatedCost: null
    }))

    ElMessage.success('分仓方案选择成功')
  } catch (error) {
    ElMessage.error('确认分仓方案失败')
  } finally {
    loading.value = false
  }
}

const handleTransportGenerate = async () => {
  generating.value = true
  try {
    const res = await AmzInboundApi.generateTransportationOptions({ planId: planId.value, placementOptionId: selectedPlacementOptionId.value, shipmentDate: shipmentDate.value, shipmentIds: confirmedShipments.value.map(s => s.shipmentId) })
    if (res && res.length > 0) {
      // Store the response data mapping shipmentId to options
      const dataMap: Record<string, any> = {}
      res.forEach((item: any) => {
        dataMap[item.shipmentId] = item
      })
      transportationData.value = dataMap

      ElMessage.success('物流配置生成成功，请配置物流信息')
    } else {
      ElMessage.warning('方案正在生成中，请稍后刷新列表')
    }
  } catch (error) {
    ElMessage.error('生成物流配置失败')
  } finally {
    generating.value = false
  }
}

/** 获取筛选后的运输选项 */
const getFilteredTransportOptions = (shipment: any) => {
  const shipmentData = transportationData.value[shipment.shipmentId]
  if (!shipmentData || !shipmentData.transportationOptions) return []

  const options = shipmentData.transportationOptions.filter((option: any) => {
    return (!shipment.shippingMode || option.shippingMode === shipment.shippingMode) && (!shipment.shippingSolution ||
      option.shippingSolution === shipment.shippingSolution)
  }).map((option: any) => ({
    label: `${option.carrier.name} - ${option.quote?.cost?.code || ''} ${option.quote?.cost?.amount || 'N/A'}`,
    value: option.transportationOptionId,
    carrierName: option.carrier?.name,
    carrierAlphaCode: option.carrier?.alphaCode,
    currency: option.quote?.cost?.code,
    amount: option.quote?.cost?.amount,
    shippingMode: option.shippingMode,
    shippingSolution: option.shippingSolution
  }))
  console.log('getFilteredTransportOptions', options)
  return options
}

/** 获取送货窗口选项 */
const getDeliveryWindows = (shipment: any) => {
  const shipmentData = transportationData.value[shipment.shipmentId]
  console.log('shipmentData', shipmentData)
  if (!shipmentData || !shipmentData.deliveryWindows) return []

  return shipmentData.deliveryWindows.map((window: any) => ({
    label: `${dayjs(window.startDate).format('YYYY-MM-DD')} - ${dayjs(window.endDate).format('YYYY-MM-DD')}`,
    value: window.deliveryWindowOptionId,
    availabilityType: window.availabilityType
  }))
}



/** 运输方式变更 */
const onTransportChange = async (shipment: any) => {
  shipment.confirmed = true
  shipment.estimatedCost = transportationData.value[shipment.shipmentId].transportationOptions
    .find((option: any) => option.transportationOptionId === shipment.transportationOptionId)?.quote?.cost
}

/** 提交分仓方案 */
const submitPlacement = async () => {
  try {
    await ElMessageBox.confirm('确定提交当前分仓方案并进入下一阶段吗？', '提示', {
      type: 'warning'
    })

    // Validate all forms
    if (shipmentForms.value && shipmentForms.value.length > 0) {
      try {
        await Promise.all(shipmentForms.value.map(form => form.validate()))
      } catch (e) {
        ElMessage.warning('请完善运输配置信息')
        return
      }
    }

    submitting.value = true
    await AmzInboundApi.setPlacementInformation({
      planId: planId.value,
      shipmentDate: shipmentDate.value,
      placementOptionId: selectedPlacementOptionId.value,
      placementOption: selectedPlacementOption.value,
      shipments: confirmedShipments.value
    })

    ElMessage.success('分仓方案提交成功')
    console.log('ShipmentPlacementV2: emitting next event')
    emit('next')
    console.log('ShipmentPlacementV2: next event emitted')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

/** 初始化：尝试加载已确认方案 */
const init = async () => {
  if (!planId.value) return
  loading.value = true

  try {
    // 尝试获取已保存的分仓信息
    const placementInfo = await AmzInboundApi.getPlacementInfo({ planId: planId.value })

    if (placementInfo) {
      selectedPlacementOptionId.value = placementInfo.placementOption?.placementOptionId
      selectedPlacementOption.value = placementInfo.placementOption
      shipmentDate.value = placementInfo.shipmentDate
      placementOptionConfirmed.value = true
      confirmedShipments.value = placementInfo.shipments || []


      const dataMap: Record<string, any> = {}
      placementInfo.amzTransportationOptions.forEach((item: any) => {
        dataMap[item.shipmentId] = item
      })
      transportationData.value = dataMap

    } else {
      // 如果没有已保存的信息，尝试获取可用方案列表
      const options = await AmzInboundApi.listPlacementOptions({ planId: planId.value })

      // 如果只有一个方案，可以考虑自动选择（根据业务需求）
      if (options && options.length === 1) {
        // 可选：自动选择唯一方案
        // await handlePlacementSelected(options[0])
      }
    }
  } catch (error) {
    console.error('Failed to init placement options', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.active) init()
})

watch(() => props.active, (val) => {
  if (val && !selectedPlacementOptionId.value) init()
})
</script>

<style scoped>
.shipment-card {
  transition: all 0.3s ease;
}

.shipment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.shipment-header {
  background: linear-gradient(to right, #f5f7fa, #ffffff);
}

.empty-state {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>
