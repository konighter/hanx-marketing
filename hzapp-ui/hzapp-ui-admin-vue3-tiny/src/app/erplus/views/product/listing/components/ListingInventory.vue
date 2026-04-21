<template>
  <div class="inventory-card bg-gray-50/50 dark:bg-slate-900/40 rounded-lg p-3 group/inv hover:bg-white dark:hover:bg-slate-800 transition-all border border-transparent hover:border-indigo-100 dark:hover:border-indigo-900/30">
    <div class="flex items-center justify-between mb-2">
      <div class="text-[10px] font-bold uppercase tracking-wider text-gray-400 dark:text-slate-500 flex items-center gap-1.5 leading-none">
        <Icon icon="ep:box" class="w-3 h-3 text-indigo-500" /> 当前库存
      </div>
      <div v-if="(inventory?.fulfillableQuantity || 0) < 50" class="flex items-center gap-1 bg-rose-50 dark:bg-rose-900/30 text-rose-500 dark:text-rose-400 px-1.5 py-0.5 rounded border border-rose-100 dark:border-rose-800/40 animate-pulse">
        <Icon icon="ep:warning" class="w-2.5 h-2.5" />
        <span class="text-[9px] font-bold uppercase">低库存</span>
      </div>
      <el-tooltip v-else content="可用库存 / 预留 / 在途 / 不可配送" placement="top">
        <Icon icon="ep:info-filled" class="w-3 h-3 text-gray-300 dark:text-slate-600" />
      </el-tooltip>
    </div>
    
    <div class="grid grid-cols-2 gap-y-2.5 gap-x-4">
      <!-- Fulfillable -->
      <div class="flex flex-col">
        <span class="text-[9px] text-gray-400 dark:text-slate-500 uppercase leading-none mb-1 font-bold tracking-tighter">Fulfillable</span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold font-mono text-emerald-600 dark:text-emerald-400 leading-none">
            {{ inventory?.fulfillableQuantity || 0 }}
          </span>
          <span class="text-[9px] text-emerald-500/60 dark:text-emerald-400/40 font-medium">可用</span>
        </div>
      </div>
      
      <!-- Reserved -->
      <div class="flex flex-col">
        <span class="text-[9px] text-gray-400 dark:text-slate-500 uppercase leading-none mb-1 font-bold tracking-tighter">Reserved</span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold font-mono text-amber-500 dark:text-amber-400 leading-none">
            {{ inventory?.reservedQuantity || 0 }}
          </span>
          <span class="text-[9px] text-amber-500/60 dark:text-amber-400/40 font-medium">预留</span>
        </div>
      </div>

      <!-- Inbound -->
      <div class="flex flex-col">
        <span class="text-[9px] text-gray-400 dark:text-slate-500 uppercase leading-none mb-1 font-bold tracking-tighter">Inbound</span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold font-mono text-indigo-500 dark:text-indigo-400 leading-none">
            {{ inventory?.inboundShippedQuantity || 0 }}
          </span>
          <span class="text-[9px] text-indigo-500/60 dark:text-indigo-400/40 font-medium">在途</span>
        </div>
      </div>

      <!-- Unfulfillable -->
      <div class="flex flex-col">
        <span class="text-[9px] text-gray-400 dark:text-slate-500 uppercase leading-none mb-1 font-bold tracking-tighter">Unfulfillable</span>
        <div class="flex items-baseline gap-1">
          <span class="text-sm font-bold font-mono text-rose-500 dark:text-rose-400 leading-none">
            {{ inventory?.unfulfillableQuantity || 0 }}
          </span>
          <span class="text-[9px] text-rose-500/60 dark:text-rose-400/40 font-medium">异常</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  inventory?: {
    fulfillableQuantity: number
    reservedQuantity: number
    inboundShippedQuantity: number
    unfulfillableQuantity: number
  }
}>()
</script>

<style scoped>
.inventory-card {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
}
.dark .inventory-card {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}
</style>
