<template>
  <div class="ad-account-data-analysis" v-loading="loading">
    <!-- 1. 全局筛选器 (Date & Granularity) -->
    <div class="flex items-center justify-between mb-20px bg-white p-15px rounded-8px shadow-sm border border-gray-100 sticky top-0 z-10">
      <div class="flex items-center gap-4">
        <span class="font-bold text-16px">账户概览</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :clearable="false"
          class="!w-260px"
          @change="loadData"
        />
        <el-radio-group v-model="timeUnit" size="default" @change="loadData">
          <el-radio-button label="day">按天</el-radio-button>
          <el-radio-button label="week">按周</el-radio-button>
          <el-radio-button label="month">按月</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="flex items-center gap-2">
        <!-- 指标定制 -->
        <el-popover placement="bottom-end" :width="200" trigger="click">
          <template #reference>
            <el-button type="primary" plain size="small">
              <Icon icon="ep:setting" class="mr-1" /> 定制指标
            </el-button>
          </template>
          <div class="p-2">
            <div class="font-bold mb-2">显示指标卡片</div>
            <el-checkbox-group v-model="visibleMetrics" class="flex flex-col gap-2">
              <el-checkbox v-for="m in allMetrics" :key="m.key" :label="m.key">
                {{ m.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-popover>
      </div>
    </div>

    <!-- 2. 核心指标看板 (Scorecard) -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-7 gap-4 mb-20px">
      <el-card 
        v-for="metric in displayedMetrics" 
        :key="metric.key" 
        shadow="hover" 
        class="metric-card border-none ring-1 ring-gray-100"
        :body-style="{ padding: '15px' }"
      >
        <div class="flex flex-col h-full justify-between">
          <div class="text-gray-500 text-13px mb-2 flex items-center justify-between">
            {{ metric.label }}
            <el-tooltip :content="metric.desc" placement="top">
              <Icon icon="ep:question-filled" class="text-gray-300 cursor-help" />
            </el-tooltip>
          </div>
          <div class="text-22px font-bold text-gray-800 mb-2">
            {{ metric.prefix }}{{ metric.value }}{{ metric.suffix }}
          </div>
          <div class="flex items-center text-12px mt-1">
            <span class="text-gray-400 mr-2">环比</span>
            <span :class="metric.trend >= 0 ? 'text-green-500' : 'text-red-500'" class="flex items-center font-medium">
              <Icon :icon="metric.trend >= 0 ? 'ep:caret-top' : 'ep:caret-bottom'" />
              {{ Math.abs(metric.trend) }}%
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 3. 趋势图 (默认折叠) -->
    <el-collapse v-model="activeCollapse">
      <el-collapse-item name="trend" class="trend-collapse">
        <template #title>
          <div class="flex items-center gap-2 font-bold px-4">
            <Icon icon="ep:histogram" class="text-primary" />
            数据趋势图
          </div>
        </template>
        <div class="p-4 bg-white rounded-b-8px">
          <AdDataChart
            :account-id="accountId"
            entity-type="ACCOUNT"
            :date-range="dateRange"
            :time-unit="timeUnit"
            class="!mt-0 border-none shadow-none"
          />
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup lang="ts">
import AdDataChart from './AdDataChart.vue'
import { ref, computed, watch } from 'vue'
import { AdsReportApi } from '@/app/erplus/api/adv/report'

const props = defineProps<{
  accountId?: number
}>()

const loading = ref(false)
const activeCollapse = ref<string[]>([]) // 默认不展开
const timeUnit = ref<'day' | 'week' | 'month'>('day')

// 日期范围初始化 (最近14天)
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

// 核心指标池
const scorecardData = ref<any>({})

// 指标定义
const allMetrics = computed(() => {
  const data = scorecardData.value
  return [
    { key: 'spend', label: '总消耗', prefix: '$', suffix: '', desc: '选定时间段内的广告消耗总额', value: data.spend?.toLocaleString() || '0', trend: data.trends?.spend || 0 },
    { key: 'impressions', label: '展现量', prefix: '', suffix: '', desc: '广告被展示的总次数', value: data.impressions?.toLocaleString() || '0', trend: data.trends?.impressions || 0 },
    { key: 'clicks', label: '点击量', prefix: '', suffix: '', desc: '广告被点击的总次数', value: data.clicks?.toLocaleString() || '0', trend: data.trends?.clicks || 0 },
    { key: 'orders', label: '订单量', prefix: '', suffix: '', desc: '通过广告产生的总订单数', value: data.orders?.toLocaleString() || '0', trend: data.trends?.orders || 0 },
    { key: 'sales', label: '销售额', prefix: '$', suffix: '', desc: '通过广告产生的总销售额(ACOS计算基础)', value: data.sales?.toLocaleString() || '0', trend: data.trends?.sales || 0 },
    { key: 'roas', label: 'ROAS', prefix: '', suffix: 'x', desc: '广告支出回报率 (Sales / Spend)', value: data.roas || '0', trend: data.trends?.roas || 0 },
    { key: 'cpc', label: '平均CPC', prefix: '$', suffix: '', desc: '每次点击的平均成本', value: data.cpc || '0', trend: data.trends?.cpc || 0 }
  ]
})

const visibleMetrics = ref<string[]>(['spend', 'impressions', 'clicks', 'orders', 'sales', 'roas', 'cpc'])

const displayedMetrics = computed(() => {
  return allMetrics.value.filter(m => visibleMetrics.value.includes(m.key))
})

const loadData = async () => {
  if (!props.accountId) return
  loading.value = true
  try {
    const data = await AdsReportApi.getPerformanceScorecard({
      accountId: props.accountId,
      entityType: 'ACCOUNT',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      timeUnit: timeUnit.value
    })
    scorecardData.value = data
  } catch (error) {
    console.error('加载汇总数据失败:', error)
  } finally {
    loading.value = false
  }
}

watch(() => props.accountId, () => {
  loadData()
}, { immediate: true })

defineExpose({
  loadData
})
</script>

<style scoped>
.ad-account-data-analysis {
  margin-bottom: 20px;
}

.metric-card {
  transition: all 0.3s ease;
  background: #fff;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.trend-collapse .el-collapse-item__header) {
  height: 48px;
  line-height: 48px;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #eef2f7;
  transition: all 0.3s;
}

:deep(.trend-collapse .el-collapse-item__header.is-active) {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

:deep(.trend-collapse .el-collapse-item__wrap) {
  border: 1px solid #eef2f7;
  border-top: none;
  border-radius: 0 0 8px 8px;
}

:deep(.trend-collapse .el-collapse-item__content) {
  padding: 0;
}
</style>
