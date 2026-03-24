<template>
  <ContentWrap title="亚马逊入库计划">

    <el-steps :active="currentStep" finish-status="success" align-center class="mb-20px">
      <el-step title="创建计划" description="设置发货地址与SKU" />
      <el-step title="包装装箱" description="设置箱子尺寸与重量" />
      <el-step title="配置货件" description="确认分仓与承运商" />
      <el-step title="获取标签" description="下载箱子与货件标签" />
    </el-steps>

    <div class="step-content">
      <InboundPlan v-if="currentStep === 0" :sku-list="skuList" :warehouse="warehouse" @next="handlePlanCreated" />
      <BoxPacking
v-if="currentStep === 1" :plan-id="inboundPlanId" :active="currentStep === 1" @back="currentStep--"
        @next="handlePackingConfirmed" />
      <ShipmentPlacement
v-if="currentStep === 2" :plan-id="inboundPlanId" @back="currentStep--"
        :active="currentStep === 2" @next="handlePlacementConfirmed" />
      <ShipmentLabel
v-if="currentStep === 3" :plan-id="inboundPlanId" @back="currentStep--" @finish="handleFinish"
        :active="currentStep === 3" />
    </div>


  </ContentWrap>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InboundPlan from './InboundPlan.vue'
import BoxPacking from './BoxPacking.vue'
import ShipmentPlacement from './ShipmentPlacement.vue'
import ShipmentLabel from './ShipmentLabel.vue'

import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'

defineOptions({ name: 'AmzInboundV1' })

const emit = defineEmits(['inboundBizCreated'])

const props = defineProps({
  skuList: {
    type: Array,
    default: () => []
  },
  warehouseId: {
    type: Number,
    // required: true
  }
})

const currentStep = ref(0)
const inboundPlanId = ref('')

const handlePlanCreated = (planId: string) => {
  inboundPlanId.value = planId
  emit('inboundBizCreated', planId)
  currentStep.value = 1
}

const handlePackingConfirmed = () => {
  currentStep.value = 2
}

const handlePlacementConfirmed = () => {
  currentStep.value = 3
}

const handleFinish = () => {
  // Reset or emit event
  currentStep.value = 0
  inboundPlanId.value = ''
}

const warehouse = ref()

onMounted(async () => {
  warehouse.value = await WarehouseApi.getWarehouse(props.warehouseId)
  console.log('Loaded warehouse:', warehouse.value)
})



</script>

<style scoped>
.step-content {
  margin-top: 30px;
}
</style>
