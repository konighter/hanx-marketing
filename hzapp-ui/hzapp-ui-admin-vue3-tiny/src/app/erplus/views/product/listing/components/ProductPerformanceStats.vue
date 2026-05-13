<template>
  <div v-loading="loading" class="performance-stats flex flex-col gap-6 p-6">
    <!-- Trend Line Chart -->
    <div class="chart-card bg-white rounded-xl border p-4 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-sm font-bold text-gray-700">近30天搜索绩效 (SCP) 趋势</h3>
        <el-radio-group v-model="trendMetric" size="small">
          <el-radio-button label="impressionCount">展示</el-radio-button>
          <el-radio-button label="clickCount">点击</el-radio-button>
          <el-radio-button label="cartAddCount">加购</el-radio-button>
          <el-radio-button label="purchaseCount">购买</el-radio-button>
        </el-radio-group>
      </div>
      <div class="h-[300px]">
        <Echart :options="lineOptions" />
      </div>
    </div>

    <!-- Funnel and Pie Row -->
    <div class="flex gap-6 h-[400px]">
      <div class="chart-card bg-white rounded-xl border p-4 shadow-sm flex-1">
        <h3 class="text-sm font-bold text-gray-700 mb-4">各维度转化漏斗</h3>
        <div class="h-full">
          <Echart :options="funnelOptions" @click="handleFunnelClick" />
        </div>
      </div>
      <div class="chart-card bg-white rounded-xl border p-4 shadow-sm w-[350px]">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-sm font-bold text-gray-700">库存时效分布 ({{ currentFunnelStageName }})</h3>
        </div>
        <div class="h-full">
          <Echart :options="pieOptions" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed } from 'vue'
import { EChartsOption } from 'echarts'
import Echart from '@/components/Echart/src/Echart.vue'
import request from '@/config/axios'

const props = defineProps<{
  asin: string
}>()

const loading = ref(false)
const rawData = ref<any[]>([])
const trendMetric = ref('impressionCount')
const currentFunnelStage = ref('impression') // impression, click, cartAdd, purchase

const currentFunnelStageName = computed(() => {
  const names: Record<string, string> = {
    impression: '展示',
    click: '点击',
    cartAdd: '加购',
    purchase: '购买'
  }
  return names[currentFunnelStage.value] || '展示'
})

const lineOptions = reactive<EChartsOption>({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    data: [],
    type: 'line',
    smooth: true,
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [{ offset: 0, color: '#3b82f6' }, { offset: 1, color: '#eff6ff' }]
      }
    },
    itemStyle: { color: '#3b82f6' }
  }]
})

const funnelOptions = reactive<EChartsOption>({
  tooltip: { trigger: 'item', formatter: '{b}: {c}' },
  series: [
    {
      name: '转化漏斗',
      type: 'funnel',
      left: '10%',
      top: 20,
      bottom: 20,
      width: '80%',
      sort: 'descending',
      label: { show: true, position: 'inside' },
      data: []
    }
  ]
})

const pieOptions = reactive<EChartsOption>({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: '5%', left: 'center' },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: []
    }
  ]
})

const fetchData = async () => {
  if (!props.asin) return
  loading.value = true
  try {
    const data = await request.get({ url: `/amz/performance/asin-stats`, params: { asin: props.asin } })
    rawData.value = data || []
    updateLineChart()
    updateFunnelChart()
    updatePieChart()
  } catch (error) {
    console.error('Failed to fetch performance stats:', error)
  } finally {
    loading.value = false
  }
}

const updateLineChart = () => {
  lineOptions.xAxis.data = rawData.value.map(i => i.startDate)
  lineOptions.series[0].data = rawData.value.map(i => i[trendMetric.value] || 0)
}

const updateFunnelChart = () => {
  const totals = {
    impression: rawData.value.reduce((sum, i) => sum + (i.impressionCount || 0), 0),
    click: rawData.value.reduce((sum, i) => sum + (i.clickCount || 0), 0),
    cartAdd: rawData.value.reduce((sum, i) => sum + (i.cartAddCount || 0), 0),
    purchase: rawData.value.reduce((sum, i) => sum + (i.purchaseCount || 0), 0)
  }
  
  funnelOptions.series[0].data = [
    { value: totals.impression, name: '展示', key: 'impression' },
    { value: totals.click, name: '点击', key: 'click' },
    { value: totals.cartAdd, name: '加购', key: 'cartAdd' },
    { value: totals.purchase, name: '购买', key: 'purchase' }
  ]
}

const updatePieChart = () => {
  const stage = currentFunnelStage.value
  const data = rawData.value
  
  const metrics = {
    sameDay: data.reduce((sum, i) => sum + (i[`sameDayShipping${capitalize(stage)}Count`] || 0), 0),
    oneDay: data.reduce((sum, i) => sum + (i[`oneDayShipping${capitalize(stage)}Count`] || 0), 0),
    twoDay: data.reduce((sum, i) => sum + (i[`twoDayShipping${capitalize(stage)}Count`] || 0), 0)
  }

  pieOptions.series[0].data = [
    { value: metrics.sameDay, name: 'Same Day' },
    { value: metrics.oneDay, name: '1-Day' },
    { value: metrics.twoDay, name: '2-Day' }
  ]
}

const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1)

const handleFunnelClick = (params: any) => {
  if (params.data && params.data.key) {
    currentFunnelStage.value = params.data.key
    updatePieChart()
  }
}

watch(trendMetric, updateLineChart)
watch(() => props.asin, fetchData)

onMounted(fetchData)
</script>

<style scoped lang="scss">
.performance-stats {
  background-color: #f8fafc;
  min-height: 100%;
}
</style>
