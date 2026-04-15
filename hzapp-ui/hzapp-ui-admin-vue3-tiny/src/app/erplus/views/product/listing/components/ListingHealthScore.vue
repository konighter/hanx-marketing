<template>
  <div class="health-container group/health relative cursor-pointer">
    <div class="health-ring transform -rotate-90">
      <svg class="w-10 h-10 overflow-visible" viewBox="0 0 36 36">
        <!-- 底色圆环 -->
        <circle
          cx="18" cy="18" r="16"
          fill="none"
          stroke="currentColor"
          stroke-width="3.5"
          class="text-gray-200 dark:text-gray-700 opacity-30"
        />
        <!-- 进度圆环 -->
        <circle
          cx="18" cy="18" r="16"
          fill="none"
          stroke="currentColor"
          stroke-width="4"
          stroke-linecap="round"
          :stroke-dasharray="`${(score / 100) * 100}, 100`"
          :class="scoreColorClass"
          class="transition-all duration-1000 ease-out"
        />
      </svg>
    </div>
    <!-- 分数文本 -->
    <div 
      class="absolute inset-0 flex items-center justify-center text-[11px] font-bold"
      :class="scoreTextClass"
    >
      {{ score }}
    </div>

    <!-- 诊断浮层 (设计占位) -->
    <div class="diagnosis-popover hidden group-hover/health:block absolute top-full right-0 mt-2 w-48 p-2 bg-white dark:bg-slate-800 rounded shadow-xl border border-gray-100 dark:border-slate-700 z-50 animate-in fade-in zoom-in duration-200 origin-top-right">
      <div class="text-[10px] font-bold uppercase tracking-wider text-gray-500 dark:text-gray-400 mb-1 border-b border-gray-100 dark:border-slate-700 pb-1">
        素材诊断
      </div>
      <slot name="diagnosis">
        <div class="text-[11px] text-gray-600 dark:text-gray-300 py-1 flex items-center gap-1">
          <Icon icon="ep:circle-check" class="text-green-500" /> 主图解析度良好
        </div>
        <div class="text-[11px] text-gray-400 dark:text-gray-500 italic pr-1">
          暂无严重素材问题
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  score: number // 0-100
}>()

// 根据分数计算颜色
const scoreColorClass = computed(() => {
  if (props.score >= 90) return 'text-green-500'
  if (props.score >= 70) return 'text-indigo-500'
  if (props.score >= 50) return 'text-amber-500'
  return 'text-red-500'
})

const scoreTextClass = computed(() => {
  if (props.score >= 90) return 'text-green-600 dark:text-green-400'
  if (props.score >= 70) return 'text-indigo-600 dark:text-indigo-400'
  if (props.score >= 50) return 'text-amber-600 dark:text-amber-400'
  return 'text-red-600 dark:text-red-400'
})
</script>

<style scoped>
.health-ring svg {
  /* 使用 dash-array 实现圆形进度 */
  stroke-dasharray: 0, 100;
}
</style>
