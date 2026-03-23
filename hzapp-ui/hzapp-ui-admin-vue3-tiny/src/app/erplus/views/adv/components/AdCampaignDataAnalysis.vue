<template>
  <div class="ad-campaign-data-analysis" v-loading="loading">
    <!-- 0. 全局时间和颗粒度大盘筛选器 (Sticky Filters) -->
    <div class="global-filters sticky top-0 z-10 bg-white pb-15px mb-15px border-b border-gray-100 flex justify-between items-center">
      <div class="flex items-center gap-4">
        <span class="font-bold text-16px text-gray-800">广告表现分析</span>
        <el-date-picker 
          v-model="dateRange" 
          type="daterange" 
          range-separator="至"
          start-placeholder="开始日期" 
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :clearable="false"
          class="w-260px" 
        />
        <el-radio-group v-model="timeUnit" size="default">
          <el-radio-button label="day">按天</el-radio-button>
          <el-radio-button label="week">按周</el-radio-button>
          <el-radio-button label="month">按月</el-radio-button>
        </el-radio-group>
      </div>
      <el-button type="primary" plain @click="loadDrilldownData">
        <Icon icon="ep:download" class="mr-1" /> 导出分析报告
      </el-button>
    </div>

    <!-- 1. 核心指标看板 (The Scorecard) -->
    <div class="scorecard-container grid grid-cols-6 gap-4 mb-20px">
      <el-card shadow="hover" class="metric-card" v-for="metric in summaryMetrics" :key="metric.key" :body-style="{ padding: '15px' }">
        <div class="text-13px text-gray-500 mb-2">{{ metric.name }}</div>
        <div class="text-22px font-bold mb-2">{{ metric.prefix || '' }}{{ metric.value }}</div>
        <div class="text-12px flex items-center font-medium" :class="getTrendColorClass(metric)">
          <Icon :icon="metric.trend >= 0 ? 'ep:top-right' : 'ep:bottom-right'" class="mr-1" />
          <span>{{ Math.abs(metric.trend) }}% (环比)</span>
        </div>
      </el-card>
    </div>

    <!-- 2 & 4. 趋势对比图 & 漏斗转化分析 (Side by side for compact view) -->
    <el-row :gutter="20" class="mb-20px">
      <!-- Trend Chart -->
      <el-col :span="16">
        <div class="chart-wrapper-border h-full">
          <AdDataChart
            :account-id="accountId"
            entity-type="CAMPAIGN"
            :query-params="{ campaignIds: [campaignId] }"
            :date-range="dateRange"
            :time-unit="timeUnit"
            class="!mt-0 border-none shadow-none h-full"
          />
        </div>
      </el-col>
      <!-- Funnel Chart -->
      <el-col :span="8">
        <el-card shadow="never" class="h-full border-gray-200">
          <template #header>
            <span class="font-bold text-15px">漏斗转化分析 (Funnel)</span>
          </template>
          <div ref="funnelChartRef" class="w-full h-300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 多维度下钻热力表格 (Heatmap Table) -->
    <el-card shadow="never" class="border-gray-200" v-loading="tableLoading">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold text-15px">多维度下钻表现 (Heatmap)</span>
          <div class="flex items-center gap-2">
            <!-- 模拟全局筛选器、自定义表头占位 -->
            <el-button type="primary" link @click="loadDrilldownData">
              <Icon icon="ep:refresh" class="mr-1" /> 刷新数据
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        row-key="id"
        border
        style="width: 100%"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :cell-style="getCellStyle"
        :header-cell-style="{ background: '#f8f9fa', color: '#333' }"
      >
        <el-table-column prop="name" label="维度 (广告组/广告)" min-width="280" fixed="left">
          <template #default="scope">
            <div class="flex items-center">
              <span v-if="scope.row.level === 'campaign'" class="level-tag campaign mr-2">活动</span>
              <span v-else-if="scope.row.level === 'adGroup'" class="level-tag adgroup mr-2">广告组</span>
              <span v-else class="level-tag ad mr-2">广告</span>
              
              <!-- 模拟素材缩略图 -->
              <div v-if="scope.row.level === 'ad'" class="w-30px h-30px bg-gray-100 rounded-sm mr-2 flex items-center justify-center overflow-hidden border border-gray-200">
                <Icon icon="ep:picture" class="text-gray-400" />
              </div>
              
              <span class="truncate font-medium" :title="scope.row.name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.AD_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="spend" label="消耗 (Spend)" width="120" align="right" sortable>
          <template #default="scope">
            <span class="font-medium">{{ scope.row.spend ? `¥${scope.row.spend}` : '¥0' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="impressions" label="展现量" width="110" align="right" sortable />
        <el-table-column prop="clicks" label="点击量" width="100" align="right" sortable />
        <el-table-column prop="ctr" label="CTR" width="100" align="right" sortable>
          <template #default="scope">
            {{ scope.row.ctr ? `${(scope.row.ctr * 100).toFixed(2)}%` : '0%' }}
          </template>
        </el-table-column>
        <el-table-column prop="cpc" label="转化成本 (CPA/CPC)" width="170" align="right" sortable>
          <template #default="scope">
            <!-- 警报图标 -->
            <el-tooltip v-if="scope.row.cpc > 0.8" content="成本偏高，建议优化" placement="top">
              <Icon icon="ep:warning" class="text-red-500 mr-1 align-text-bottom" />
            </el-tooltip>
            <span class="font-medium">{{ scope.row.cpc ? `¥${scope.row.cpc.toFixed(2)}` : '¥0' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orders" label="转化数" width="100" align="right" sortable />
        <el-table-column prop="sales" label="销售额" width="140" align="right" sortable>
          <template #default="scope">
            {{ scope.row.sales ? `¥${scope.row.sales}` : '¥0' }}
          </template>
        </el-table-column>
        <el-table-column prop="roas" label="ROI / ROAS" width="120" align="center" sortable>
          <template #default="scope">
            <span class="font-bold">{{ scope.row.roas ? scope.row.roas.toFixed(2) : '0' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作建议" width="140" align="center" fixed="right">
          <template #default="scope">
             <el-tag v-if="scope.row.roas >= 6" type="success" effect="light" size="small">🚀 增加预算</el-tag>
             <el-tag v-else-if="scope.row.cpc > 0.8" type="danger" effect="light" size="small">⚠️ 暂停/优化</el-tag>
             <el-tag v-else type="info" effect="light" size="small">➖ 保持观察</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import AdDataChart from './AdDataChart.vue'
import { DICT_TYPE } from '@/app/erplus/common/dict'
import { AdsReportApi } from '@/app/erplus/api/adv/report'

const props = defineProps<{
  accountId?: number
  campaignId: number
}>()

const getDefaultDateRange = (): [string, string] => {
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 14)
  return [
      startDate.toISOString().split('T')[0],
      endDate.toISOString().split('T')[0]
  ]
}

const dateRange = ref<[string, string]>(getDefaultDateRange())
const timeUnit = ref<'day' | 'week' | 'month'>('day')
const loading = ref(false)
const tableLoading = ref(false)
const scorecardData = ref<any>({})
const tableData = ref<any[]>([])
const funnelChartRef = ref<HTMLDivElement | null>(null)
let funnelChartInstance: echarts.ECharts | null = null

// 1. Core Metrics Summary Data (Computed from real data)
const summaryMetrics = computed(() => {
  const data = scorecardData.value
  return [
    { key: 'spend', name: '消耗 (Spend)', value: data.spend?.toLocaleString() || '0', prefix: '¥', trend: data.trends?.spend || 0, inverseGood: true },
    { key: 'impressions', name: '展现量 (Imp)', value: data.impressions?.toLocaleString() || '0', trend: data.trends?.impressions || 0, inverseGood: false },
    { key: 'clicks', name: '点击量 (Clicks)', value: data.clicks?.toLocaleString() || '0', trend: data.trends?.clicks || 0, inverseGood: false },
    { key: 'orders', name: '转化量 (Conv)', value: data.orders?.toLocaleString() || '0', trend: data.trends?.orders || 0, inverseGood: false },
    { key: 'cpc', name: '转化成本 (CPC)', value: data.cpc || '0', prefix: '¥', trend: data.trends?.cpc || 0, inverseGood: true },
    { key: 'roas', name: 'ROI / ROAS', value: data.roas || '0', trend: data.trends?.roas || 0, inverseGood: false },
  ]
})

const loadScorecardData = async () => {
  if (!props.accountId || !props.campaignId) return
  try {
    const data = await AdsReportApi.getPerformanceScorecard({
      accountId: props.accountId,
      entityType: 'CAMPAIGN',
      entityId: props.campaignId,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      timeUnit: timeUnit.value
    })
    scorecardData.value = data
  } catch (error) {
    console.error('加载 Scorecard 数据失败:', error)
  }
}

const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadScorecardData(),
      loadDrilldownData()
    ])
  } finally {
    loading.value = false
  }
}

const getTrendColorClass = (metric: any) => {
  if (metric.trend > 0) {
    return metric.inverseGood ? 'text-red-500' : 'text-green-500'
  } else if (metric.trend < 0) {
    return metric.inverseGood ? 'text-green-500' : 'text-red-500'
  }
  return 'text-gray-400'
}

// 4. Heatmap Style Logic
const getCellStyle = ({ row, column }: any) => {
  // 红色预警/绿色健康的背景热力图效果
  if (column.property === 'roas') {
    if (row.roas >= 6) return { backgroundColor: 'rgba(103, 194, 58, 0.15)', color: '#137333' }
    if (row.roas < 4) return { backgroundColor: 'rgba(245, 108, 108, 0.1)', color: '#c5221f' }
  }
  if (column.property === 'cpc') {
    if (row.cpc > 0.8) return { backgroundColor: 'rgba(245, 108, 108, 0.15)', color: '#c5221f' }
    if (row.cpc < 0.5) return { backgroundColor: 'rgba(103, 194, 58, 0.1)', color: '#137333' }
  }
  return {}
}

const handleDateRangeUpdate = (dates: [string, string]) => {
  if (dates[0] !== dateRange.value[0] || dates[1] !== dateRange.value[1]) {
    dateRange.value = dates
    loadDrilldownData()
  }
}

// Render Funnel Chart
const renderFunnelChart = () => {
  if (!funnelChartRef.value) return
  if (!funnelChartInstance) {
    funnelChartInstance = echarts.init(funnelChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c}'
    },
    color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C'],
    series: [
      {
        name: '转化漏斗',
        type: 'funnel',
        left: '10%',
        width: '80%',
        sort: 'descending',
        gap: 2,
        label: {
          show: true,
          position: 'inside',
          formatter: '{b}: {c}'
        },
        labelLine: {
          length: 10,
          lineStyle: { width: 1, type: 'solid' }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: { fontSize: 16 }
        },
        data: [
          { value: 154200, name: '展现' },
          { value: 8560, name: '点击' },
          { value: 1200, name: '到访 (会话)' },
          { value: 425, name: '下单' }
        ]
      }
    ]
  }
  funnelChartInstance.setOption(option)
}

const handleResize = () => {
  if (funnelChartInstance) {
    funnelChartInstance.resize()
  }
}

const loadDrilldownData = async () => {
  if (!props.accountId || !props.campaignId) return
  tableLoading.value = true
  try {
    const data = await AdsReportApi.getPerformanceDrilldown({
      accountId: props.accountId,
      entityType: 'CAMPAIGN',
      entityId: props.campaignId,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      timeUnit: timeUnit.value
    })
    tableData.value = data
    
    nextTick(() => {
      renderFunnelChart()
    })
  } catch (error) {
    console.error('加载下钻数据失败:', error)
  } finally {
    tableLoading.value = false
  }
}

watch(
  () => [props.campaignId, dateRange.value, timeUnit.value],
  () => {
    loadAllData()
  }
)

onMounted(() => {
  loadAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (funnelChartInstance) {
    funnelChartInstance.dispose()
    funnelChartInstance = null
  }
})
</script>

<style scoped>
.ad-campaign-data-analysis {
  display: flex;
  flex-direction: column;
}

/* Metric Card */
.metric-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}
.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* 包含 AdDataChart 的外部容器模拟 Card 样式，由于 AdDataChart 内部带有 el-card，为了结构统一稍微包裹一下边框即可 */
.chart-wrapper-border {
  border-radius: 4px;
  border: 1px solid var(--el-border-color-light);
  background-color: #fff;
  overflow: hidden;
}

/* Level Tags */
.level-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  color: #fff;
  font-weight: bold;
}
.level-tag.campaign {
  background-color: #409eff;
}
.level-tag.adgroup {
  background-color: #67c23a;
}
.level-tag.ad {
  background-color: #e6a23c;
}

/* Table transitions & hovers */
:deep(.el-table__row) {
  transition: background-color 0.2s;
}
:deep(.el-table__row:hover > td) {
  opacity: 0.95;
}
</style>
