<template>
  <div class="w-full flex-1 min-h-0 relative overflow-x-auto overflow-y-hidden custom-scroll-x pt-2 pb-4 px-2">
    <transition-group name="card-list" tag="div" class="flex items-stretch gap-6 h-full min-w-max">
      <MasAgentCard 
        v-for="card in cards" 
        :key="card.role" 
        :card="card"
        class="card-item shrink-0"
      />
    </transition-group>
    
    <!-- Empty state if no active agents -->
    <div v-if="cards.length === 0" class="absolute inset-0 flex flex-col items-center justify-center opacity-40 text-slate-500">
      <Icon icon="solar:tea-cup-bold-duotone" class="text-6xl mb-4" />
      <span class="font-bold tracking-widest uppercase">Agent Pool Standby</span>
      <span class="text-xs mt-2">Manager 尚未指派或任务已全部归档</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AgentWorkCard } from '../types'
import MasAgentCard from './MasAgentCard.vue'

defineProps<{
  cards: AgentWorkCard[]
}>()
</script>

<style scoped>
.custom-scroll-x::-webkit-scrollbar {
  height: 6px;
}
.custom-scroll-x::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scroll-x::-webkit-scrollbar-thumb {
  @apply bg-slate-300 dark:bg-slate-600 rounded-full;
}

.card-list-enter-active,
.card-list-leave-active,
.card-list-move {
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
.card-list-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.9);
}
.card-list-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.85); /* 向上缩小消失，模拟被 Manager 归档 */
}
.card-list-leave-active {
  position: absolute;
}
</style>
