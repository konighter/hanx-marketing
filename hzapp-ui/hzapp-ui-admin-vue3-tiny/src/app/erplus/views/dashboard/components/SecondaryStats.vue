<template>
  <el-row :gutter="8">
    <el-col v-for="group in secondaryGroups" :key="group.title" :xl="6" :lg="6" :md="12" :sm="12" :xs="24" class="mb-2">
      <el-card shadow="never" class="stat-group-card" :body-style="{ padding: '12px' }">
        <template #header>
          <div class="flex items-center">
            <Icon :icon="group.icon" class="mr-2 text-primary" />
            <span class="font-bold">{{ group.title }}</span>
          </div>
        </template>
        <div class="flex flex-col space-y-2">
          <div v-for="metric in group.metrics" :key="metric.label" class="flex justify-between items-center">
            <span class="text-gray-500 text-sm">{{ metric.label }}</span>
            <div class="flex flex-col items-end">
              <span class="font-bold font-mono">{{ metric.prefix }}{{ metric.value }}{{ metric.suffix }}</span>
              <span v-if="metric.delta !== undefined" :class="metric.delta >= 0 ? 'text-success' : 'text-danger'" class="text-[10px]">
                {{ metric.delta >= 0 ? '↑' : '↓' }} {{ Math.abs(metric.delta * 100).toFixed(1) }}%
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const secondaryGroups = computed(() => [
  {
    title: '售后概况',
    icon: 'ep:service',
    metrics: [
      { label: '退款金额', value: '12,450', prefix: '¥', delta: -0.05 },
      { label: '退款率', value: '2.15', suffix: '%', delta: 0.01 }
    ]
  },
  {
    title: '广告概况',
    icon: 'ep:promotion',
    metrics: [
      { label: '广告花费', value: '45,800', prefix: '¥', delta: 0.12 },
      { label: 'ACOS', value: '18.5', suffix: '%', delta: -0.02 },
      { label: 'ROAS', value: '5.4', prefix: '', delta: 0.03 }
    ]
  },
  {
    title: '财务概况',
    icon: 'ep:money',
    metrics: [
      { label: '净利润', value: '158,200', prefix: '¥', delta: 0.25 },
      { label: '利润率', value: '27.1', suffix: '%', delta: 0.05 }
    ]
  },
  {
    title: '仓库概况',
    icon: 'ep:house',
    metrics: [
      { label: '库存总额', value: '3,250,000', prefix: '¥' },
      { label: '库龄 > 90天', value: '12.5', suffix: '%' }
    ]
  }
])
</script>

<style scoped>
.stat-group-card {
  border-radius: 12px;
  height: 100%;
}
.stat-group-card :deep(.el-card__header) {
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
  background-color: #f8fafc;
}
.text-success { color: #52c41a; }
.text-danger { color: #f5222d; }
</style>
