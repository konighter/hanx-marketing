<template>
  <el-drawer v-model="visible" size="90%" :destroy-on-close="true" class="shipment-config-drawer" @close="handleClose">
    <template #header>
      <div class="flex items-center justify-between w-full pr-10">
        <span class="text-lg font-bold flex items-center gap-2 shrink-0">
          <Icon icon="ep:setting" />
          {{ configTitle }}
          <el-button v-if="!readonly && shipmentData.status !== ShipmentStatus.CANCELED" type="danger" link size="small"
            @click="handleCancel">
            <Icon icon="ep:circle-close" class="mr-1" /> 取消计划
          </el-button>
        </span>
        <div class="flex-1 max-w-600px">
          <el-steps :active="activeStep" align-center finish-status="success" simple class="clickable-steps">
            <el-step v-for="(step, index) in steps" :key="index" :title="step.title"
              :class="{ 'is-clickable': index <= activeStep, 'is-selected': index === selectedStep }"
              @click="handleStepClick(index)" />
          </el-steps>
        </div>
      </div>
    </template>
    <div v-loading="loading" class="config-container h-full flex flex-col bg-gray-50 overflow-auto">
      <div class="flex-1 min-h-0 p-4">
        <!-- 中间主体：动态组件/内容 -->
        <div class="content-wrapper bg-white p-6 rounded shadow-sm mb-4">
          <component :is="activeComponent" v-if="activeComponent" :shipment-plan="shipmentData" :active="visible"
            :readonly="effectiveReadonly" @next="handleNext" @back="handleBack" />
          <div v-else class="empty-content flex items-center justify-center border-dashed border-2 border-gray-200">
            <div class="text-center text-gray-400">
              <Icon icon="ep:monitor" :size="48" class="mb-2" />
              <p>当前阶段：{{ getStatusLabel(shipmentData.status) }} 的配置内容正在开发中...</p>
              <p class="text-xs">平台: {{ shipmentData.platform || 'Amazon' }} (ID: {{ shipmentData.platformId }})</p>
            </div>
          </div>
        </div>

        <!-- 底部：货件概述 -->
        <ShipmentOverview :shipmentData="shipmentData" class="px-4 pb-4" />
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { shipmentApi, ShipmentStatus } from '@/app/erplus/api/stock/shipment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Icon } from '@/components/Icon'
import ShipmentOverview from './ShipmentOverview.vue'
import { selectComponent, getStatusLabel } from './config'

import ShipmentAudit from './ShipmentAudit.vue'
defineOptions({ name: 'ShipmentConfig' })

const visible = ref(false)
const loading = ref(false)
const readonly = ref(false)
const shipmentData = ref<any>({})
const selectedStep = ref<number>(0)

/** 步骤配置 */
const steps = [
  { title: '审核', status: ShipmentStatus.AUDITING },
  { title: '装箱', status: ShipmentStatus.PENDING_BOXING },
  { title: '物流', status: ShipmentStatus.PENDING_SHIPMENT },
  { title: '贴标', status: ShipmentStatus.PENDING_LABEL },
  { title: '发货', status: ShipmentStatus.SHIPPED }
]

/** 动态标题 */
const configTitle = computed(() => {
  const label = getStatusLabel(shipmentData.value.status)
  return `货件配置 - ${label}`
})

/** 映射状态到步骤索引 */
const activeStep = computed(() => {
  const status = Number(shipmentData.value.status)
  switch (status) {

    case ShipmentStatus.AUDITING: return 0
    case ShipmentStatus.PENDING_BOXING: return 1
    case ShipmentStatus.PENDING_SHIPMENT: return 2
    case ShipmentStatus.PENDING_LABEL: return 3
    case ShipmentStatus.SHIPPED: return 5
    default: return 0
  }
})

/** 动态计算组件是否只读 */
const effectiveReadonly = computed(() => {
  if (readonly.value) return true

  const currentStatus = Number(shipmentData.value.status)
  const stepStatus = steps[selectedStep.value]?.status

  // 如果已完成或已取消，全部只读
  if (currentStatus >= ShipmentStatus.SHIPPED) return true

  // 当全局状态处于 PENDING_LABEL (40) 或之后（但还没到已发货）时，锁定之前的步骤 (10, 20, 30)
  if (currentStatus >= ShipmentStatus.PENDING_LABEL) {
    if (stepStatus && stepStatus < ShipmentStatus.PENDING_LABEL) {
      return true
    }
  }

  return false
})

/** 动态组件逻辑 - 基于选中的步骤 */
const activeComponent = computed(() => {

  if (shipmentData.value.status === ShipmentStatus.AUDITING) {
    return ShipmentAudit
  }


  // 如果选中的步骤有对应的状态，使用选中步骤的状态
  const targetStatus = steps[selectedStep.value]?.status || shipmentData.value.status
  console.log("ShipmentConfig: activeComponent triggered, selectedStep=", selectedStep.value, "targetStatus=", targetStatus)
  return selectComponent(shipmentData.value.platformId, targetStatus)
})

const nextStatus = computed(() => {
  const status = Number(shipmentData.value.status)
  console.log("ShipmentConfig: nextStatus triggered, selectedStep=", selectedStep.value, "status=", status)
  switch (status) {
    case ShipmentStatus.AUDITING: return ShipmentStatus.PENDING_BOXING
    case ShipmentStatus.PENDING_BOXING: return ShipmentStatus.PENDING_SHIPMENT
    case ShipmentStatus.PENDING_SHIPMENT: return ShipmentStatus.PENDING_LABEL
    case ShipmentStatus.PENDING_LABEL:
    case ShipmentStatus.PENDING_DELIVERY:
      return ShipmentStatus.SHIPPED
    default: return ShipmentStatus.INIT
  }
})

/** 处理步骤点击 */
const handleStepClick = (index: number) => {
  // 只允许点击已完成和当前步骤
  if (index <= activeStep.value) {
    selectedStep.value = index
  }
}

/** 下一步 / 完成当前阶段 */
const handleNext = async () => {
  if (loading.value) return

  const currentStatus = Number(shipmentData.value.status)
  const stepStatus = steps[selectedStep.value]?.status

  // 1. 如果当前操作的步骤已经落后于全局状态 (说明是在修改历史步骤)
  if (stepStatus !== undefined && stepStatus < currentStatus) {
    // 仅仅是 UI 上的跳转，不更新后端状态
    selectedStep.value = Math.min(selectedStep.value + 1, steps.length - 1)

    return
  }

  // 2. 正常推进流程的逻辑 (当前步骤就是全局最新进度)
  const status = nextStatus.value
  if (status === ShipmentStatus.INIT) {
    console.warn("ShipmentConfig: nextStatus is INIT, staying on current page.")
    return
  }

  loading.value = true
  try {
    console.log("ShipmentConfig: handleNext triggered, current status=", currentStatus, "nextStatus=", status)
    await shipmentApi.updateShipmentStatus({
      shipmentId: shipmentData.value.id,
      status: status
    })

    // 重新获取最新数据
    shipmentData.value = await shipmentApi.getShipment(shipmentData.value.id)
    ElMessage.success('操作成功')

    // 更新选中步骤到新的当前步骤 (确保索引不越界)
    // 如果 activeStep 是 5 (已完成状态), 我们停留在最后一个 Tab (索引 4)
    selectedStep.value = Math.min(activeStep.value, steps.length - 1)
  } catch (error) {
    ElMessage.error('更新状态失败，请重试')
  } finally {
    loading.value = false
  }
}

/** 上一步 */
const handleBack = () => {
  if (selectedStep.value > 0) {
    selectedStep.value--
  } else {
    visible.value = false
  }
}

/** 获取状态文本 */
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该货件计划吗？取消后不可恢复。', '警告', {
      type: 'warning',
      confirmButtonText: '确定取消',
      cancelButtonText: '暂不取消',
      confirmButtonClass: 'el-button--danger'
    })

    loading.value = true
    await shipmentApi.updateShipmentStatus({
      shipmentId: shipmentData.value.id,
      status: ShipmentStatus.CANCELED
    })

    shipmentData.value.status = ShipmentStatus.CANCELED
    visible.value = false
    ElMessage.success('货件计划已取消')

  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  } finally {
    loading.value = false
  }
}



/** 打开配置抽屉 */
const open = async (row: any, isReadonly = false) => {
  loading.value = true
  readonly.value = isReadonly
  try {
    const data = await shipmentApi.getShipment(row.id)
    shipmentData.value = data
    // 默认选中当前步骤
    selectedStep.value = activeStep.value
    // 数据拿到后再打开抽屉，确保子组件初始化时有数据
    visible.value = true
  } catch (error) {
    ElMessage.error('获取货件详情失败')
  } finally {
    loading.value = false
  }
}

const emit = defineEmits(['close'])

/** 关闭抽屉 */
const handleClose = () => {
  emit('close')
}

defineExpose({ open })
</script>

<style scoped>
.config-container {
  max-height: calc(100vh - 80px);
}

.content-wrapper {
  min-height: 300px;
}

.empty-content {
  min-height: 400px;
}

/* 可点击步骤样式 */
:deep(.clickable-steps .el-step.is-clickable) {
  cursor: pointer;
  transition: all 0.3s ease;
}

:deep(.clickable-steps .el-step.is-clickable:hover .el-step__head) {
  transform: scale(1.1);
}

:deep(.clickable-steps .el-step.is-clickable:hover .el-step__title) {
  color: var(--el-color-primary);
  font-weight: 600;
}

/* 选中步骤样式 */
:deep(.clickable-steps .el-step.is-selected .el-step__head) {
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

:deep(.clickable-steps .el-step.is-selected .el-step__title) {
  color: var(--el-color-primary);
  font-weight: 700;
}

/* 未完成步骤不可点击 */
:deep(.clickable-steps .el-step:not(.is-clickable)) {
  cursor: not-allowed;
  opacity: 0.6;
}

/* 抽屉标题样式调整 - 需要全局样式覆盖 */
.shipment-config-drawer .el-drawer__header {
  margin-bottom: 0 !important;
  padding: 12px 20px !important;
}

.shipment-config-drawer .el-steps--simple {
  padding: 10px 8% !important;
}
</style>

<style></style>
