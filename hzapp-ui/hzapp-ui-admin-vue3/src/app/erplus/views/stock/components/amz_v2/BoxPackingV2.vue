<template>
  <div class="box-packing-v2">
    <!-- Header: Action Bar -->
    <div class="flex justify-between items-center mb-6 bg-white p-4 rounded shadow-sm">
      <div class="flex items-center gap-4">

        <el-tag v-if="selectedOptionId" type="success" size="large">
          已确认方案: {{ selectedOptionId }}
        </el-tag>
        <el-tag v-else type="info">未选择方案</el-tag>
      </div>
      <div class="flex gap-2">
        <el-button type="primary" plain @click="openOptionDialog">
          <Icon icon="ep:list" class="mr-1" /> 选择/更换装箱方案
        </el-button>
        <!-- <el-button type="primary" :loading="generating" @click="handleGenerate">
          <Icon icon="ep:refresh" class="mr-1" /> 生成新方案
        </el-button> -->
      </div>
    </div>

    <!-- Main Content: Packing Groups -->
    <div v-loading="loading">
      <div v-if="packingOptionConfirmed && hasGroups" class="space-y-6">
        <div v-for="(group, key) in selectedPackingOption.groupItems" :key="key"
          class="group-card bg-white rounded-lg shadow-sm border overflow-hidden">
          <div class="group-header bg-gray-50 p-4 border-b flex justify-between items-center">
            <div class="flex items-center gap-4">
              <span class="font-bold text-gray-700">箱子组 #{{ key }}</span>
              <span class="text-sm text-gray-500">组内箱数: <span class="text-blue-600 font-bold">{{ group[0]?.boxQuantity
                || 0 }}</span></span>
              <span>
                <el-switch v-model="packingType" active-text="整装" inactive-text="散装" inline-prompt />


              </span>
            </div>
            <div class="flex gap-4 text-sm">
              <span>单箱重量: <b>{{ group[0]?.boxWeight || 0 }} kg</b></span>
              <span>单箱尺寸: <b>{{ group[0]?.boxLength }}x{{ group[0]?.boxWidth }}x{{ group[0]?.boxHeight }} cm</b></span>
            </div>
          </div>

          <el-table :data="group" border stripe class="w-full">
            <el-table-column label="商品信息" min-width="250">
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <el-image :src="row.image" class="w-10 h-10 rounded shrink-0" fit="cover" />
                  <div class="flex flex-col overflow-hidden">
                    <span class="font-bold text-sm truncate">{{ row.msku }}</span>
                    <span class="text-xs text-gray-400 truncate">{{ row.fnsku || row.asin }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单箱装载数量" width="150" align="center">
              <template #default="{ row }">
                <el-input-number v-model="row.quantityInBox" :min="1" size="small" class="w-full"
                  @change="updateGroupConsistency(group, row, 'quantityInBox')" />
              </template>
            </el-table-column>
            <el-table-column label="计划发货总数" width="120" align="center" prop="quantity" />
            <el-table-column label="箱子总数" width="120" align="center" v-if="packingType">
              <template #default="scope">
                {{ scope.row.boxQuantity }}
              </template>
            </el-table-column>
            <el-table-column label="本组装完数量" width="120" align="center" v-else>
              <template #default="{ row }">
                <span :class="{ 'text-green-600 font-bold': row.quantityInBox * row.boxQuantity === row.quantity }">
                  {{ row.quantityInBox * row.boxQuantity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="箱子详情" width="280">
              <template #header>
                <div class="flex items-center justify-center gap-1">
                  箱子参数 (重量/L/W/H)
                </div>
              </template>
              <template #default="{ row, $index }">

                <!-- 整装, 每个箱子只有一个品, 每个品单独设置箱子规格 -->
                <div v-if="packingType" class="flex gap-1">
                  <el-input v-model="row.boxWeight" placeholder="重" size="small" class="w-16" />
                  <el-input v-model="row.boxLength" placeholder="长" size="small" class="w-12" />
                  <el-input v-model="row.boxWidth" placeholder="宽" size="small" class="w-12" />
                  <el-input v-model="row.boxHeight" placeholder="高" size="small" class="w-12" />
                </div>

                <template v-else>
                  <!-- 仅在第一行显示汇总编辑，或者每行都可调但保持组内一致 -->
                  <div v-if="$index === 0" class="flex gap-1">
                    <el-input v-model="row.boxWeight" placeholder="重" size="small" class="w-16" />
                    <el-input v-model="row.boxLength" placeholder="长" size="small" class="w-12" />
                    <el-input v-model="row.boxWidth" placeholder="宽" size="small" class="w-12" />
                    <el-input v-model="row.boxHeight" placeholder="高" size="small" class="w-12" />
                  </div>
                  <div v-else class="text-center text-gray-300 text-xs italic">
                    跟随本组设置
                  </div>
                </template>


              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- Footer Actions -->
        <div class="flex justify-center gap-4 mt-8 pb-10">
          <el-button size="large" @click="$emit('back')">上一步</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="submitPacking">
            确认并提交装箱明细
          </el-button>
        </div>
      </div>

      <div v-else-if="!loading" class="empty-state p-20 text-center bg-white rounded-lg border-2 border-dashed">
        <Icon icon="ep:box" :size="64" class="text-gray-200 mb-4" />
        <h3 class="text-gray-500 font-bold">尚未加载装箱方案</h3>
        <p class="text-gray-400 text-sm mt-2">请点击右上角按钮以生成或选择一个装箱方案</p>
        <el-button type="primary" class="mt-6" @click="handleGenerate" :loading="generating">生成装箱方案</el-button>
      </div>
    </div>

    <!-- 方案选择弹窗 -->
    <PackingOptionDialog ref="optionDialogRef" @select="handleOptionSelected" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Icon } from '@/components/Icon'
import PackingOptionDialog from './PackingOptionDialog.vue'

const props = defineProps({
  shipmentPlan: {
    type: Object,
    required: true
  },
  active: {
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
const packingType = ref(false)
const packingOptionConfirmed = ref(false)
const selectedOptionId = ref('')
const selectedPackingOption = ref<any>({})
const optionDialogRef = ref()

const hasGroups = computed(() => {
  return selectedPackingOption.value.groupItems && Object.keys(selectedPackingOption.value.groupItems).length > 0
})

/** 打开方案选择弹窗 */
const openOptionDialog = () => {
  optionDialogRef.value.open(planId.value, selectedOptionId.value)
}

/** 生成方案 */
const handleGenerate = async () => {
  generating.value = true
  try {
    const res = await AmzInboundApi.generatePackingOptions({ planId: planId.value })
    if (res && res.length > 0) {
      ElMessage.success('方案生成成功，请选择')
      openOptionDialog()
    } else {
      ElMessage.warning('方案正在生成中，请稍后刷新列表')
    }
  } catch (error) {
    ElMessage.error('生成方案失败')
  } finally {
    generating.value = false
  }
}

/** 选择方案后的回调 */
const handleOptionSelected = async (option: any) => {
  loading.value = true
  try {
    await AmzInboundApi.confirmPackingOption({
      planId: planId.value,
      packingOptionId: option.packingOptionId
    })
    selectedPackingOption.value = option
    selectedOptionId.value = option.packingOptionId
    packingOptionConfirmed.value = true
    ElMessage.success('方案选择成功')
  } catch (error) {
    ElMessage.error('确认方案失败')
  } finally {
    loading.value = false
  }
}

/** 保持组内箱数和尺寸一致 */
const updateGroupConsistency = (group: any[], changedRow: any, field: string) => {

  // 如果是散装, 确保每个箱子的规格相同
  if (!packingType.value) {
    // 如果修改了单箱装载数量，重新计算推导的箱数（简化逻辑：取所有SKU推导出来的最大箱数）
    if (field === 'quantityInBox') {
      const boxQty = Math.ceil(changedRow.quantity / changedRow.quantityInBox)
      group.forEach(item => {
        item.boxQuantity = boxQty
      })
    }
    // 保持组内所有行的箱子参数同步
    const first = group[0]
    group.forEach((item, index) => {
      if (index > 0) {
        item.boxWeight = first.boxWeight
        item.boxLength = first.boxLength
        item.boxWidth = first.boxWidth
        item.boxHeight = first.boxHeight
        item.boxQuantity = first.boxQuantity
      }
    })
  } else {

    changedRow.boxQuantity = Math.ceil(changedRow.quantity / changedRow.quantityInBox)

  }

}

/** 提交装箱明细 */
const submitPacking = async () => {
  try {
    await ElMessageBox.confirm('确定提交当前装箱明细并进入下一阶段吗？', '提示', {
      type: 'warning'
    })
    submitting.value = true
    await AmzInboundApi.setPackingInformation({
      planId: planId.value,
      packingType: packingType.value,
      option: selectedPackingOption.value
    })
    ElMessage.success('装箱明细提交成功')
    emit('next')
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

    const packingInfo = await AmzInboundApi.getPackingInfo({ planId: planId.value })
    const options = await AmzInboundApi.listPackingOptions(planId.value)

    if (packingInfo !== null) {
      selectedPackingOption.value = packingInfo
      selectedOptionId.value = packingInfo.packingOptionId
      packingOptionConfirmed.value = true
    }
    // 如果有已确认或者现成的方案，自动加载第一个（实际业务可能需要更复杂的判断）
    else if (options && options.length > 0) {
      selectedPackingOption.value = options[0]
      selectedOptionId.value = options[0].packingOptionId
      packingOptionConfirmed.value = true
    }
  } catch (error) {
    console.error('Failed to init packing options', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.active) init()
})

watch(() => props.active, (val) => {
  if (val && !selectedOptionId.value) init()
})
</script>

<style scoped>
.group-card {
  transition: all 0.3s ease;
}

.group-card:hover {
  border-color: #409eff;
}

.group-header b {
  color: #409eff;
}

:deep(.el-input-number.el-input-number--small) {
  width: 100%;
}
</style>
