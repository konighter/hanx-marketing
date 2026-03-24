<template>
  <div class="shipment-placement">
    <div v-if="!placementOptionConfirmed">
      <div class="mb-20px flex justify-between items-center">
        <h3>分仓方案确认</h3>
        <el-button type="primary" :loading="loading" @click="getPlacementOptions">生成/刷新方案</el-button>
      </div>

      <el-skeleton :loading="loading" animated>
        <template #template>
          <el-skeleton-item variant="p" style="width: 100%" />
          <el-skeleton-item variant="p" style="width: 80%" />
        </template>
        <div
v-for="option in placementOptions" :key="option.placementOptionId"
          class="option-card mb-15px p-15px border rounded hover:bg-gray-50 transition-colors">
          <div class="flex justify-between items-center">
            <div>
              <span class="font-bold">方案 ID: {{ option.placementOptionId }}</span>
              <el-tag type="info" class="ml-10px">费用: {{ option.placeFee.currency }} {{ option.placeFee.amount
                }}</el-tag>
            </div>
            <el-button type="success" size="small" @click="confirmPlacement(option.placementOptionId)">确认此方案</el-button>
          </div>
          <div class="mt-10px text-gray-500">
            货件详情:
            <ul class="ml-20px mt-5px">
              <li v-for="shipment in option.shipmentDetails" :key="shipment.shipmentId">
                发送至:

                <el-tooltip :content="shipment.destination.warehouseId" placement="top" effect="light">
                  <span class="text-truncate">{{ shipment.destination.warehouseId }} </span>

                  <template #content>
                    <div>
                      <div class="address-details mt-10px p-10px bg-gray-50 border-rounded">
                        <div>{{ shipment.destination.address.name }}</div>
                        <div class="text-gray-500 text-sm">
                          {{ shipment.destination.address.addressLine1 }}, {{ shipment.destination.address.city }}, {{
                            shipment.destination.address.postalCode }}, {{
                            shipment.destination.address.countryCode }}
                        </div>
                      </div>
                    </div>
                  </template>


                </el-tooltip>

                <el-divider direction="vertical" />

                <el-tooltip :content="shipment.itemCount" placement="top" effect="light">
                  <span class="text-truncate">(共{{ shipment.shipmentItems.length }}种商品)</span>

                  <template #content>
                    <div class="m-5">
                      <el-table :data="shipment.shipmentItems" :min-height="300">
                        <el-table-column prop="asin" label="ASIN" width="150" />
                        <el-table-column prop="quantity" label="数量" />
                      </el-table>
                    </div>
                  </template>



                </el-tooltip>


              </li>
            </ul>
          </div>
        </div>
      </el-skeleton>

      <div
v-if="!placementOptionConfirmed && placementOptions.length === 0 && !loading"
        class="text-center p-40px text-gray-400 border-dashed border-2 rounded">
        暂无方案，请点击“生成/刷新方案”
      </div>

      <div class="flex justify-center mt-30px">
        <el-button @click="$emit('back')">上一步</el-button>
      </div>
    </div>

    <div v-else>
      <div class="mb-20px">
        <div class="flex justify-between items-center mb-10px">
          <h3>承运商与运输方式</h3>
          <el-button link type="primary" @click="placementOptionConfirmed = false">返回重新选择方案</el-button>
        </div>
        <el-alert title="请为每个货件选择承运商" type="success" :closable="false" class="mb-15px" />
      </div>

      <div
v-for="shipment in confirmedShipments" :key="shipment.shipmentId"
        class="shipment-card mb-20px p-20px border rounded">
        <div class="flex justify-between mb-15px">
          <span class="font-bold">货件: {{ shipment.shipmentId }} ({{ shipment.destination }})</span>
          <el-tag v-if="shipment.confirmed" type="success">运输已确认</el-tag>
        </div>

        <el-form label-width="100px">
          <el-form-item label="运输渠道">
            <el-select
v-model="shipment.optionId" placeholder="选择运输选项" class="w-250px"
              @change="onTransportChange(shipment)">
              <el-option label="UPS (Ground)" value="ups_ground" />
              <el-option label="FedEx (Express)" value="fedex_exp" />
              <el-option label="LTL (Less Than Truckload)" value="ltl" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <div class="flex justify-center gap-4 mt-30px">
        <el-button @click="placementOptionConfirmed = false">返回</el-button>
        <el-button type="primary" :disabled="!allConfirmed" @click="$emit('next')">进入下一步（取标）</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'

const props = defineProps({
  planId: {
    type: String,
    required: true
  },
  active: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['back', 'next'])

const loading = ref(false)
const placementOptionConfirmed = ref(false)
const placementOptions = ref<any[]>([])
const confirmedShipments = ref<any[]>([])

const allConfirmed = computed(() => {
  return confirmedShipments.value.length > 0 && confirmedShipments.value.every(s => s.confirmed)
})

const getPlacementOptions = async () => {
  loading.value = true
  try {
    let res = await AmzInboundApi.listPlacementOptions({ planId: props.planId })
    if (!res || res.length === 0) {
      placementOptions.value = await AmzInboundApi.generatePlacementOptions({ planId: props.planId })
      return
    }
    placementOptions.value = res
  } catch (error) {
    ElMessage.error('获取分仓方案失败')
  } finally {
    loading.value = false
  }
}

const generatePlacementOptions = async () => {
  loading.value = true
  try {
    placementOptions.value = await AmzInboundApi.generatePlacementOptions({ planId: props.planId })
  } catch (error) {
    ElMessage.error('生成分仓方案失败')
  } finally {
    loading.value = false
  }
}

const confirmPlacement = async (optionId: string) => {
  loading.value = true
  try {
    // await AmzInboundApi.confirmPlacementOption(props.planId, optionId)
    console.log('confirmPlacement', optionId)
    placementOptionConfirmed.value = true
    const selected = placementOptions.value.find(o => o.placementOptionId === optionId)
    console.log('selected', selected)
    confirmedShipments.value = selected.shipmentDetails.map((s: any) => ({
      ...s,
      optionId: '',
      confirmed: false
    }))
    ElMessage.success('方案已确认')
  } catch (error) {
    ElMessage.error('确认方案失败')
  } finally {
    loading.value = false
  }
}

const onTransportChange = async (shipment: any) => {
  try {
    await AmzInboundApi.confirmTransportationOptions({
      planId: props.planId,
      shipmentId: shipment.shipmentId,
      transportationOptionId: shipment.optionId
    })
    shipment.confirmed = true
    ElMessage.success(`${shipment.shipmentId} 运输方式已确认`)
  } catch (error) {
    ElMessage.error('确认运输失败')
  }
}
onMounted(() => {
  if (props.active) {
    getPlacementOptions()
  }
})

watch(() => props.active, (val) => {
  if (val && placementOptions.value.length === 0) {
    getPlacementOptions()
  }
})
</script>

<style scoped>
.option-card:hover {
  background-color: #f5f7fa;
}

.shipment-card {
  border-left: 4px solid #409eff;
}

.address-details {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
</style>
