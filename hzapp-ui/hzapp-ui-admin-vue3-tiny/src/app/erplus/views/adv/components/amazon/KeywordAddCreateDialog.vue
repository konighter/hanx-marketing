<template>
  <el-dialog v-model="visible" title="添加关键词" width="1000px" append-to-body>
    <div class="flex gap-24px h-550px p-4px">
      <!-- 左侧输入框 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-start gap-8px">
            <span class="text-13px font-medium whitespace-nowrap pt-4px">匹配方式:</span>
            <el-checkbox-group v-model="selectedMatchTypes" size="small" class="flex flex-wrap gap-x-10px gap-y-2px">
              <el-checkbox label="EXACT">精准</el-checkbox>
              <el-checkbox label="PHRASE">词组</el-checkbox>
              <el-checkbox label="BROAD">广泛</el-checkbox>
            </el-checkbox-group>
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

        <div class="mb-12px">
          <el-input
            v-model="searchKeyword"
            placeholder="输入核心词按回车触发推荐"
            size="small"
            clearable
            @keyup.enter="handleSearchInput"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleAddInputKeyword">添加</el-button>
            </template>
          </el-input>
        </div>
        
        <div v-if="recommendedKeywords.length > 0 || searching" v-loading="searching" class="mb-12px border-1 border-blue-100 border-solid rounded-4px flex flex-col flex-1 overflow-hidden">

          <div class="flex-1 overflow-y-auto">
            <el-table :data="recommendedKeywords" size="small" :span-method="spanMethod" style="width: 100%; height: 100%" border>
              <el-table-column label="推荐关键词" prop="keyword" min-width="150">
                <template #default="{ row }">
                  <span class="font-medium text-13px text-gray-700">{{ row.keyword }}</span>
                </template>
              </el-table-column>
              <el-table-column label="匹配方式" prop="matchType" width="100" align="center">
                <template #default="{ row }">
                  <el-tag size="small" type="info" effect="plain" class="w-40px text-center px-0">
                    {{ row.matchType === 'EXACT' ? '精准' : row.matchType === 'PHRASE' ? '词组' : row.matchType === 'BROAD' ? '广泛' : row.matchType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="出价" min-width="150">
                <template #default="{ row }">
                  <div class="flex flex-col justify-center">
                    <span class="text-12px text-gray-600">推荐: ${{ row.suggestedBid?.rangeMedian || row.bid || '-' }}</span>
                    <span v-if="row.suggestedBid?.rangeStart" class="text-11px text-gray-400 mt-2px">
                      (最低: ${{ row.suggestedBid.rangeStart }} 最高: ${{ row.suggestedBid.rangeEnd }})
                    </span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60" align="center">
                <template #default="{ row }">
                  <el-button size="small" type="primary" link @click="addSingleRecommendedKeyword(row.keyword, row)">添加</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>


      </div>
      
      <!-- 右侧已添加的关键词和出价 -->
      <div class="flex-1 border-1 border-gray-200 border-solid rounded-4px p-12px flex flex-col h-full overflow-hidden bg-white shadow-sm">
        <div class="flex justify-between items-center mb-12px">
          <span class="font-bold text-14px text-gray-700">待添加列表</span>
          <el-tag size="small" type="info" effect="dark">{{ pendingKeywords.length }}</el-tag>
        </div>
        <div class="flex-1 overflow-y-auto">
          <el-table :data="pendingKeywords" size="small" height="100%">
            <el-table-column label="关键词" prop="keywordText" show-overflow-tooltip />
            <el-table-column label="匹配" prop="matchType" width="70">
              <template #default="{ row }">
                <span v-if="row.matchType === 'EXACT'">精准</span>
                <span v-else-if="row.matchType === 'PHRASE'">词组</span>
                <span v-else-if="row.matchType === 'BROAD'">广泛</span>
              </template>
            </el-table-column>
            <el-table-column label="出价" prop="bid" width="90">
              <template #default="{ row }">
                <el-input-number v-model="row.bid" :precision="2" size="small" :controls="false" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="50" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" @click="pendingKeywords.splice($index, 1)">
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
      <el-button type="primary" @click="submitKeywords">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, inject, unref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Search } from '@element-plus/icons-vue'
import { AmzAdvHelpApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  defaultBid: number
  existingKeywords?: any[]
  campaignId?: number
  adGroupId?: number
  adAsins?: string[]
}>()

const shopId = inject<any>('shopId')
const emit = defineEmits(['success'])

const visible = ref(false)
const selectedMatchTypes = ref(['EXACT'])
const bidStrategy = ref('fixed')
const fixedBid = ref(props.defaultBid || 1.0)
const bidAdjustment = ref(0)
const pendingKeywords = ref<any[]>([])

const searchKeyword = ref('')
const recommendedKeywords = ref<any[]>([])
const searching = ref(false)

const open = async () => {
  visible.value = true
  pendingKeywords.value = []
  searchKeyword.value = ''
  recommendedKeywords.value = []
  fixedBid.value = props.defaultBid || 1.0
  bidStrategy.value = 'fixed'
  bidAdjustment.value = 0
  

}

const handleSearchInput = async () => {
  const val = searchKeyword.value?.trim()
  if (!val || val.length < 2) {
    recommendedKeywords.value = []
    return
  }
  
  const currentShopId = unref(shopId)
  if (!currentShopId) return
  
  searching.value = true
  try {
    const targets = selectedMatchTypes.value.length > 0 
      ? selectedMatchTypes.value.map(type => ({ value: val, type: type }))
      : [{ value: val, type: 'BROAD' }]
      
    const payload: any = {
      shopId: currentShopId,
      recommendationType: 'KEYWORDS_FOR_ASINS', // 默认按搜索词
      targets: targets,
      maxRecommendations: 50
    }
    if (props.adAsins && props.adAsins.length > 0) {
      payload.asins = props.adAsins
    }
    const res = await AmzAdvHelpApi.getKeywordRecommendations(payload)
    
    if (res && res.recommendations) {
      const grouped = res.recommendations.reduce((acc: any, r: any) => {
        if (!acc[r.keyword]) acc[r.keyword] = []
        acc[r.keyword].push(r)
        return acc
      }, {})
      const flattened: any[] = []
      Object.keys(grouped).forEach(k => {
        const items = grouped[k]
        items.forEach((item: any, index: number) => {
          flattened.push({
            ...item,
            rowspan: index === 0 ? items.length : 0,
            rowspanIndex: index
          })
        })
      })
      recommendedKeywords.value = flattened
    } else if (Array.isArray(res)) {
      const grouped = res.reduce((acc: any, r: any) => {
        const k = r.keyword || r
        if (!acc[k]) acc[k] = []
        if (r.keyword) acc[k].push(r)
        return acc
      }, {})
      const flattened: any[] = []
      Object.keys(grouped).forEach(k => {
        const items = grouped[k]
        if (items.length === 0) {
          flattened.push({ keyword: k, matchType: 'BROAD', rowspan: 1, rowspanIndex: 0 })
        } else {
          items.forEach((item: any, index: number) => {
            flattened.push({
              ...item,
              rowspan: index === 0 ? items.length : 0,
              rowspanIndex: index
            })
          })
        }
      })
      recommendedKeywords.value = flattened
    }
  } catch (e) {
    console.error(e)
  } finally {
    searching.value = false
  }
}

const spanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
    if (row.rowspanIndex === 0) {
      return { rowspan: row.rowspan, colspan: 1 }
    } else {
      return { rowspan: 0, colspan: 0 }
    }
  }
}

const getCalculatedBid = () => {
  if (bidStrategy.value === 'fixed') return fixedBid.value
  const base = props.defaultBid || 1.0
  return Number((base * (1 + bidAdjustment.value / 100)).toFixed(2))
}

const addRecommendedKeyword = (k: string) => {
  if (selectedMatchTypes.value.length === 0) {
    ElMessage.warning('请至少选择一种匹配方式')
    return
  }
  selectedMatchTypes.value.forEach(matchType => {
    const isInPending = pendingKeywords.value.some(p => p.keywordText?.trim().toLowerCase() === k.trim().toLowerCase() && p.matchType === matchType)
    const isExisted = props.existingKeywords?.some(p => 
      p.state === 'ENABLED' && 
      p.keywordText?.trim().toLowerCase() === k.trim().toLowerCase() && 
      p.matchType === matchType
    )

    if (!isInPending && !isExisted) {
      pendingKeywords.value.push({
        keywordText: k,
        matchType: matchType,
        bid: getCalculatedBid(),
        state: 'ENABLED'
      })
    }
  })
}

const addSingleRecommendedKeyword = (k: string, item: any) => {
  const matchType = item.matchType
  const isInPending = pendingKeywords.value.some(p => p.keywordText?.trim().toLowerCase() === k.trim().toLowerCase() && p.matchType === matchType)
  const isExisted = props.existingKeywords?.some(p => 
    p.state === 'ENABLED' && 
    p.keywordText?.trim().toLowerCase() === k.trim().toLowerCase() && 
    p.matchType === matchType
  )

  if (!isInPending && !isExisted) {
    let finalBid = getCalculatedBid()
    if (bidStrategy.value === 'suggested' && item.suggestedBid?.rangeMedian) {
       finalBid = item.suggestedBid.rangeMedian
    } else if (bidStrategy.value === 'suggested' && item.bid) {
       finalBid = item.bid
    }
    pendingKeywords.value.push({
      keywordText: k,
      matchType: matchType,
      bid: finalBid,
      state: 'ENABLED'
    })
  }
}

const addRecommendedKeywordGroup = (row: any) => {
  if (row.items && row.items.length > 0) {
    row.items.forEach((item: any) => addSingleRecommendedKeyword(row.keyword, item))
  } else {
    addRecommendedKeyword(row.keyword)
  }
}

const handleAddInputKeyword = () => {
  const text = searchKeyword.value?.trim()
  if (!text) return
  if (selectedMatchTypes.value.length === 0) {
    ElMessage.warning('请至少选择一种匹配方式')
    return
  }
  
  let skipCount = 0
  
  selectedMatchTypes.value.forEach(matchType => {
    const isInPending = pendingKeywords.value.some(k => k.keywordText?.trim().toLowerCase() === text.toLowerCase() && k.matchType === matchType)
    const isExisted = props.existingKeywords?.some(k => 
      k.state === 'ENABLED' && 
      k.keywordText?.trim().toLowerCase() === text.toLowerCase() && 
      k.matchType === matchType
    )
    
    if (!isInPending && !isExisted) {
      pendingKeywords.value.push({
        keywordText: text,
        matchType: matchType,
        bid: getCalculatedBid(),
        state: 'ENABLED'
      })
    } else {
      skipCount++
    }
  })
  
}

const submitKeywords = () => {
  if (pendingKeywords.value.length === 0) {
    ElMessage.warning('没有要添加的关键词')
    return
  }
  emit('success', pendingKeywords.value)
  visible.value = false
}

defineExpose({ open })
</script>
