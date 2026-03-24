<template>
  <div class="agent-card flex flex-col bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden w-[380px] shrink-0 h-full max-h-full relative transition-all duration-300">
    
    <!-- Header -->
    <div class="px-4 py-3 border-b border-slate-100 dark:border-slate-700/50 flex items-center justify-between relative z-10 bg-slate-50/50 dark:bg-slate-800/50">
      <div class="flex items-center gap-2">
        <div
class="w-8 h-8 rounded-lg flex items-center justify-center text-white shadow-md relative"
             :class="roleColorInfo.bg">
          <Icon :icon="roleColorInfo.icon" class="text-sm z-10" />
          <!-- Pulse effect when working -->
          <div v-if="card.status === 'working'" class="absolute inset-0 rounded-lg animate-ping opacity-40" :class="roleColorInfo.bg"></div>
        </div>
        <div>
          <h4 class="m-0 text-sm font-bold text-slate-800 dark:text-slate-200">{{ card.agentName }}</h4>
          <span class="text-[10px] uppercase font-bold tracking-wider text-slate-400">{{ card.role }}</span>
        </div>
      </div>
      
      <!-- Status Badge -->
      <div
class="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold"
           :class="badgeStyle">
        <Icon v-if="card.status === 'working'" icon="svg-spinners:180-ring" />
        <Icon v-else-if="card.status === 'done'" icon="solar:check-circle-bold" />
        <Icon v-else icon="solar:minus-circle-bold" />
        {{ statusText }}
      </div>
    </div>

    <!-- Body Scrollable Area -->
    <div class="flex-1 overflow-y-auto px-4 py-4 custom-scroll relative bg-slate-50/30 dark:bg-transparent">
      
      <!-- Current Task Label -->
      <div v-if="card.currentTask || card.status === 'working'" class="mb-4">
        <div class="text-xs font-bold text-slate-500 mb-1 flex items-center justify-between">
          <span>🎯 当前任务</span>
          <span class="text-indigo-500">{{ card.completedTasks }} / {{ card.totalTasks }}</span>
        </div>
        <p class="text-sm font-bold m-0 text-slate-700 dark:text-slate-300 bg-white dark:bg-slate-700/50 p-2.5 rounded-lg border border-slate-200 dark:border-slate-600 shadow-sm leading-snug">
          {{ card.currentTask?.name || '正在规划并调用系统执行层...' }}
        </p>
      </div>

      <!-- Execution In Progress Indicator -->
      <transition name="fade-slide" mode="out-in">
        <div v-if="card.status === 'working'" class="mb-5 bg-indigo-50 dark:bg-indigo-900/20 border border-indigo-100 dark:border-indigo-800/50 rounded-xl p-3">
          <div class="flex items-center gap-2 mb-2 text-indigo-600 dark:text-indigo-400 font-bold text-sm">
            <Icon icon="svg-spinners:pulse-rings-multiple" class="text-xl" />
            <span>执行中...</span>
          </div>
          <!-- Thought/Prompt Collapsible -->
          <el-collapse v-if="card.currentTask" v-model="activeCollapse" class="!border-none ghost-collapse">
            <el-collapse-item name="prompt" class="!bg-transparent">
              <template #title>
                <span class="text-xs font-semibold text-slate-500 flex items-center gap-1"><Icon icon="solar:document-text-linear" /> 查看上一轮 Prompt</span>
              </template>
              <div class="text-xs text-slate-600 dark:text-slate-400 bg-white dark:bg-slate-800 p-2 rounded border border-slate-200 dark:border-slate-700 max-h-32 overflow-y-auto custom-scroll whitespace-pre-wrap">
                {{ card.currentTask.prompt }}
              </div>
            </el-collapse-item>
          </el-collapse>
          <div v-else class="text-xs text-slate-500 py-1">
            大模型正在思考和生成内容中，请稍候...
          </div>
        </div>
      </transition>

      <!-- Divider -->
      <div v-if="card.recentResults.length > 0" class="flex items-center gap-2 my-2 mt-4 text-xs font-bold text-slate-400 select-none">
        <Icon icon="solar:history-bold" />
        <span>历史结果 (最新在上)</span>
        <div class="flex-1 h-px bg-slate-200 dark:bg-slate-700"></div>
      </div>

      <!-- Results List (Reverse Order) -->
      <div class="flex flex-col gap-3 relative z-0">
        <transition-group name="list-enter">
          <div
v-for="(res, index) in card.recentResults" :key="res.id"
               class="bg-white dark:bg-slate-800 border rounded-xl overflow-hidden transition-all duration-300"
               :class="[
                 index === 0 ? 'border-indigo-200 dark:border-indigo-700 shadow-[0_4px_12px_rgba(99,102,241,0.08)]' : 'border-slate-200 dark:border-slate-700 shadow-sm opacity-80 scale-[0.98]'
               ]">
            
            <div
class="px-3 py-2 bg-slate-50 dark:bg-slate-800 border-b border-slate-100 dark:border-slate-700 flex justify-between items-center cursor-pointer select-none"
                 @click="toggleResult(res.id)">
              <div class="flex items-center gap-1.5 truncate pr-2">
                <Icon v-if="res.status === 'SUCCESS'" icon="solar:check-circle-bold" class="text-emerald-500" />
                <Icon v-else-if="res.status === 'FAILED'" icon="solar:close-circle-bold" class="text-rose-500" />
                <span class="text-xs font-bold truncate" :class="index === 0 ? 'text-indigo-600 dark:text-indigo-400' : 'text-slate-600 dark:text-slate-400'">{{ res.name }}</span>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span class="text-[10px] text-slate-400"><Icon icon="solar:clock-circle-linear" class="inline mr-0.5"/>{{ res.executionTime }}ms</span>
                <Icon :icon="expandedResults.includes(res.id) ? 'solar:alt-arrow-down-linear' : 'solar:alt-arrow-right-linear'" class="text-slate-400" />
              </div>
            </div>

            <el-collapse-transition>
              <div v-show="expandedResults.includes(res.id)">
                <div class="p-3 text-sm text-slate-700 dark:text-slate-300 bg-white dark:bg-slate-900/30 border-t border-slate-100 dark:border-slate-800">
                  <pre class="whitespace-pre-wrap font-sans m-0 text-xs leading-relaxed max-h-64 overflow-y-auto custom-scroll">{{ res.result }}</pre>
                </div>
              </div>
            </el-collapse-transition>
            
            <!-- Default collapsed view preview -->
            <div v-show="!expandedResults.includes(res.id)" class="px-3 py-2 text-xs text-slate-500 dark:text-slate-400 line-clamp-2 truncate" :title="res.result">
               {{ formatPreview(res.result) }}
            </div>
          </div>
        </transition-group>
      </div>

    </div>
    
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { AgentWorkCard } from '../types'

const props = defineProps<{
  card: AgentWorkCard
}>()

const activeCollapse = ref(['prompt'])
const expandedResults = ref<number[]>([])

// When a new result comes in, auto expand it if it's the first one
watch(() => props.card.recentResults, (newVals) => {
  if (newVals.length > 0) {
    const latestId = newVals[0].id
    if (!expandedResults.value.includes(latestId)) {
       // Optionally auto-collapse older ones to save space
       expandedResults.value = [latestId]
    }
  }
}, { deep: true, immediate: true })

const toggleResult = (id: number) => {
  const index = expandedResults.value.indexOf(id)
  if (index === -1) expandedResults.value.push(id)
  else expandedResults.value.splice(index, 1)
}

const formatPreview = (text: string) => {
  if (!text) return ''
  // simple markdown block strip
  return text.replace(/```[\s\S]*?```/g, '[代码块]').replace(/\n/g, ' ')
}

const roleColorInfo = computed(() => {
  const role = props.card.role.toUpperCase()
  if (role === 'PM') return { bg: 'bg-gradient-to-br from-indigo-500 to-purple-500', icon: 'solar:clipboard-list-bold' }
  if (role === 'EXECUTOR') return { bg: 'bg-gradient-to-br from-blue-500 to-cyan-500', icon: 'solar:settings-bold' }
  return { bg: 'bg-gradient-to-br from-slate-500 to-slate-600', icon: 'solar:user-bold' }
})

const badgeStyle = computed(() => {
  if (props.card.status === 'working') return 'bg-indigo-100 text-indigo-600 dark:bg-indigo-900/50 dark:text-indigo-400'
  if (props.card.status === 'done') return 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900/50 dark:text-emerald-400'
  return 'bg-slate-100 text-slate-500 dark:bg-slate-800'
})

const statusText = computed(() => {
  if (props.card.status === 'working') return '处理中'
  if (props.card.status === 'done') return '就绪'
  return '空闲'
})
</script>

<style scoped>
.custom-scroll::-webkit-scrollbar {
  width: 4px;
}
.custom-scroll::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scroll::-webkit-scrollbar-thumb {
  @apply bg-slate-300 dark:bg-slate-600 rounded-full;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.95);
}

.list-enter-enter-active,
.list-enter-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.list-enter-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
.list-enter-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
  position: absolute; /* helps smooth layout shift */
}
.list-enter-move {
  transition: transform 0.4s ease;
}

/* Override element plus collapse */
.ghost-collapse :deep(.el-collapse-item__header) {
  @apply bg-transparent border-none h-8 text-xs;
}
.ghost-collapse :deep(.el-collapse-item__wrap) {
  @apply bg-transparent border-none;
}
.ghost-collapse :deep(.el-collapse-item__content) {
  @apply pb-2 pt-0;
}
</style>
