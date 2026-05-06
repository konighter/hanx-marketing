<template>
  <div class="ad-account-data-analysis" v-loading="loading">
    <!-- 1. 全局筛选器 (Date & Granularity) -->
    <div class="flex items-center justify-between mb-20px bg-[var(--el-bg-color)] p-15px rounded-8px shadow-sm border border-[var(--el-border-color-lighter)] sticky top-0 z-10">
      <div class="flex items-center gap-4">
        <!-- <span class="font-bold text-16px">账户概览</span> -->
        <el-date-picker
          v-model="localDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :clearable="false"
          class="!w-260px"
          @change="handleDateChange"
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
        class="metric-card border-none ring-1 ring-[var(--el-border-color-lighter)]"
        :body-style="{ padding: '15px' }"
      >
        <div class="flex flex-col h-full justify-between">
          <div class="text-[var(--el-text-color-secondary)] text-13px mb-2 flex items-center justify-between">
            {{ metric.label }}
            <el-tooltip :content="metric.desc" placement="top">
              <Icon icon="ep:question-filled" class="text-[var(--el-border-color-dark)] cursor-help" />
            </el-tooltip>
          </div>
          <div class="text-22px font-bold text-[var(--el-text-color-primary)] mb-2">
            {{ metric.prefix }}{{ metric.value }}{{ metric.suffix }}
          </div>
          <div class="flex items-center text-12px mt-1">
            <span class="text-[var(--el-text-color-placeholder)] mr-2">环比</span>
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
          <div class="flex items-center justify-between w-full pr-15px">
            <div class="flex items-center gap-2 font-bold px-4">
              <Icon icon="ep:histogram" class="text-primary" />
              数据趋势图
            </div>
            
            <!-- 仅展开时显示配置按钮 -->
            <el-dropdown 
              v-if="activeCollapse.includes('trend')" 
              @command="handleChartConfig" 
              trigger="click"
              @click.stop
            >
              <el-button type="primary" link size="small" @click.stop>
                <Icon icon="ep:setting" class="mr-1" /> 图表配置
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="selectMetrics">
                    <Icon icon="ep:list" class="mr-2" /> 选择展示指标
                  </el-dropdown-item>
                  <el-dropdown-item command="configureAxes">
                    <Icon icon="ep:connection" class="mr-2" /> 坐标轴配置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="resetMetrics">
                    <Icon icon="ep:refresh" class="mr-2" /> 重置默认配置
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
        <div class="p-4 bg-[var(--el-bg-color)] rounded-b-8px">
          <AdDataChart
            v-if="activeCollapse.includes('trend')"
            ref="chartRef"
            :account-id="accountId"
            :shop-id="shopId"
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
  shopId?: number
}>()

const loading = ref(false)
const activeCollapse = ref<string[]>([]) // 默认不展开
const timeUnit = ref<'day' | 'week' | 'month'>('day')
const chartRef = ref<any>(null)

const dateRange = defineModel<[string, string]>('dateRange', {
  required: true
})

// 内部使用的日期范围，用于隔离选择过程中的中间状态
const localDateRange = ref<[string, string]>([...dateRange.value])

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

// 监听外部日期变化同步给内部
watch(() => dateRange.value, (newVal) => {
  if (newVal && newVal[0] && newVal[1]) {
    localDateRange.value = [...newVal]
  }
}, { deep: true })

const handleDateChange = (val: [string, string]) => {
  if (val && val[0] && val[1]) {
    // 先确保 localDateRange 是最新值
    localDateRange.value = [...val]
    // 同步给父组件
    dateRange.value = [...val]
    loadData()
  }
}

const loadData = async () => {
  if (!props.shopId) return
  // 使用 localDateRange 作为查询的唯一数据源（与用户看到的一致）
  const dr = localDateRange.value
  if (!dr || !dr[0] || !dr[1]) return
  
  const params = {
    shopId: props.shopId,
    startDate: dr[0],
    endDate: dr[1],
    metrics: ['spend', 'impressions', 'clicks', 'orders', 'sales', 'roas', 'cpc', 'acos', 'ctr']
  }
  console.log('[AdAccountDataAnalysis] loadData params:', JSON.stringify(params))

  loading.value = true
  try {
    const res = await AdsReportApi.queryAdsReport(params)
    console.log('[AdAccountDataAnalysis] loadData response:', JSON.stringify(res))
    
    // 将返回的 metrics 列表转换为对象格式，方便 scorecard 使用
    const metricsMap: Record<string, any> = {}
    if (res.summary && res.summary.metrics) {
      res.summary.metrics.forEach(m => {
        metricsMap[m.key] = m.value
      })
    }

    scorecardData.value = {
      ...metricsMap,
      trends: {} // 环比逻辑后续若有数据可在此扩展
    }
  } catch (error) {
    console.error('加载汇总数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听账号或外部强制同步的日期变化（不含内部选择过程）
watch(() => [props.accountId, props.shopId], () => {
  loadData()
}, { immediate: true })

const handleChartConfig = (command: string) => {
  chartRef.value?.handleConfigCommand(command)
}

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
  background: var(--el-bg-color);
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--el-box-shadow-light);
}

:deep(.trend-collapse .el-collapse-item__header) {
  height: 48px;
  line-height: 48px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s;
}

:deep(.trend-collapse .el-collapse-item__header.is-active) {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

:deep(.trend-collapse .el-collapse-item__wrap) {
  border: 1px solid var(--el-border-color-lighter);
  border-top: none;
  border-radius: 0 0 8px 8px;
}

:deep(.trend-collapse .el-collapse-item__content) {
  padding: 0;
}
</style>
