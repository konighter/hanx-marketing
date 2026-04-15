<template>
  <div class="variant-box bg-gray-50/50 dark:bg-slate-900/40 rounded-lg p-2.5 transition-all duration-300">
    <div class="flex justify-between items-center mb-1.5 px-1">
      <div class="text-[10px] font-bold uppercase tracking-wider text-gray-400 dark:text-slate-500 flex items-center gap-1">
        <Icon icon="ep:list" class="w-3 h-3" /> 变体 ({{ variants.length }})
      </div>
      <div v-if="variants.length > 2" class="text-[10px] text-gray-400 hover:text-indigo-500 cursor-pointer flex items-center gap-0.5" @click="isExpanded = !isExpanded">
        {{ isExpanded ? '收起' : '展开' }} <Icon :icon="isExpanded ? 'ep:arrow-up' : 'ep:arrow-down'" class="w-2.5 h-2.5" />
      </div>
    </div>
    
    <div class="space-y-1.5 overflow-hidden transition-all duration-300" :class="isExpanded ? 'max-h-[1000px]' : 'max-h-24'">
      <div 
        v-for="(v, index) in limitedVariants" 
        :key="index"
        class="flex items-center justify-between py-1 border-b border-gray-100 dark:border-slate-800 last:border-0 group/row"
      >
        <div class="flex flex-col min-w-0 pr-2">
          <span class="text-[11px] font-medium truncate text-gray-700 dark:text-slate-300">
            {{ v.sku }}
          </span>
          <span v-if="v.spec" class="text-[9px] text-gray-400 dark:text-slate-500">
            {{ v.spec }}
          </span>
        </div>
        <div class="flex items-center gap-3 text-right flex-shrink-0">
          <div class="flex flex-col items-end">
            <span class="text-[11px] font-mono font-bold text-indigo-600 dark:text-indigo-400">
              {{ currency }} {{ formatPrice(v.price) }}
            </span>
            <span 
              class="text-[10px] font-bold"
              :class="v.stock <= 5 ? 'text-red-500' : 'text-gray-400 dark:text-slate-500'"
            >
              {{ v.stock <= 0 ? '缺货' : (v.stock <= 5 ? '低库存: ' + v.stock : '库存: ' + v.stock) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Variant {
  sku: string
  spec?: string
  price: number
  stock: number
}

const props = defineProps<{
  variants: Variant[]
  currency: string
}>()

const isExpanded = ref(false)
const limitedVariants = computed(() => isExpanded.value ? props.variants : props.variants.slice(0, 2))

const formatPrice = (p: number) => {
  return (p / 100).toLocaleString(undefined, { minimumFractionDigits: 2 })
}
</script>

<style scoped>
</style>
