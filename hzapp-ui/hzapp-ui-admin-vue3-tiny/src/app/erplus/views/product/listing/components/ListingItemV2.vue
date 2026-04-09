<template>
  <div 
    class="listing-card group relative p-4 rounded-2xl border border-gray-100 dark:border-slate-800 bg-white dark:bg-slate-800/50 transition-colors"
    :class="[
      isSelected ? 'ring-2 ring-indigo-500/50 bg-indigo-50/1' : '',
      viewMode === 'list' ? 'flex flex-row items-start gap-6' : 'flex flex-col gap-4'
    ]"
  >
    <div :class="viewMode === 'list' ? 'flex flex-col gap-4 flex-[1.5] min-w-0' : 'contents'">
    <!-- Header Section: Health, Platform & Status -->
    <div class="flex items-start justify-between">
      <div class="flex items-center gap-3">
        <!-- Platform Icon & Selection Checkbox -->
        <div class="relative platform-icon w-8 h-8 rounded-lg bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 flex items-center justify-center shadow-sm cursor-pointer" @click="$emit('select', !isSelected)">
           <img :src="getPlatformIcon(listing.platformId)" class="w-5 h-5 object-contain transition-opacity duration-300 group-hover:opacity-0" :class="{ 'opacity-0': isSelected }" />
           <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity" :class="{ '!opacity-100': isSelected }">
             <el-checkbox :model-value="isSelected" @click.stop @change="$emit('select', !isSelected)" />
           </div>
        </div>
        <div class="min-w-0 pr-4">
          <div class="text-[10px] font-bold uppercase tracking-widest text-gray-400 dark:text-slate-500 flex items-center gap-1 leading-none mb-1">
             ID: {{ listing.platformProductCode }} 
             <Icon icon="ep:copy-document" class="cursor-pointer hover:text-indigo-500" />
             <el-tooltip content="同步到对应店铺" placement="top">
               <Icon icon="ep:refresh" class="cursor-pointer hover:text-indigo-500 ml-1" @click.stop="$emit('sync', listing)" />
             </el-tooltip>
             <el-tooltip content="详情/编辑" placement="top">
               <Icon icon="ep:edit" class="cursor-pointer hover:text-indigo-500 ml-0.5" @click.stop="$emit('detail', listing)" />
             </el-tooltip>
          </div>
          <div class="status-badge flex items-center gap-1.5">
            <span class="w-1.5 h-1.5 rounded-full" :class="getStatusColor(listing.status)"></span>
            <span class="text-[11px] font-bold" :class="getStatusTextClass(listing.status)">{{ listing.statusName }}</span>
            
            <template v-if="listing.syncStatus !== undefined && listing.syncStatus !== 0">
              <span class="mx-1 text-gray-300">|</span>
              <el-tag 
                :type="getSyncStatusType(listing.syncStatus)" 
                size="small" 
                class="!h-4 !px-1.5 !text-[10px] cursor-pointer"
                @click.stop="$emit('detail', listing)"
              >
                {{ listing.syncStatusName }}
              </el-tag>
            </template>
          </div>
        </div>
      </div>
      
      <!-- Built-in Health Score -->
      <ListingHealthScore :score="diagnosis.score">
        <template #diagnosis>
          <div class="space-y-1 text-[11px]">
             <div v-for="(issue, idx) in diagnosis.issues" :key="idx" class="flex items-start gap-1.5">
                <Icon 
                  :icon="issue.type === 'error' ? 'ep:circle-close-filled' : 'ep:warning-filled'" 
                  :class="issue.type === 'error' ? 'text-red-500' : 'text-amber-500'" 
                  class="mt-0.5"
                />
                <span class="text-gray-600 dark:text-slate-300">{{ issue.label }}</span>
             </div>
          </div>
        </template>
      </ListingHealthScore>
    </div>

    <!-- Body Section: Main Image & Info -->
    <div class="flex gap-4">
      <div class="image-wrapper group/img relative w-16 h-16 rounded-lg overflow-hidden bg-gray-50 dark:bg-slate-900 border border-gray-100 dark:border-slate-800 flex-shrink-0 shadow-inner">
        <el-image 
          :src="listing.mainImage?.url" 
          fit="cover" 
          class="w-full h-full"
          :preview-src-list="listing.mainImage?.url ? [listing.mainImage.url] : []"
          preview-teleported
          hide-on-click-modal
        />
        <div class="absolute inset-0 bg-black/20 opacity-0 group-hover/img:opacity-100 transition-opacity flex items-center justify-center pointer-events-none">
           <Icon icon="ep:view" class="text-white w-6 h-6" />
        </div>
      </div>
      
      <div class="flex flex-col justify-between py-0.5 min-w-0 flex-1">
        <div class="title-container">
           <h3 
             class="text-sm font-bold leading-tight line-clamp-2 text-gray-800 dark:text-slate-200 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 hover:underline cursor-pointer transition-colors pr-2"
             @click.stop="$emit('detail', listing)"
           >
             {{ listing.title }}
           </h3>
           <div class="mt-1 text-[11px] text-gray-400 dark:text-slate-500 font-mono">P-SKU: {{ listing.sellerProductCode }}</div>
        </div>
        
        <div class="price-section flex items-baseline gap-1 mt-1">
          <span class="text-xs font-bold text-gray-400 font-mono leading-none">{{ currency }}</span>
          <span class="text-lg font-bold text-indigo-600 dark:text-indigo-400 font-mono leading-none tracking-tight">
            {{ formatPriceRange }}
          </span>
        </div>
      </div>
    </div> <!-- End Info Column -->
    </div> <!-- End Left Column Wrapper -->

    <!-- Variant Box (Table inside card) -->
    <div :class="viewMode === 'list' ? 'flex-[1.2] min-w-0' : 'contents'">
      <ListingVariantBox 
        :variants="mockVariants" 
        :currency="currency" 
      />
    </div>

    <!-- Footer: Performance Metrics (Mocked) -->
    <div :class="viewMode === 'list' ? 'flex-[1] min-w-0' : 'contents'">
      <ListingPerformance 
        :sales7d="performance.sales7d"
        :gmv30d="performance.gmv30d"
        :currency="currency"
        :growth="12.5"
        :trend="performance.revenueCurve"
        :layout="viewMode === 'list' ? 'vertical' : 'horizontal'"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ListingV2VO, ListingDiagnosisVO, ListingPerformanceVO } from '../types'
import ListingHealthScore from './ListingHealthScore.vue'
import ListingVariantBox from './ListingVariantBox.vue'
import ListingPerformance from './ListingPerformance.vue'

const props = defineProps<{
  listing: ListingV2VO
  isSelected: boolean
  viewMode?: 'grid' | 'list'
}>()

defineEmits(['select', 'sync', 'detail'])

// --- Data Mapping (Now provided by API/Mock Interceptor) ---
const currency = computed(() => props.listing.price?.[0]?.currency || '$')

const formatPriceRange = computed(() => {
  if (!props.listing.price || props.listing.price.length === 0) return '0.00'
  const prices = props.listing.price.map(p => p.salePrice / 100)
  const min = Math.min(...prices)
  const max = Math.max(...prices)
  return min === max ? min.toFixed(2) : `${min.toFixed(2)} - ${max.toFixed(2)}`
})

// Using data from props.listing (Injected by queryCrossProductListingPage)
const diagnosis = computed((): ListingDiagnosisVO => props.listing.diagnosis!)

const performance = computed((): ListingPerformanceVO => props.listing.performance!)

const mockVariants = computed(() => props.listing.mockVariants || [])

// --- Helper Functions ---
const getPlatformIcon = (id: number) => {
  // Simple map for icons
  const icons: Record<number, string> = {
    1: 'https://www.google.com/s2/favicons?domain=amazon.com&sz=64',
    2: 'https://www.google.com/s2/favicons?domain=shopee.com&sz=64',
    3: 'https://www.google.com/s2/favicons?domain=tiktok.com&sz=64'
  }
  return icons[id] || icons[1]
}

const getStatusColor = (status: string) => {
  if (status === 'active' || status === '1') return 'bg-emerald-500'
  if (status === 'pending' || status === '2') return 'bg-amber-500'
  return 'bg-red-500'
}

const getStatusTextClass = (status: string) => {
  if (status === 'active' || status === '1') return 'text-emerald-500 dark:text-emerald-400'
  if (status === 'pending' || status === '2') return 'text-amber-500 dark:text-amber-400'
  return 'text-red-500 dark:text-red-400'
}

const getSyncStatusType = (status: number): 'info' | 'success' | 'danger' | 'warning' => {
  const types: Record<number, 'info' | 'success' | 'danger' | 'warning'> = {
    0: 'info',    // 未刊登 (INIT)
    10: 'warning', // 待发布 (AUDITING)
    90: 'info',    // 发布中 (PUBLISHING)
    91: 'danger',  // 发布失败 (FAIL)
    99: 'success'  // 发布成功 (SUC)
  }
  return types[status] || 'info'
}
</script>

<style scoped>
</style>
