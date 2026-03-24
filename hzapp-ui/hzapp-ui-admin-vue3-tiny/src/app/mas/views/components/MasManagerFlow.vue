<template>
  <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-200 dark:border-slate-700 p-5 w-full flex flex-col gap-4 relative overflow-hidden">
    <!-- bg glow -->
    <div class="absolute -right-10 -top-10 w-40 h-40 bg-indigo-500/10 blur-[50px] rounded-full pointer-events-none"></div>
    
    <div class="flex items-center gap-2 mb-2">
      <div class="w-8 h-8 rounded-full bg-gradient-to-br from-indigo-500 to-blue-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
        <Icon icon="solar:round-alt-arrow-right-bold" class="text-white text-lg" />
      </div>
      <h3 class="font-bold text-lg text-slate-800 dark:text-slate-100 m-0">Manager 全局视图</h3>
    </div>

    <!-- Stepper -->
    <div class="flex items-center justify-between w-full px-4 relative z-10 py-2">
      <template v-for="(node, index) in nodes" :key="node.state">
        <div class="flex flex-col items-center gap-2 relative z-10 w-24">
          <!-- Dot -->
          <div 
            class="w-6 h-6 rounded-full border-[3px] flex items-center justify-center transition-all duration-300"
            :class="[
              node.status === 'completed' ? 'bg-emerald-500 border-emerald-500' : 
              node.status === 'active' ? 'bg-indigo-500 border-indigo-200 dark:border-indigo-900 shadow-[0_0_15px_rgba(99,102,241,0.5)]' : 
              node.status === 'failed' ? 'bg-rose-500 border-rose-500' : 
              'bg-slate-200 dark:bg-slate-700 border-transparent'
            ]"
          >
            <Icon v-if="node.status === 'completed'" icon="solar:check-read-bold" class="text-white text-xs" />
            <div v-else-if="node.status === 'active'" class="w-2 h-2 bg-white rounded-full animate-pulse"></div>
            <Icon v-else-if="node.status === 'failed'" icon="solar:close-circle-bold" class="text-white text-xs" />
          </div>
          <!-- Label -->
          <span 
            class="text-[11px] font-bold uppercase tracking-wider text-center"
            :class="[
              node.status === 'active' ? 'text-indigo-600 dark:text-indigo-400' :
              node.status === 'completed' ? 'text-emerald-600 dark:text-emerald-400' :
              'text-slate-400'
            ]"
          >
            {{ node.label }}
          </span>
        </div>

        <!-- Connecting Line -->
        <div
v-if="index < nodes.length - 1" class="flex-1 h-[2px] mx-[-20px] relative top-[-15px] z-0"
             :class="node.status === 'completed' ? 'bg-emerald-500/50' : 'bg-slate-200 dark:bg-slate-700'">
        </div>
      </template>
    </div>

    <!-- Manager Decision Callout -->
    <div v-if="latestDecision" class="mt-2 bg-slate-50 dark:bg-slate-900/50 rounded-xl p-3 border border-slate-100 dark:border-slate-700/50 flex gap-3 anim-slide-up">
      <div class="text-xl">💬</div>
      <div class="flex-1 flex flex-col">
        <span class="text-xs font-bold text-slate-500 mb-1">Manager 最新决策</span>
        <p class="text-sm text-slate-700 dark:text-slate-300 m-0 line-clamp-2 leading-relaxed" :title="latestDecision">
          {{ latestDecision }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { FlowNode, MasSession, MasTaskHistory } from '../types'

const props = defineProps<{
  session: MasSession
  history: MasTaskHistory[]
}>()

// 状态流转定义
const STATES = ['INIT', 'PLANNING', 'EXECUTING', 'REVIEWING', 'COMPLETED']
const LABELS = ['初始化', '任务规划', '任务执行', '结果评估', '已完成']

const nodes = computed<FlowNode[]>(() => {
  const currentStatus = props.session.status
  let currentIndex = STATES.indexOf(currentStatus)
  
  if (currentStatus === 'FAILED') {
    currentIndex = -1 // handle specially
  }

  return STATES.map((state, index) => {
    let nodeStatus: 'completed' | 'active' | 'pending' | 'failed' = 'pending'
    if (currentStatus === 'FAILED') {
      nodeStatus = index === STATES.length - 1 ? 'pending' : 'completed' // Approximated
      if (state === STATES[STATES.length - 1]) nodeStatus = 'failed'
    } else {
      if (index < currentIndex) nodeStatus = 'completed'
      else if (index === currentIndex) nodeStatus = 'active'
    }

    return {
      state,
      label: LABELS[index],
      status: nodeStatus
    }
  })
})

const latestDecision = computed(() => {
  // Find latest MANAGER internal task result
  const managerTasks = props.history.filter(h => h.role === 'MANAGER' && h.isInternal && h.status === 'SUCCESS')
  if (managerTasks.length === 0) return ''
  
  const latestTask = managerTasks[managerTasks.length - 1]
  // clean up markdown json blocks if any
  let text = latestTask.result || ''
  try {
    const jsonMatch = text.match(/```(?:json)?[\s\S]*?```/)
    if (jsonMatch) {
       text = text.replace(jsonMatch[0], '[决策数据]')
    }
  } catch(e){}
  
  return text.trim()
})
</script>

<style scoped>
.anim-slide-up {
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
