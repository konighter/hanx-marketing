<template>
  <div class="bg-white p-4 rounded shadow-sm overflow-hidden flex flex-col">
    <h3 class="text-lg font-bold mb-4 border-l-4 border-blue-500 pl-2">货件概述</h3>
    <el-row :gutter="12">
      <el-col :span="8">
        <el-card shadow="never" header="发货信息" class="h-full">
          <div class="text-sm space-y-2">
            <div class="flex justify-between">
              <span class="text-gray-500">店铺:</span>
              <span>{{ shipmentData.shopName }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">发货仓库:</span>
              <span>{{ shipmentData.warehouseName }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">目的地:</span>
              <span>{{ shipmentData.destination }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" header="商品体积重量" class="h-full">
          <div class="text-sm space-y-2">
            <div class="flex justify-between">
              <span class="text-gray-500">总重:</span>
              <span>{{ calculateTotalWeight() }} kg</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">总体积:</span>
              <span>{{ calculateTotalVolume() }} CBM</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">SKU数量:</span>
              <span>{{ shipmentData.items?.length || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" header="预估运费" class="h-full">
          <div class="flex flex-col items-center justify-center h-full gap-2">
            <span class="text-2xl font-bold text-orange-500">¥ {{ shipmentData.estimatedFreight || '--' }}</span>
            <span class="text-xs text-gray-400">基于历史运费计算</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { } from 'vue'

const props = defineProps({
  shipmentData: {
    type: Object,
    required: true,
    default: () => ({})
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

/** 计算总重量 */
const calculateTotalWeight = () => {
  if (!props.shipmentData.items) return 0
  return props.shipmentData.items.reduce((total: number, item: any) => {
    return total + (Number(item.weight) || 0) * (item.quantity || 0)
  }, 0).toFixed(2)
}

/** 计算总体积 */
const calculateTotalVolume = () => {
  if (!props.shipmentData.items) return 0
  // 简化的体积计算逻辑（长*宽*高/1000000得到立方米）
  return props.shipmentData.items.reduce((total: number, item: any) => {
    const [l, w, h] = item.dimension?.split('*').map(Number) || [0, 0, 0]
    const volume = (l * w * h) / 1000000
    return total + volume * (item.quantity || 0)
  }, 0).toFixed(4)
}
</script>

<style scoped>
:deep(.el-card__header) {
  padding: 8px 12px;
  font-weight: bold;
  font-size: 14px;
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>
