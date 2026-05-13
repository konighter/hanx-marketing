<template>
  <el-drawer
    v-model="visible"
    :with-header="false"
    size="50%"
    @opened="onOpened"
    @close="onClose"
  >
    <div v-if="contentReady && listing" class="detail-container px-4">
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
              <span class="text-gray-500">售价:</span>
              <div class="inline-flex flex-col gap-1 ml-2 align-top">
                <!-- Discounted Prices -->
                <template v-if="discountedPrices.length > 0">
                  <div class="flex items-center gap-2">
                    <span class="px-1 py-0.5 rounded text-[10px] bg-rose-50 dark:bg-rose-900/20 text-rose-500 dark:text-rose-400 font-bold border border-rose-100 dark:border-rose-800/30 font-sans">促销价</span>
                    <span class="font-mono font-bold text-rose-600 dark:text-rose-400 text-lg">
                      {{ formatRange(discountedPrices) }}
                    </span>
                  </div>
                  <div v-if="allPrices.length > 0" class="flex items-center gap-2 opacity-60">
                    <span class="px-1 py-0.5 rounded text-[10px] bg-gray-50 dark:bg-slate-800/40 text-gray-400 dark:text-slate-500 font-medium border border-gray-100 dark:border-slate-700/30 font-sans">原价</span>
                    <span class="text-sm text-gray-400 font-mono line-through">
                      {{ formatRange(allPrices) }}
                    </span>
                  </div>
                </template>
                
                <!-- Only All Price -->
                <template v-else-if="allPrices.length > 0">
                  <span class="font-mono font-bold text-indigo-600 dark:text-indigo-400 text-lg leading-none">
                    {{ formatRange(allPrices) }}
                  </span>
                </template>
                
                <span v-else class="text-gray-400 text-sm font-mono">0.00</span>
              </div>
            </div>
            <div>
              <span class="text-gray-500">状态:</span>
              <el-tag :type="listing.status === 'active' || listing.status === '1' || String(listing.status) === '19' ? 'success' : 'warning'" size="small" class="ml-2">
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
          <!-- Top KPI Grid -->
          <div class="grid grid-cols-4 gap-6 mb-6" v-if="listing.performance">
            <div class="bg-gray-50 dark:bg-slate-800 p-4 rounded-xl border border-gray-100 dark:border-slate-700">
              <div class="text-xs text-gray-500 mb-1">7D 销量 (件)</div>
              <div class="flex items-baseline gap-2">
                <div class="text-xl font-bold text-gray-800 dark:text-slate-100">{{ listing.performance.sales7d }}</div>
                <div v-if="listing.performance.sales7dGrowth !== undefined" class="text-xs font-bold flex items-center" :class="listing.performance.sales7dGrowth >= 0 ? 'text-green-500' : 'text-rose-500'">
                  <Icon :icon="listing.performance.sales7dGrowth >= 0 ? 'lucide:trending-up' : 'lucide:trending-down'" class="w-3 h-3 mr-0.5" />
                  {{ listing.performance.sales7dGrowth >= 0 ? '+' : '' }}{{ listing.performance.sales7dGrowth }}%
                </div>
              </div>
            </div>
            <div class="bg-indigo-50 dark:bg-indigo-900/20 p-4 rounded-xl border border-indigo-100 dark:border-indigo-800/30">
              <div class="text-xs text-indigo-500 mb-1">30D GMV</div>
              <div class="text-xl font-bold text-indigo-600 dark:text-indigo-400">{{ currency }}{{ listing.performance.gmv30d }}</div>
            </div>
            <div class="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-xl border border-emerald-100 dark:border-emerald-800/30">
              <div class="text-xs text-emerald-500 mb-1">30D 订单量</div>
              <div class="text-xl font-bold text-emerald-600 dark:text-emerald-400">{{ listing.performance.orderCount30d || 0 }}</div>
            </div>
            <div class="bg-amber-50 dark:bg-amber-900/20 p-4 rounded-xl border border-amber-100 dark:border-amber-800/30">
              <div class="text-xs text-amber-500 mb-1">30D 销量 (件)</div>
              <div class="text-xl font-bold text-amber-600 dark:text-amber-400">{{ listing.performance.sales30d }}</div>
            </div>
          </div>

          <!-- Detailed Charts -->
          <div class="mt-4 border-t pt-4">
            <ProductPerformanceStats v-if="activeTab === 'performance' && listing.platformProductCode" :asin="listing.platformProductCode" />
            <el-empty v-else-if="!listing.platformProductCode" description="暂无 ASIN 性能趋势数据" />
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

        <el-tab-pane label="刊登反馈" name="listing">
          <div v-if="feedbackLoading" class="p-8 flex justify-center">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="feedback" class="space-y-6">
            <div class="feedback-task-info p-4 bg-gray-50 dark:bg-slate-800 rounded-xl border border-gray-200 dark:border-slate-700">
               <div class="flex items-center justify-between mb-4">
                 <div class="flex items-center gap-2">
                   <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">最新任务 ID:</span>
                   <span class="font-mono text-sm font-bold text-gray-700 dark:text-slate-300">#{{ feedback.id }}</span>
                 </div>
                 <el-tag :type="getSyncStatusType(feedback.status)" size="small">
                    {{ getSyncStatusName(feedback.status) }}
                 </el-tag>
               </div>
               <div class="text-sm text-gray-600 dark:text-slate-400">
                 完成时间: {{ feedback.endTime || '进行中...' }}
               </div>
            </div>

            <div v-if="feedback.status === 3" class="error-center">
               <h3 class="text-sm font-bold text-red-600 mb-3 flex items-center gap-2">
                 <Icon icon="ep:warning" /> 错误反馈与修复建议
               </h3>
               <div class="space-y-3">
                 <div v-for="(err, idx) in parsedFeedbackErrors" :key="idx" class="p-4 rounded-xl border border-red-100 bg-red-50/30 dark:bg-red-900/10 dark:border-red-900/30">
                   <div class="font-bold text-sm text-red-700 dark:text-red-400">{{ err.code }}: {{ err.message }}</div>
                   <div v-if="err.suggestion" class="mt-2 text-xs text-gray-600 dark:text-slate-400 leading-relaxed">
                     <span class="font-bold text-amber-600">建议:</span> {{ err.suggestion }}
                   </div>
                 </div>
               </div>
            </div>
            
            <div v-if="feedback.rawFeedback" class="raw-logs mt-8">
               <div class="flex items-center justify-between mb-2">
                 <span class="text-xs font-bold text-gray-400 uppercase">原始数据日志</span>
                 <el-button link type="primary" size="small" @click="showRaw = !showRaw">{{ showRaw ? '收起' : '展开' }}</el-button>
               </div>
               <pre v-if="showRaw" class="p-3 bg-slate-900 text-slate-300 rounded-lg text-[10px] overflow-auto max-h-60 font-mono">{{ listingFeedbackRaw }}</pre>
            </div>
          </div>
          <el-empty v-else description="暂无刊登任务记录" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ListingV2VO } from '../types'
import { getListingFeedback } from '../../../../api/product/listing'
import ProductPerformanceStats from './ProductPerformanceStats.vue'

const visible = ref(false)
const contentReady = ref(false)
const listing = ref<ListingV2VO | null>(null)
const activeTab = ref('variants')
const visitedTabs = ref<Set<string>>(new Set(['variants']))

const feedback = ref<any>(null)
const feedbackLoading = ref(false)
const showRaw = ref(false)

const currency = computed(() => listing.value?.prices?.[0]?.currency || '$')

const formatRange = (prices: any[]) => {
  if (!prices || prices.length === 0) return ''
  const values = prices.map(p => p.salePrice / 100)
  const min = Math.min(...values)
  const max = Math.max(...values)
  return min === max ? `${currency.value}${min.toFixed(2)}` : `${currency.value}${min.toFixed(2)} - ${max.toFixed(2)}`
}

const allPrices = computed(() => listing.value?.prices?.filter(p => p.type?.toLowerCase() === 'all') || [])
const discountedPrices = computed(() => listing.value?.prices?.filter(p => p.type?.toLowerCase() === 'discounted') || [])

const formatPrice = (p: number) => {
  return `${currency.value}${(p / 100).toLocaleString(undefined, { minimumFractionDigits: 2 })}`
}

const open = (row: ListingV2VO) => {
  listing.value = row
  activeTab.value = 'listing'
  visitedTabs.value = new Set(['listing'])
  contentReady.value = false
  visible.value = true
}

/** Mount content after slide-in animation completes */
const onOpened = () => {
  contentReady.value = true
  fetchFeedback()
}

/** Unmount content before slide-out animation */
const onClose = () => {
  contentReady.value = false
}

const fetchFeedback = async () => {
  if (!listing.value?.latestTaskId) {
    feedback.value = null
    return
  }
  feedbackLoading.value = true
  try {
    const res = await getListingFeedback(listing.value.latestTaskId)
    feedback.value = res
  } catch (error) {
    console.error('Failed to fetch listing feedback', error)
  } finally {
    feedbackLoading.value = false
  }
}

watch(activeTab, (val) => {
  visitedTabs.value.add(val)
  if (val === 'listing' && !feedback.value) {
    fetchFeedback()
  }
})

const getSyncStatusType = (status: number): 'info' | 'success' | 'danger' | 'warning' => {
  const types: Record<number, 'info' | 'success' | 'danger' | 'warning'> = {
    0: 'info',     // 未刊登 (INIT)
    10: 'warning', // 待发布 (AUDITING)
    90: 'info',    // 发布中 (PUBLISHING)
    91: 'danger',  // 发布失败 (FAIL)
    99: 'success'  // 发布成功 (SUC)
  }
  return types[status] || 'info'
}

const getSyncStatusName = (status: number) => {
  const names: Record<number, string> = {
    0: '未刊登',
    10: '待发布',
    90: '发布中',
    91: '发布失败',
    99: '发布成功'
  }
  return names[status] || '未知'
}

const listingFeedbackRaw = computed(() => {
  if (!feedback.value?.rawFeedback) return ''
  try {
    return JSON.stringify(JSON.parse(feedback.value.rawFeedback), null, 2)
  } catch (e) {
    return feedback.value.rawFeedback
  }
})

// 解析错误反馈 (从 rawFeedback 中提取)
const parsedFeedbackErrors = computed(() => {
  if (!feedback.value?.rawFeedback) return []
  try {
    const raw = JSON.parse(feedback.value.rawFeedback)
    // 假设 ApiResponse 结构中包含错误代码和信息
    // 针对不同平台可以做更复杂的映射
    const errors: any[] = []
    
    // 如果是 ApiResponse 格式 (code, msg, data)
    if (raw.code && raw.code !== 0 && raw.code !== 1) {
       errors.push({
         code: raw.code,
         message: raw.msg || '未知错误',
         suggestion: getPlatformSuggestion(raw.code, raw.msg)
       })
    }
    
    // 如果 data 中还有更详细的错误列表 (例如 Amazon Validation Errors)
    if (raw.data?.errors && Array.isArray(raw.data.errors)) {
      raw.data.errors.forEach((e: any) => {
        errors.push({
          code: e.code || 'API_ERROR',
          message: e.message || e.msg,
          suggestion: getPlatformSuggestion(e.code, e.message || e.msg)
        })
      })
    }

    // 如果列表为空但状态是失败，展示概要信息
    if (errors.length === 0 && feedback.value.status === 3) {
      errors.push({
        code: 'FAILED',
        message: feedback.value.brief || '刊登失败，请检查原始日志',
        suggestion: '请查看下方原始数据日志以获取详细错误信息。'
      })
    }

    return errors
  } catch (e) {
    return [{ code: 'PARSE_ERROR', message: '无法解析错误反馈数据', suggestion: '数据格式异常，请联系系统管理员。' }]
  }
})

// 平台错误建议映射
const getPlatformSuggestion = (code: string | number, msg: string) => {
  const codeStr = String(code)
  if (codeStr === '90014' || msg.includes('white background')) return '请更换背景为纯白色的图片，确保无阴影和多余物品。'
  if (codeStr === '8541' || msg.includes('SKU submitted matches')) return 'SKU 已存在，请确认是否为同一商品或修改 SKU。'
  if (msg.includes('Inventory') || msg.includes('stock')) return '库存数据不合理，请检查 SKU 的库存配置。'
  if (msg.includes('Price') || msg.includes('MSRP')) return '价格验证失败，请确保售价在平台允许范围内。'
  return '请根据错误提示检查商品属性或图片，修复后重新提交刊登。'
}

defineExpose({ open })
</script>

<style scoped>
:deep(.el-drawer) {
  will-change: transform;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 4px 10px 0;
}

:deep(.el-drawer__body) {
  padding-top: 20px;
}

:deep(.custom-drawer-tabs) {
  .el-tabs__item {
    font-size: 14px;
    padding: 0 20px;
    height: 48px;
    line-height: 48px;
  }
}
</style>
