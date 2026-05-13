<template>
  <el-drawer
    v-model="visible"
    title="订单统计"
    size="70%"
    direction="rtl"
    @opened="onOpened"
    @close="onClose"
  >
    <template #header>
      <div class="flex items-center">
        <span class="text-lg font-bold">订单统计</span>
      </div>
    </template>
    <div v-if="contentReady" v-loading="loading" class="p-6 flex flex-col gap-6 pt-2">
      <!-- Filter Section -->
      <div class="mb-2">
        <el-form :inline="true" class="stats-filter-form">
          <el-form-item label="时间范围:">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              unlink-panels
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="shortcuts"
              value-format="x"
              style="width: 240px;"
              @change="fetchData"
            />
          </el-form-item>
          <el-form-item label="产品编码:">
            <el-input 
              v-model="productCode" 
              placeholder="请输入产品编码" 
              clearable 
              style="width: 180px;" 
              @change="fetchData"
              @clear="fetchData"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- Charts Section -->
      <div class="bg-white rounded-xl border p-4 pb-0">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-4">
            <h3 class="text-lg font-bold flex items-center gap-2 text-gray-800">
              <Icon :icon="chartType === 'map' ? 'ep:location' : 'ep:histogram'" class="text-blue-500" />
              订单分布统计
            </h3>
            <el-radio-group v-model="chartType" size="small">
              <el-radio-button label="map">地图模式</el-radio-button>
              <el-radio-button label="bar">柱状图模式</el-radio-button>
            </el-radio-group>
          </div>
          <div class="text-sm text-gray-500">数据来源: 订单地址统计</div>
        </div>

        <!-- GeoMap View -->
        <div v-if="chartType === 'map'">
          <div v-if="geoReady" style="height: 500px;">
            <Echart :options="mapOptions" height="500px" />
          </div>
          <div v-else class="flex items-center justify-center" style="height: 500px;">
            <span class="text-gray-400">地图加载中...</span>
          </div>
        </div>

        <!-- Bar Chart View -->
        <div v-else :style="{ height: chartHeight + 'px' }">
          <Echart :options="barOptions" />
        </div>
      </div>

      <!-- Table Section -->
      <div class="bg-white rounded-xl border p-4">
        <h3 class="text-lg font-bold flex items-center gap-2 text-gray-800 mb-4">
          <Icon icon="ep:expand" class="text-blue-500" />
          州订单排名
        </h3>
        <el-table :data="statsData" border stripe style="width: 100%" v-loading="loading">
          <el-table-column label="国家" prop="countryCode" width="100" align="center" />
          <el-table-column label="州/省" prop="stateOrRegion" min-width="120" />
          <el-table-column label="订单数" prop="count" align="center" sortable width="120">
            <template #default="{ row }">
              <span class="font-bold text-blue-600">{{ row.count }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { EChartsOption } from 'echarts'
import echarts from '@/plugins/echarts'
import Echart from '@/components/Echart/src/Echart.vue'
import request from '@/config/axios'
import usaGeoJson from '@/app/erplus/common/geomap/usa.json'

const visible = ref(false)
const loading = ref(false)
const statsData = ref<any[]>([])
const contentReady = ref(false)
const currentShopId = ref<number | null>(null)
const chartHeight = ref(400)
const dateRange = ref<[number, number] | null>(null)
const productCode = ref('')
const geoReady = ref(false)
const chartType = ref<'map' | 'bar'>('map')

// Register USA map on load
const registerMap = () => {
  if (!geoReady.value) {
    echarts.registerMap('USA', usaGeoJson as any, {
      Alaska: {
        left: -131,
        top: 25,
        width: 15
      },
      Hawaii: {
        left: -110,
        top: 28,
        width: 5
      },
      'Puerto Rico': {
        left: -76,
        top: 26,
        width: 2
      }
    })
    geoReady.value = true
  }
}

// US state abbreviation -> full name mapping for GeoJSON matching
const STATE_MAP: Record<string, string> = {
  AL: 'Alabama', AK: 'Alaska', AZ: 'Arizona', AR: 'Arkansas', CA: 'California',
  CO: 'Colorado', CT: 'Connecticut', DE: 'Delaware', FL: 'Florida', GA: 'Georgia',
  HI: 'Hawaii', ID: 'Idaho', IL: 'Illinois', IN: 'Indiana', IA: 'Iowa',
  KS: 'Kansas', KY: 'Kentucky', LA: 'Louisiana', ME: 'Maine', MD: 'Maryland',
  MA: 'Massachusetts', MI: 'Michigan', MN: 'Minnesota', MS: 'Mississippi', MO: 'Missouri',
  MT: 'Montana', NE: 'Nebraska', NV: 'Nevada', NH: 'New Hampshire', NJ: 'New Jersey',
  NM: 'New Mexico', NY: 'New York', NC: 'North Carolina', ND: 'North Dakota', OH: 'Ohio',
  OK: 'Oklahoma', OR: 'Oregon', PA: 'Pennsylvania', RI: 'Rhode Island', SC: 'South Carolina',
  SD: 'South Dakota', TN: 'Tennessee', TX: 'Texas', UT: 'Utah', VT: 'Vermont',
  VA: 'Virginia', WA: 'Washington', WV: 'West Virginia', WI: 'Wisconsin', WY: 'Wyoming',
  DC: 'District of Columbia'
}

const shortcuts = [
  {
    text: '昨天',
    value: () => {
      const end = new Date()
      end.setHours(0, 0, 0, 0)
      const start = new Date()
      start.setHours(0, 0, 0, 0)
      start.setTime(start.getTime() - 3600 * 1000 * 24)
      return [start.getTime(), end.getTime() - 1]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start.getTime(), end.getTime()]
    }
  },
  {
    text: '上一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      // Get previous Monday
      const day = start.getDay() || 7 // 1 (Mon) to 7 (Sun)
      start.setTime(start.getTime() - 3600 * 1000 * 24 * (day + 6))
      const lastWeekEnd = new Date(start)
      lastWeekEnd.setTime(start.getTime() + 3600 * 1000 * 24 * 6)
      lastWeekEnd.setHours(23, 59, 59, 999)
      return [start.getTime(), lastWeekEnd.getTime()]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start.getTime(), end.getTime()]
    }
  }
]

const setDefaultRange = () => {
  const end = new Date()
  const start = new Date()
  start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
  dateRange.value = [start.getTime(), end.getTime()]
}

// Map chart options
const mapOptions = reactive<EChartsOption>({
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      const val = params.value || 0
      return `${params.name}<br/>订单量: <b>${val}</b>`
    }
  },
  visualMap: {
    min: 0,
    max: 10,
    left: 'left',
    top: 'bottom',
    text: ['多', '少'],
    inRange: {
      color: ['#e0f2fe', '#7dd3fc', '#38bdf8', '#0284c7', '#075985']
    },
    calculable: true
  },
  series: [
    {
      name: '订单量',
      type: 'map',
      map: 'USA',
      roam: true,
      emphasis: {
        label: { show: true },
        itemStyle: { areaColor: '#fbbf24' }
      },
      data: []
    }
  ]
})

// Bar chart options
const barOptions = reactive<EChartsOption>({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '0',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    boundaryGap: [0, 0.01]
  },
  yAxis: {
    type: 'category',
    data: []
  },
  series: [
    {
      name: '订单量',
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#3b82f6',
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right'
      }
    }
  ]
})

const open = (shopId: number) => {
  currentShopId.value = shopId
  setDefaultRange()
  visible.value = true
}

const onOpened = () => {
  contentReady.value = true
  registerMap()
  fetchData()
}

const onClose = () => {
  contentReady.value = false
}

const toFullName = (abbr: string): string => {
  // Try direct match first (e.g. "CA" -> "California")
  const upper = abbr.toUpperCase().trim()
  if (STATE_MAP[upper]) return STATE_MAP[upper]
  // If already a full name (e.g. "Michigan"), return as-is
  const fullNames = Object.values(STATE_MAP)
  const found = fullNames.find(n => n.toLowerCase() === abbr.toLowerCase().trim())
  if (found) return found
  return abbr
}

const fetchData = async () => {
  if (!currentShopId.value) return
  loading.value = true
  try {
    const params: any = { shopId: currentShopId.value }
    if (productCode.value) {
      params.platformProductCode = productCode.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.orderTimeStart = dateRange.value[0]
      params.orderTimeEnd = dateRange.value[1]
    }

    const data = await request.post({ 
      url: '/erplus/order/cross-order/state-stats',
      data: params
    })
    statsData.value = data || []
    
    // Aggregate by full state name for map (merge abbreviation + full name duplicates)
    const stateAgg: Record<string, number> = {}
    statsData.value.forEach(item => {
      const fullName = toFullName(item.stateOrRegion || '未知')
      stateAgg[fullName] = (stateAgg[fullName] || 0) + (item.count || 0)
    })

    // Update map data
    const mapData = Object.entries(stateAgg).map(([name, value]) => ({ name, value }))
    mapOptions.series[0].data = mapData
    const maxVal = Math.max(...mapData.map(d => d.value), 1)
    mapOptions.visualMap.max = maxVal

    // Update bar chart
    const sortedData = [...statsData.value].sort((a, b) => (a.count || 0) - (b.count || 0))
    const yData = sortedData.map(i => i.stateOrRegion || '未知')
    const sData = sortedData.map(i => i.count || 0)
    barOptions.yAxis.data = yData
    barOptions.series[0].data = sData
    
    // Dynamic height based on number of states (max compression)
    chartHeight.value = Math.max(150, yData.length * 22 + 5)
  } catch (error) {
    console.error('Failed to fetch order stats:', error)
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.stats-hero {
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}
</style>

