<template>
  <el-drawer
    v-model="visible"
    :title="`商品详情 - ${listing?.title || ''}`"
    size="50%"
    :destroy-on-close="true"
  >
    <div v-if="listing" class="detail-container px-4">
      <!-- 基础信息面板 -->
      <div class="flex gap-6 mb-6">
        <div class="w-32 h-32 rounded-lg overflow-hidden border border-gray-200 flex-shrink-0">
          <el-image 
            :src="listing.mainImage?.url" 
            fit="cover" 
            class="w-full h-full"
            :preview-src-list="listing.mainImage?.url ? [listing.mainImage.url] : []"
          />
        </div>
        <div class="flex-1 min-w-0">
          <h2 class="text-lg font-bold text-gray-800 dark:text-slate-100 leading-snug mb-2">
            {{ listing.title }}
          </h2>
          <div class="grid grid-cols-2 gap-4 text-sm mt-4">
            <div>
              <span class="text-gray-500">平台 ID:</span>
              <span class="ml-2 font-mono font-bold text-gray-700 dark:text-slate-300">{{ listing.platformProductCode }}</span>
            </div>
            <div>
              <span class="text-gray-500">卖家 SKU:</span>
              <span class="ml-2 font-mono font-bold text-gray-700 dark:text-slate-300">{{ listing.sellerProductCode }}</span>
            </div>
            <div>
              <span class="text-gray-500">最高零售价:</span>
              <span class="ml-2 font-mono text-indigo-600 font-bold max-w-[200px] truncate">
                {{ formatPriceRange }}
              </span>
            </div>
            <div>
              <span class="text-gray-500">状态:</span>
              <el-tag :type="listing.status === 'active' || listing.status === '1' ? 'success' : 'warning'" size="small" class="ml-2">
                {{ listing.statusName }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据详情 Tabs -->
      <el-tabs v-model="activeTab" class="custom-drawer-tabs mt-2">
        <el-tab-pane label="变体信息" name="variants">
          <div v-if="listing.mockVariants?.length" class="space-y-4">
            <el-table :data="listing.mockVariants" border style="width: 100%" stripe size="small">
              <el-table-column prop="sku" label="变体 SKU" min-width="120" show-overflow-tooltip />
              <el-table-column prop="name" label="属性" min-width="150" show-overflow-tooltip />
              <el-table-column label="售价" width="100" align="right">
                <template #default="{ row }">
                  <span class="font-mono text-indigo-600 font-bold">{{ formatPrice(row.price) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="sales7d" label="7D 销量" width="90" align="right">
                <template #default="{ row }">
                  <span class="font-bold text-gray-700 dark:text-slate-300">{{ row.sales7d || 0 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="gmv30d" label="30D 销售额" width="110" align="right">
                <template #default="{ row }">
                  <span class="font-mono font-bold text-emerald-600 dark:text-emerald-400">{{ currency }}{{ row.gmv30d || 0 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="stock" label="库存" width="100" align="right">
                <template #default="{ row }">
                  <el-tag :type="row.stock > 10 ? 'success' : 'danger'" size="small">
                    {{ row.stock }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-empty v-else description="暂无变体数据" />
        </el-tab-pane>
        
        <el-tab-pane label="性能数据" name="performance">
          <div class="grid grid-cols-3 gap-6 mb-6" v-if="listing.performance">
            <div class="bg-gray-50 dark:bg-slate-800 p-4 rounded-xl border border-gray-100 dark:border-slate-700">
              <div class="text-xs text-gray-500 mb-1">7D 销量</div>
              <div class="text-xl font-bold text-gray-800 dark:text-slate-100">{{ listing.performance.sales7d }}</div>
            </div>
            <div class="bg-indigo-50 dark:bg-indigo-900/20 p-4 rounded-xl border border-indigo-100 dark:border-indigo-800/30">
              <div class="text-xs text-indigo-500 mb-1">30D GMV</div>
              <div class="text-xl font-bold text-indigo-600 dark:text-indigo-400">{{ currency }}{{ listing.performance.gmv30d }}</div>
            </div>
            <div class="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-xl border border-emerald-100 dark:border-emerald-800/30">
              <div class="text-xs text-emerald-500 mb-1">转化率 (转化)</div>
              <div class="text-xl font-bold text-emerald-600 dark:text-emerald-400">12.5%</div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="健康诊断" name="health">
          <div v-if="listing.diagnosis?.issues?.length" class="space-y-4">
            <el-alert
              v-for="(issue, index) in listing.diagnosis.issues"
              :key="index"
              :title="issue.label"
              :type="issue.type === 'error' ? 'error' : 'warning'"
              :closable="false"
              show-icon
            />
          </div>
          <el-empty v-else description="恭喜，此商品目前无健康问题" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ListingV2VO } from '../types'

const visible = ref(false)
const listing = ref<ListingV2VO | null>(null)
const activeTab = ref('variants')

const currency = computed(() => listing.value?.price?.[0]?.currency || '$')

const formatPriceRange = computed(() => {
  if (!listing.value?.price || listing.value.price.length === 0) return '0.00'
  const prices = listing.value.price.map(p => p.salePrice / 100)
  const min = Math.min(...prices)
  const max = Math.max(...prices)
  return min === max ? `${currency.value}${min.toFixed(2)}` : `${currency.value}${min.toFixed(2)} - ${max.toFixed(2)}`
})

const formatPrice = (p: number) => {
  return `${currency.value}${(p / 100).toLocaleString(undefined, { minimumFractionDigits: 2 })}`
}

const open = (row: ListingV2VO) => {
  listing.value = row
  activeTab.value = 'variants'
  visible.value = true
}

defineExpose({ open })
</script>

<style scoped>
:deep(.custom-drawer-tabs) {
  .el-tabs__item {
    font-size: 14px;
    padding: 0 20px;
    height: 48px;
    line-height: 48px;
  }
}
</style>
