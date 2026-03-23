<template>
  <div class="mt-auto pt-4 border-t border-slate-200 dark:border-slate-800">
    <div 
      class="flex items-center gap-2 cursor-pointer text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 transition-colors w-max select-none"
      @click="expanded = !expanded"
    >
      <Icon :icon="expanded ? 'solar:alt-arrow-down-bold' : 'solar:alt-arrow-right-bold'" />
      <span class="font-bold text-sm">已完成任务归档</span>
      <el-badge :value="tasks.length" :max="99" class="ml-1" type="info" />
    </div>

    <el-collapse-transition>
      <div v-show="expanded" class="mt-4 flex flex-col gap-2 max-h-[30vh] overflow-y-auto pr-2 custom-scroll">
        <template v-if="tasks.length > 0">
          <div 
            v-for="task in reversedTasks" 
            :key="task.id"
            class="bg-white dark:bg-slate-800 rounded-lg p-3 border border-slate-200 dark:border-slate-700 shadow-sm flex items-start gap-3 hover:border-slate-300 dark:hover:border-slate-600 transition-colors"
          >
            <div class="mt-0.5">
              <Icon v-if="task.status === 'SUCCESS'" icon="solar:check-circle-bold" class="text-emerald-500 text-lg" />
              <Icon v-else-if="task.status === 'FAILED'" icon="solar:close-circle-bold" class="text-rose-500 text-lg" />
              <Icon v-else icon="solar:minus-circle-bold" class="text-slate-400 text-lg" />
            </div>
            
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-1">
                <div class="flex items-center gap-2 truncate">
                  <span class="text-xs font-bold px-1.5 py-0.5 rounded bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300">{{ task.role }}</span>
                  <span class="text-sm font-semibold text-slate-700 dark:text-slate-200 truncate">{{ task.name }}</span>
                </div>
                <span class="text-[10px] text-slate-400 shrink-0"><Icon icon="solar:clock-circle-linear" class="inline mr-0.5"/>{{ task.executionTime }}ms</span>
              </div>
              <p class="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 m-0 mt-1 cursor-pointer hover:text-slate-700 dark:hover:text-slate-200" :title="task.result">{{ task.result }}</p>
            </div>
          </div>
        </template>
        <div v-else class="text-center py-6 text-sm text-slate-400">
          暂无已归档任务
        </div>
      </div>
    </el-collapse-transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { MasTaskHistory } from '../types'

const props = defineProps<{
  tasks: MasTaskHistory[]
}>()

const expanded = ref(false)

// 倒序展示，最新完成的在最上面
const reversedTasks = computed(() => {
  return [...props.tasks].sort((a, b) => b.id - a.id)
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
</style>
