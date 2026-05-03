<template>
  <el-dialog v-model="visible" title="添加商品/品类定向" width="1000px" append-to-body>
    <div class="flex gap-24px h-600px p-4px">
      <!-- 左侧输入/搜索 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-center gap-8px">
            <span class="text-13px font-medium whitespace-nowrap">选择方式:</span>
            <el-radio-group v-model="entryMode" size="small">
              <el-radio label="manual">手动输入</el-radio>
              <el-radio label="suggested">推荐商品</el-radio>
            </el-radio-group>
          </div>

          <div v-if="entryMode === 'manual'" class="flex items-center gap-8px">
            <span class="text-13px font-medium whitespace-nowrap">定向类型:</span>
            <el-radio-group v-model="selectedType" size="small">
              <el-radio label="asinSameAs">商品 (ASIN)</el-radio>
              <el-radio label="category">类目</el-radio>
              <el-radio label="brand">品牌</el-radio>
            </el-radio-group>
          </div>
          
          <div class="flex flex-col gap-8px pt-2px">
            <div class="flex items-center gap-12px">
              <span class="text-13px font-medium whitespace-nowrap">出价设置:</span>
              <el-radio-group v-model="bidStrategy" size="small" class="flex flex-nowrap gap-x-10px">
                <el-radio label="fixed">固定</el-radio>
                <el-radio label="suggested">推荐</el-radio>
                <el-radio label="min">最低</el-radio>
                <el-radio label="max">最高</el-radio>
              </el-radio-group>
            </div>
            
            <div class="flex items-center gap-6px ml-68px">
              <template v-if="bidStrategy === 'fixed'">
                <el-input-number v-model="fixedBid" size="small" :precision="2" :step="0.01" :controls="false" style="width: 80px" />
                <span class="text-12px text-gray-400">USD</span>
              </template>
              <template v-else>
                <span class="text-12px text-gray-500">调整:</span>
                <el-input-number v-model="bidAdjustment" size="small" :step="1" :controls="false" style="width: 60px" />
                <span class="text-12px text-gray-500">%</span>
                <span class="text-11px text-gray-400 ml-4px">(10表示+10%, -10表示-10%)</span>
              </template>
            </div>
          </div>
        </div>

        <!-- 手动输入模式 -->
        <template v-if="entryMode === 'manual'">
          <el-input 
            v-model="bulkValues" 
            type="textarea" 
            style="flex: 1"
            :input-style="{ height: '100%' }"
            :placeholder="getPlaceholder()"
          />
          <el-button class="mt-10px" @click="parseValues">添加到列表 >></el-button>
        </template>

        <!-- 推荐商品模式 -->
        <template v-else>
          <div class="flex-1 flex flex-col overflow-hidden border-1 border-gray-100 border-solid rounded-4px">
            <div class="p-8px bg-gray-50 border-b-1 border-gray-100 border-solid flex justify-between items-center">
              <span class="text-12px font-medium">系统推荐投放 ASIN</span>
              <el-button size="mini" :loading="loadingSuggested" @click="fetchSuggestedTargets">刷新推荐</el-button>
            </div>
            <div class="flex-1 overflow-y-auto p-8px" v-loading="loadingSuggested">
              <el-table :data="suggestedList" size="small" @selection-change="handleSuggestedSelection">
                <el-table-column type="selection" width="40" />
                <el-table-column label="产品" min-width="150">
                  <template #default="{ row }">
                    <div class="flex items-center gap-8px">
                      <el-image :src="row.imageUrl" class="w-32px h-32px rounded-2px flex-shrink-0" />
                      <div class="flex flex-col min-w-0">
                        <div class="text-11px line-clamp-1" :title="row.title">{{ row.title }}</div>
                        <div class="text-10px text-gray-400">ASIN: {{ row.asin }}</div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="建议出价" width="80" align="center">
                  <template #default="{ row }">
                    <span v-if="row.suggestedBid">{{ row.suggestedBid }}</span>
                    <span v-else class="text-gray-300">-</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-button class="m-8px" type="primary" plain size="small" @click="addSuggestedToPending" :disabled="selectedSuggested.length === 0">
              添加选中的推荐产品 >>
            </el-button>
          </div>
        </template>
      </div>
      
      <!-- 右侧待添加列表 -->
      <div class="flex-1 border-1 border-gray-200 border-solid rounded-4px p-12px flex flex-col h-full overflow-hidden bg-white shadow-sm">
        <div class="flex justify-between items-center mb-12px">
          <span class="font-bold text-14px text-gray-700">待添加列表</span>
          <el-tag size="small" type="info" effect="dark">{{ pendingItems.length }}</el-tag>
        </div>
        <div class="flex-1 overflow-y-auto">
          <el-table :data="pendingItems" size="small" height="100%">
            <el-table-column label="值" prop="value" show-overflow-tooltip />
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <span v-if="row.type === 'asinSameAs'">ASIN</span>
                <span v-else-if="row.type === 'category'">类目</span>
                <span v-else-if="row.type === 'brand'">品牌</span>
              </template>
            </el-table-column>
            <el-table-column label="出价" prop="bid" width="90">
              <template #default="{ row }">
                <el-input-number v-model="row.bid" :precision="2" size="small" :controls="false" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="50" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" @click="pendingItems.splice($index, 1)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitItems">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, inject, watch, unref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { AmzAdvHelpApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  defaultBid: number
  existingItems?: any[]
  adAsins?: string[]
}>()

const shopId = inject<any>('shopId')
const emit = defineEmits(['success'])

const visible = ref(false)
const entryMode = ref('manual')
const selectedType = ref('asinSameAs')
const bulkValues = ref('')
const bidStrategy = ref('fixed')
const fixedBid = ref(props.defaultBid || 1.0)
const bidAdjustment = ref(0)
const pendingItems = ref<any[]>([])

const loadingSuggested = ref(false)
const suggestedList = ref<any[]>([])
const selectedSuggested = ref<any[]>([])

const open = () => {
  visible.value = true
  bulkValues.value = ''
  pendingItems.value = []
  fixedBid.value = props.defaultBid || 1.0
  bidStrategy.value = 'fixed'
  bidAdjustment.value = 0
  entryMode.value = 'manual'
  suggestedList.value = []
}

const fetchSuggestedTargets = async () => {
  const currentShopId = unref(shopId)
  if (!currentShopId || !props.adAsins?.length) return
  
  loadingSuggested.value = true
  try {
    const res = await AmzAdvHelpApi.getProductRecommendations({
      shopId: currentShopId,
      adAsins: props.adAsins,
      count: 50
    })
    
    // 假设返回结构包含 recommendations
    if (res && res.recommendations) {
      suggestedList.value = res.recommendations.map((r: any) => ({
        asin: r.asin,
        title: r.title,
        imageUrl: r.imageUrl,
        suggestedBid: r.suggestedBid
      }))
    } else if (Array.isArray(res)) {
      suggestedList.value = res
    }
  } catch (e) {
    console.error(e)
  } finally {
    loadingSuggested.value = false
  }
}

watch(entryMode, (val) => {
  if (val === 'suggested' && suggestedList.value.length === 0) {
    fetchSuggestedTargets()
  }
})

const handleSuggestedSelection = (selection: any[]) => {
  selectedSuggested.value = selection
}

const addSuggestedToPending = () => {
  selectedSuggested.value.forEach(item => {
    const isInPending = pendingItems.value.some(p => p.value === item.asin && p.type === 'asinSameAs')
    const isExisted = props.existingItems?.some(p => p.expressionValue === item.asin && p.expressionType === 'asinSameAs')
    
    if (!isInPending && !isExisted) {
      pendingItems.value.push({
        value: item.asin,
        type: 'asinSameAs',
        bid: item.suggestedBid || getCalculatedBid(),
        state: 'ENABLED'
      })
    }
  })
  selectedSuggested.value = []
  ElMessage.success('已添加到待添加列表')
}

const getPlaceholder = () => {
  switch (selectedType.value) {
    case 'asinSameAs': return '手动输入 ASIN，每行一个'
    case 'category': return '手动输入类目 ID，每行一个'
    case 'brand': return '手动输入品牌 ID，每行一个'
    default: return '请输入，每行一个'
  }
}

const getCalculatedBid = () => {
  if (bidStrategy.value === 'fixed') return fixedBid.value
  const base = props.defaultBid || 1.0
  return Number((base * (1 + bidAdjustment.value / 100)).toFixed(2))
}

const parseValues = () => {
  if (!bulkValues.value) return
  
  const lines = bulkValues.value.split('\n').map(l => l.trim()).filter(l => l)
  let skipCount = 0
  
  lines.forEach(text => {
    const isInPending = pendingItems.value.some(k => k.value?.trim().toLowerCase() === text.trim().toLowerCase() && k.type === selectedType.value)
    const isExisted = props.existingItems?.some(p => 
      p.state === 'ENABLED' && 
      p.expressionValue?.trim().toLowerCase() === text.trim().toLowerCase() && 
      p.expressionType === selectedType.value
    )
    
    if (!isInPending && !isExisted) {
      pendingItems.value.push({
        value: text,
        type: selectedType.value,
        bid: getCalculatedBid(),
        state: 'ENABLED'
      })
    } else {
      skipCount++
    }
  })
  
  if (skipCount > 0) {
    ElMessage.info(`已自动过滤 ${skipCount} 个重复的定向项`)
  }
  bulkValues.value = ''
}

const submitItems = () => {
  if (pendingItems.value.length === 0) {
    ElMessage.warning('没有要添加的定向项')
    return
  }
  
  const mappedItems = pendingItems.value.map((k: any) => ({
    state: 'ENABLED',
    expressionType: k.type,
    expressionValue: k.value,
    bid: k.bid
  }))
  
  emit('success', mappedItems)
  visible.value = false
}

defineExpose({ open })
</script>
