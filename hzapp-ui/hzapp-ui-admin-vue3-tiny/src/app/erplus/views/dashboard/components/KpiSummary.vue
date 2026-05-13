<template>
  <div class="sales-analysis-card bg-white p-3 rounded-xl shadow-sm">
    <div class="flex items-center mb-3">
      <div class="w-1 h-5 bg-primary mr-3 rounded-full"></div>
      <h3 class="text-lg font-bold text-gray-800">销售分析</h3>
      <el-tooltip content="统计选定时间范围内的付款金额及相关转化指标" placement="top">
        <Icon icon="ep:info-filled" class="ml-2 text-gray-400 cursor-help" />
      </el-tooltip>
    </div>

    <div class="grid grid-cols-5 gap-2">
      <div v-for="item in salesMetrics" :key="item.title" class="metric-item p-2 rounded-lg bg-gray-50/50 border border-gray-100 hover:border-primary/30 transition-all">
        <div class="text-gray-500 text-sm mb-2">{{ item.title }}</div>
        <div class="text-2xl font-bold font-mono mb-3">
          <CountTo :end-val="item.value" :prefix="item.prefix" :decimals="item.decimals" />
          <span v-if="item.suffix" class="text-sm ml-1">{{ item.suffix }}</span>
        </div>
        <div class="flex flex-col space-y-1">
          <div class="flex items-center text-xs justify-between">
            <span class="text-gray-400">较昨日</span>
            <span :class="item.dayDelta >= 0 ? 'text-success' : 'text-danger'" class="font-medium">
              {{ item.dayDelta >= 0 ? '↑' : '↓' }} {{ Math.abs(item.dayDelta * 100).toFixed(1) }}%
            </span>
          </div>
          <div class="flex items-center text-xs justify-between">
            <span class="text-gray-400">较去年同期</span>
            <span :class="item.yearDelta >= 0 ? 'text-success' : 'text-danger'" class="font-medium">
              {{ item.yearDelta >= 0 ? '↑' : '↓' }} {{ Math.abs(item.yearDelta * 100).toFixed(1) }}%
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  loading: Boolean,
  data: Object
})

const salesMetrics = computed(() => [
  {
    title: '付款金额',
    value: 582450.00,
    prefix: '¥',
    decimals: 2,
    dayDelta: 0.125,
    yearDelta: 0.452
  },
  {
    title: '付款订单',
    value: 1256,
    decimals: 0,
    dayDelta: 0.082,
    yearDelta: 0.231
  },
  {
    title: '付款买家',
    value: 1120,
    decimals: 0,
    dayDelta: 0.045,
    yearDelta: 0.198
  },
  {
    title: '访客数',
    value: 15680,
    decimals: 0,
    dayDelta: -0.021,
    yearDelta: 0.085
  },
  {
    title: '支付转化率',
    value: 7.15,
    suffix: '%',
    decimals: 2,
    dayDelta: 0.012,
    yearDelta: 0.054
  }
])
</script>

<style scoped>
.metric-item {
  transition: transform 0.2s, box-shadow 0.2s;
}
.metric-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}
.text-success { color: #52c41a; }
.text-danger { color: #f5222d; }
</style>


