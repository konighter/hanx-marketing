<template>
  <div class="ad-campaign-data-analysis" v-loading="loading">
    <!-- 0. 全局时间和颗粒度筛选器 -->
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
    </div>

    <!-- 1. 核心指标看板 (Scorecard) -->
    <div class="scorecard-container grid grid-cols-4 lg:grid-cols-7 gap-4 mb-20px">
      <el-card shadow="hover" class="metric-card" v-for="metric in summaryMetrics" :key="metric.key" :body-style="{ padding: '15px' }">
        <div class="text-13px text-gray-500 mb-2 flex items-center justify-between">
          {{ metric.name }}
          <el-tooltip v-if="metric.desc" :content="metric.desc" placement="top">
            <Icon icon="ep:question-filled" class="text-gray-300 cursor-help text-14px" />
          </el-tooltip>
        </div>
        <div class="text-22px font-bold mb-1">{{ metric.prefix || '' }}{{ metric.displayValue }}{{ metric.suffix || '' }}</div>
      </el-card>
    </div>

    <!-- 2. 趋势图 & 漏斗图 -->
    <el-row :gutter="20" class="mb-20px">
      <!-- Trend Chart -->
      <el-col :span="16">
        <div class="chart-wrapper-border h-full">
          <AdDataChart
            :shop-id="shopId"
            :account-id="accountId"
            entity-type="CAMPAIGN"
            :query-params="{ campaignIds: [externalId] }"
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
            <span class="font-bold text-15px">转化漏斗</span>
          </template>
          <div ref="funnelChartRef" class="w-full h-300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 广告组维度下钻表格 -->
    <el-card shadow="never" class="border-gray-200" v-loading="tableLoading">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold text-15px">广告组表现明细</span>
          <div class="flex items-center gap-2">
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
        :cell-style="getCellStyle"
        :header-cell-style="{ background: '#f8f9fa', color: '#333' }"
      >
        <el-table-column prop="name" label="广告组" min-width="280" fixed="left">
          <template #default="scope">
            <div class="flex items-center">
              <span class="level-tag adgroup mr-2">广告组</span>
              <span class="truncate font-medium" :title="scope.row.name">{{ scope.row.name || scope.row.id }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="spend" label="花费 (Spend)" width="130" align="right" sortable>
          <template #default="scope">
            <span class="font-medium">${{ formatNumber(scope.row.spend) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="impressions" label="展现量" width="110" align="right" sortable>
          <template #default="scope">
            {{ formatNumber(scope.row.impressions, 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="clicks" label="点击量" width="100" align="right" sortable>
          <template #default="scope">
            {{ formatNumber(scope.row.clicks, 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="ctr" label="CTR" width="90" align="right" sortable>
          <template #default="scope">
            {{ scope.row.ctr != null ? `${Number(scope.row.ctr).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="cpc" label="CPC" width="100" align="right" sortable>
          <template #default="scope">
            <span class="font-medium">${{ formatNumber(scope.row.cpc) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orders" label="订单数" width="100" align="right" sortable>
          <template #default="scope">
            {{ formatNumber(scope.row.orders, 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销售额" width="130" align="right" sortable>
          <template #default="scope">
            ${{ formatNumber(scope.row.sales) }}
          </template>
        </el-table-column>
        <el-table-column prop="acos" label="ACOS" width="90" align="right" sortable>
          <template #default="scope">
            {{ scope.row.acos != null ? `${Number(scope.row.acos).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="roas" label="ROAS" width="90" align="center" sortable>
          <template #default="scope">
            <span class="font-bold">{{ scope.row.roas != null ? Number(scope.row.roas).toFixed(2) : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作建议" width="140" align="center" fixed="right">
          <template #default="scope">
             <el-tag v-if="scope.row.roas >= 6" type="success" effect="light" size="small">🚀 增加预算</el-tag>
             <el-tag v-else-if="scope.row.acos > 40" type="danger" effect="light" size="small">⚠️ 暂停/优化</el-tag>
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
import { AdsReportApi, type AdsReportRow } from '@/app/erplus/api/adv/report'

const props = defineProps<{
  shopId?: number
  accountId?: number
  campaignId: number
  externalId: string
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
const scorecardData = ref<Record<string, any>>({})
const tableData = ref<any[]>([])
const funnelChartRef = ref<HTMLDivElement | null>(null)
let funnelChartInstance: echarts.ECharts | null = null

// ==================== 工具函数 ====================

/** 从 OLAP Row 的 metrics 列表中提取为扁平 Map */
function metricsToMap(metrics: { key: string; value: any }[]): Record<string, any> {
  const map: Record<string, any> = {}
  metrics?.forEach(m => { map[m.key] = m.value })
  return map
}

/** 从 OLAP Row 的 dimensions 列表中提取单个维度值 */
function getDimValue(row: AdsReportRow, key: string): string | undefined {
  return row.dimensions?.find(d => d.key === key)?.value
}

/** 从 OLAP Row 的 dimensions 列表中提取 label */
function getDimLabel(row: AdsReportRow, key: string): string | undefined {
  return row.dimensions?.find(d => d.key === key)?.label
}

function formatNumber(val: any, decimals = 2): string {
  if (val == null || val === '') return '0'
  const n = Number(val)
  if (isNaN(n)) return '0'
  return n.toLocaleString(undefined, { minimumFractionDigits: decimals, maximumFractionDigits: decimals })
}

// ==================== 1. Scorecard 数据 ====================

const summaryMetrics = computed(() => {
  const d = scorecardData.value
  return [
    { key: 'spend', name: '花费', prefix: '$', displayValue: formatNumber(d.spend), desc: '广告总花费' },
    { key: 'impressions', name: '展现量', displayValue: formatNumber(d.impressions, 0), desc: '广告被展示的次数' },
    { key: 'clicks', name: '点击量', displayValue: formatNumber(d.clicks, 0), desc: '广告被点击的次数' },
    { key: 'orders', name: '订单量', displayValue: formatNumber(d.orders, 0), desc: '广告带来的订单数' },
    { key: 'sales', name: '销售额', prefix: '$', displayValue: formatNumber(d.sales), desc: '广告带来的销售额' },
    { key: 'acos', name: 'ACOS', suffix: '%', displayValue: d.acos != null ? Number(d.acos).toFixed(2) : '-', desc: '广告花费占销售额比例' },
    { key: 'roas', name: 'ROAS', displayValue: d.roas != null ? Number(d.roas).toFixed(2) : '-', desc: '广告投入产出比' },
  ]
})

/**
 * 使用 queryAdsReport 不传 dimensions → 返回 summary 汇总行
 */
const loadScorecardData = async () => {
  if (!props.shopId) return
  try {
    const res = await AdsReportApi.queryAdsReport({
      shopId: props.shopId,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      campaignIds: [props.externalId],
      metrics: ['impressions', 'clicks', 'spend', 'sales', 'orders']
      // 不传 dimensions → 后端返回需要 summary 汇总
    })
    // 优先从 summary 取，如果后端没填 summary 则从 rows[0] 取
    const sourceRow = res.summary || res.rows?.[0]
    if (sourceRow?.metrics) {
      scorecardData.value = metricsToMap(sourceRow.metrics)
    } else {
      scorecardData.value = {}
    }
  } catch (error) {
    console.error('加载 Scorecard 数据失败:', error)
  }
}

// ==================== 2. 下钻表格 ====================

/**
 * 使用 queryAdsReport 按 ad_group_id 维度分组查询
 */
const loadDrilldownData = async () => {
  if (!props.shopId) return
  tableLoading.value = true
  try {
    const res = await AdsReportApi.queryAdsReport({
      shopId: props.shopId,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      campaignIds: [props.externalId],
      dimensions: ['ad_group_id'],
      metrics: ['impressions', 'clicks', 'spend', 'sales', 'orders'],
      orderBy: 'spend',
      isAsc: false
    })
    // 将 OLAP rows 转换为表格行
    tableData.value = (res.rows || []).map((row: AdsReportRow) => {
      const metrics = metricsToMap(row.metrics)
      const adGroupId = getDimValue(row, 'ad_group_id')
      const adGroupLabel = getDimLabel(row, 'ad_group_id')
      return {
        id: adGroupId,
        name: adGroupLabel || `广告组 #${adGroupId}`,
        level: 'adGroup',
        ...metrics // spend, impressions, clicks, sales, orders, roas, cpc, ctr, acos 等
      }
    })
  } catch (error) {
    console.error('加载下钻数据失败:', error)
  } finally {
    tableLoading.value = false
  }
}

// ==================== 3. 漏斗图 ====================

const renderFunnelChart = () => {
  if (!funnelChartRef.value) return
  if (!funnelChartInstance) {
    funnelChartInstance = echarts.init(funnelChartRef.value)
  }

  const d = scorecardData.value
  const impressions = Number(d.impressions || 0)
  const clicks = Number(d.clicks || 0)
  const orders = Number(d.orders || 0)

  const option = {
    tooltip: {
      trigger: 'item' as const,
      formatter: '{b}: {c}'
    },
    color: ['#409EFF', '#67C23A', '#F56C6C'],
    series: [
      {
        name: '转化漏斗',
        type: 'funnel',
        left: '10%',
        width: '80%',
        sort: 'descending' as const,
        gap: 2,
        label: {
          show: true,
          position: 'inside' as const,
          formatter: (params: any) => {
            return `${params.name}\n${params.value.toLocaleString()}`
          }
        },
        labelLine: {
          length: 10,
          lineStyle: { width: 1, type: 'solid' as const }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: { fontSize: 16 }
        },
        data: [
          { value: impressions, name: '展现' },
          { value: clicks, name: '点击' },
          { value: orders, name: '下单' }
        ]
      }
    ]
  }
  funnelChartInstance.setOption(option, { notMerge: true })
}

const handleResize = () => {
  if (funnelChartInstance) {
    funnelChartInstance.resize()
  }
}

// ==================== 4. 热力表格样式 ====================

const getCellStyle = ({ row, column }: any) => {
  if (column.property === 'roas') {
    const roas = Number(row.roas)
    if (roas >= 6) return { backgroundColor: 'rgba(103, 194, 58, 0.15)', color: '#137333' }
    if (roas > 0 && roas < 3) return { backgroundColor: 'rgba(245, 108, 108, 0.1)', color: '#c5221f' }
  }
  if (column.property === 'acos') {
    const acos = Number(row.acos)
    if (acos > 40) return { backgroundColor: 'rgba(245, 108, 108, 0.15)', color: '#c5221f' }
    if (acos > 0 && acos < 20) return { backgroundColor: 'rgba(103, 194, 58, 0.1)', color: '#137333' }
  }
  return {}
}

// ==================== 5. 生命周期 ====================

const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadScorecardData(),
      loadDrilldownData()
    ])
    // 漏斗图依赖 scorecard 数据，等数据返回后再渲染
    nextTick(() => {
      renderFunnelChart()
    })
  } finally {
    loading.value = false
  }
}

watch(
  () => [props.shopId, props.accountId, props.externalId, dateRange.value, timeUnit.value],
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

/* 包含 AdDataChart 的外部容器 */
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
