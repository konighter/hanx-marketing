<template>
  <div class="p-1 bg-[#f0f2f5] min-h-screen dashboard-container">
    <!-- Header Section -->
    <div class="flex justify-between items-center mb-2">
      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-2">
          <el-radio-group v-model="timeRange">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="yesterday">昨日</el-radio-button>
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div class="flex items-center space-x-3">
        <el-cascader
          v-model="storeSelection"
          :options="storeOptions"
          placeholder="选择站点 / 店铺"
          clearable
          style="width: 260px"
          class="rounded-cascader"
        />
        <el-date-picker
          v-model="customDate"
          type="daterange"
          placeholder="自定义时间"
          style="width: 260px"
        />
        <el-button type="primary" :icon="Refresh" circle class="refresh-btn" @click="refreshData" />
      </div>
    </div>

    <!-- Main Content -->
    <div>
      <!-- 1. 核心销售分析 -->
      <KpiSummary :loading="loading" class="mb-2" />

      <!-- 2. 运营二级概况 -->
      <SecondaryStats class="mb-2" />

      <!-- 3. 业绩走势 -->
      <AnalysisCharts class="mb-2" />

      <!-- 4. 待办事项模块 -->
      <TodoDashboard class="mb-2" />

      <!-- 5. 排行榜单 -->
      <RankingLists class="mb-2" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import KpiSummary from './components/KpiSummary.vue'
import SecondaryStats from './components/SecondaryStats.vue'
import AnalysisCharts from './components/AnalysisCharts.vue'
import TodoDashboard from './components/TodoDashboard.vue'
import RankingLists from './components/RankingLists.vue'

const loading = ref(false)
const timeRange = ref('today')
const storeSelection = ref([])
const customDate = ref([])

const storeOptions = [
  {
    value: 'amazon',
    label: 'Amazon',
    children: [
      { value: 'us', label: '美国站' },
      { value: 'eu', label: '欧洲站' },
      { value: 'jp', label: '日本站' }
    ]
  },
  {
    value: 'shopify',
    label: 'Shopify',
    children: [
      { value: 'global', label: '全球店' }
    ]
  },
  {
    value: 'walmart',
    label: 'Walmart',
    children: [
      { value: 'us', label: '美国站' }
    ]
  }
]

const refreshData = async () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 800)
}

onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.dashboard-container :deep(.el-card) {
  border: none;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
}
.dashboard-container :deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 20px;
}

/* Restored Rounded styles */
.rounded-cascader :deep(.el-input__wrapper) {
  border-radius: 20px !important;
  padding-left: 15px;
}

.refresh-btn {
  background-color: #5b5dfa !important;
  border-color: #5b5dfa !important;
  width: 40px;
  height: 40px;
}

.refresh-btn :deep(.el-icon) {
  font-size: 18px;
  color: white;
}
</style>
