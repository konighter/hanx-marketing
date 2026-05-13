<template>
  <el-card shadow="never" class="performance-trend-card" :body-style="{ padding: '12px' }">
    <template #header>
      <div class="flex justify-between items-center flex-wrap gap-4">
        <div class="flex items-center">
          <span class="font-bold text-lg">业绩走势</span>
          <Icon icon="ep:info-filled" class="ml-2 text-gray-400 cursor-help" />
        </div>
        <div class="flex items-center space-x-2">
          <el-radio-group v-model="timeRange">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="yesterday">昨日</el-radio-button>
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="lastMonth">上月</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            placeholder="自定义"
            style="width: 260px"
          />
          <el-button-group>
            <el-button>指标对比</el-button>
            <el-button>时间对比</el-button>
          </el-button-group>
          <Icon icon="ep:full-screen" class="cursor-pointer text-gray-500 hover:text-primary text-xl" />
          <Icon icon="ep:setting" class="cursor-pointer text-gray-500 hover:text-primary text-xl" />
        </div>
      </div>
    </template>

    <!-- Metric Cards Row -->
    <div class="flex space-x-2 mb-3 overflow-x-auto pb-2 scrollbar-hide">
      <div 
        v-for="(item, index) in metrics" 
        :key="item.label"
        class="metric-tab-card min-w-[140px] p-2 rounded-lg border-2 cursor-pointer transition-all flex-1"
        :class="activeMetrics.includes(index) ? 'active-metric shadow-sm' : 'border-transparent bg-gray-50'"
        :style="activeMetrics.includes(index) ? { borderColor: item.color, borderTopWidth: '4px' } : {}"
        @click="toggleMetric(index)"
      >
        <div class="flex items-center text-xs text-gray-500 mb-2">
          <span>{{ item.label }}</span>
          <Icon icon="ep:info-filled" class="ml-1 text-gray-300" />
        </div>
        <div class="text-xl font-bold font-mono mb-1" :style="{ color: activeMetrics.includes(index) ? '#303133' : '#909399' }">
          {{ item.prefix }}{{ item.value }}{{ item.suffix }}
        </div>
        <div class="flex flex-col text-[10px] space-y-0.5">
          <div class="flex justify-between">
            <span class="text-gray-400">环比</span>
            <span :class="item.huanbi >= 0 ? 'text-success' : 'text-danger'">
              {{ item.huanbi >= 0 ? '+' : '' }}{{ (item.huanbi * 100).toFixed(2) }}%
            </span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-400">同比</span>
            <span :class="item.tongbi >= 0 ? 'text-success' : 'text-danger'">
              {{ item.tongbi >= 0 ? '+' : '' }}{{ (item.tongbi * 100).toFixed(2) }}%
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Chart -->
    <div class="h-64">
      <Echart :options="chartOptions" height="100%" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Echart from '@/components/Echart/src/Echart.vue'

const timeRange = ref('30d')
const dateRange = ref([])
const activeMetrics = ref([0, 1, 2]) // Default selected: Sales, Amount, Ad Spend

const metrics = [
  { label: '销量', value: '100', prefix: '', suffix: '', huanbi: 3.3478, tongbi: 1.0, color: '#3b82f6' },
  { label: '销售额', value: '2,191.08', prefix: '$', suffix: '', huanbi: 3.2647, tongbi: 1.0, color: '#ef4444' },
  { label: '广告花费', value: '136.16', prefix: '$', suffix: '', huanbi: 1.0, tongbi: 1.0, color: '#22c55e' },
  { label: '广告销售额', value: '625.68', prefix: '$', suffix: '', huanbi: 0.1592, tongbi: 1.0, color: '#0ea5e9' },
  { label: 'ACOS', value: '21.76', prefix: '', suffix: '%', huanbi: 0.2176, tongbi: 0.2176, color: '#8b5cf6' },
  { label: '退款额', value: '35.36', prefix: '$', suffix: '', huanbi: -0.1795, tongbi: 1.0, color: '#f97316' },
  { label: '结算毛利润', value: '273.22', prefix: '$', suffix: '', huanbi: 1.0004, tongbi: 1.0, color: '#ec4899' },
  { label: '结算毛利率', value: '30.50', prefix: '', suffix: '%', huanbi: -0.1553, tongbi: 0.305, color: '#10b981' }
]

const toggleMetric = (index: number) => {
  const i = activeMetrics.value.indexOf(index)
  if (i > -1) {
    if (activeMetrics.value.length > 1) {
      activeMetrics.value.splice(i, 1)
    }
  } else {
    activeMetrics.value.push(index)
  }
}

const chartOptions = computed(() => {
  const dates = ['04-13', '04-16', '04-19', '04-22', '04-25', '04-28', '05-01', '05-04', '05-07', '05-10', '05-12']
  
  const series = activeMetrics.value.map(index => {
    const m = metrics[index]
    // Mock data based on index
    const baseData = [150, 40, 20, 10, 15, 30, 80, 250, 120, 45, 30]
    const multiplier = (index + 1) * 20
    return {
      name: m.label,
      type: 'line',
      smooth: true,
      symbol: 'none',
      data: baseData.map(v => v * (1 + index * 0.2) + Math.random() * 10),
      itemStyle: { color: m.color },
      lineStyle: { width: 2 },
      areaStyle: index === activeMetrics.value[0] ? {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: m.color + '33' }, { offset: 1, color: m.color + '00' }]
        }
      } : undefined
    }
  })

  return {
    tooltip: { trigger: 'axis' },
    legend: { show: false },
    grid: { left: '3%', right: '4%', bottom: '10%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#94a3b8', fontSize: 10 },
      boundaryGap: false
    },
    yAxis: [
      { type: 'value', axisLabel: { color: '#94a3b8' }, splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } } },
      { type: 'value', axisLabel: { color: '#94a3b8' }, splitLine: { show: false } }
    ],
    series
  }
})
</script>

<style scoped>
.performance-trend-card {
  border-radius: 12px;
}
.metric-tab-card {
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
  border: 2px solid transparent;
}
.metric-tab-card:hover {
  background-color: #f8fafc;
}
.active-metric {
  background-color: white !important;
  transform: translateY(-2px);
}
.text-success { color: #52c41a; }
.text-danger { color: #f5222d; }
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>

