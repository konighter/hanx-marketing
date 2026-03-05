<template>
  <el-table :data="placementData" border style="width: 100%">
    <el-table-column label="位置" prop="label" width="180" />
    <el-table-column label="竞价调整 (%)">
      <template #default="{ row }">
        <el-input-number 
          v-model="row.percentage" 
          :min="0" 
          :max="900" 
          :step="10" 
          size="small"
          :disabled="disabled"
          @change="emitUpdate(row)"
        /> %
      </template>
    </el-table-column>
    <el-table-column label="目标出价">
      <template #default="{ row }">
        <el-input-number 
          v-model="row.targetBid"
          :precision="2" 
          :step="0.01" 
          :min="0" 
          size="small"
          :disabled="disabled"
        />
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: any[]
  disabled?: boolean
}>()

const emit = defineEmits(['update'])

const placementData = ref([
  { type: 'PLACEMENT_TOP', label: '首页 (搜索顶部)', percentage: 0, targetBid: 0 },
  { type: 'PLACEMENT_PRODUCT_PAGE', label: '商品详情页', percentage: 0, targetBid: 0 },
  { type: 'PLACEMENT_REST_OF_SEARCH', label: '搜索其他位置', percentage: 0, targetBid: 0 }
])

watch(() => props.modelValue, (adjustments) => {
  if (adjustments) {
    placementData.value.forEach(item => {
      const adj = adjustments.find(a => a.placement === item.type)
      item.percentage = adj ? adj.percentage : 0
      if (item.targetBid === undefined) {
        item.targetBid = 0
      }
    })
  }
}, { immediate: true, deep: true })

const emitUpdate = (row: any) => {
  emit('update', row.type, row.percentage)
}
</script>
