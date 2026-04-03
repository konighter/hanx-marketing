<template>
  <div class="performance-box flex justify-center py-2 h-full" :class="[
    layout === 'vertical' ? 'flex-col border-transparent' : 'flex-row items-center justify-between gap-4 border-t border-gray-200/50 dark:border-slate-800'
  ]">
    <div class="flex flex-wrap" :class="layout === 'vertical' ? 'gap-6 mb-2 w-full' : 'gap-6'">
      <div class="flex flex-col gap-0.5 min-w-16">
        <span class="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-slate-500">
          7D 销量
        </span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold text-gray-800 dark:text-slate-100">{{ sales7d }}</span>
          <span v-if="growth > 0" class="text-[10px] font-bold text-green-500 flex items-center">
            <Icon icon="lucide:trending-up" class="w-2.5 h-2.5 mr-0.5" />+{{ growth }}%
          </span>
        </div>
      </div>
      
      <div class="flex flex-col gap-0.5 min-w-16 border-l border-gray-100 dark:border-slate-800 pl-4">
        <span class="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-slate-500">
          30D GMV
        </span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold text-emerald-500 dark:text-emerald-400">{{ currency }}{{ formatGMV }}</span>
        </div>
      </div>
    </div>

    <!-- Sparkline Trend -->
    <div class="sparkline w-24 h-8 flex-shrink-0 opacity-50 dark:opacity-80" :class="layout === 'vertical' ? 'w-full h-10 mt-2' : ''">
      <svg class="w-full h-full overflow-visible" viewBox="0 0 100 30" preserveAspectRatio="none">
        <path
          :d="sparklinePath"
          fill="none"
          stroke="currentColor"
          stroke-width="2.5"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="text-emerald-500 dark:text-emerald-400/80"
        />
        <!-- 渐变效果 -->
         <path
          :d="sparklinePath + ' L 100 30 L 0 30 Z'"
          class="fill-emerald-500/10 dark:fill-emerald-400/5"
          stroke="none"
        />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  sales7d: number
  gmv30d: number
  currency: string
  growth: number
  trend: number[] // 趋势数据 [val, val, ...]
  layout?: 'horizontal' | 'vertical'
}>()

const formatGMV = computed(() => {
  if (props.gmv30d >= 1000) {
    return (props.gmv30d / 1000).toFixed(1) + 'k'
  }
  return props.gmv30d.toString()
})

// 生成 SVG Path
const sparklinePath = computed(() => {
  if (!props.trend || props.trend.length < 2) return ''
  const max = Math.max(...props.trend) || 1
  const min = Math.min(...props.trend)
  const range = max - min || 1
  const width = 100
  const height = 28 // 留出一点边距
  
  return props.trend.map((val, i) => {
    const x = (i / (props.trend.length - 1)) * width
    const y = 30 - ((val - min) / range) * height - 1
    return (i === 0 ? 'M' : 'L') + ` ${x} ${y}`
  }).join(' ')
})
</script>
