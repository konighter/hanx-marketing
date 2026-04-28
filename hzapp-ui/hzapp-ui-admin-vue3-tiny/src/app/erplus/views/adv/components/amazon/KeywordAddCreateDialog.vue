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
            placeholder="输入核心词触发推荐"
            size="small"
            clearable
            @input="handleSearchInput"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <div v-if="recommendedKeywords.length > 0" class="mb-12px p-12px bg-blue-50 border-1 border-blue-100 border-solid rounded-4px max-h-120px overflow-y-auto">
          <div class="text-12px text-blue-600 font-medium mb-8px">推荐关键词 (点击添加)</div>
          <div class="flex flex-wrap gap-6px">
            <el-tag 
              v-for="k in recommendedKeywords" 
              :key="k" 
              size="small" 
              effect="plain"
              class="cursor-pointer hover:bg-blue-100 border-blue-200"
              @click="addRecommendedKeyword(k)"
            >
              {{ k }}
            </el-tag>
          </div>
        </div>

        <el-input 
          v-model="bulkKeywords" 
          type="textarea" 
          style="flex: 1"
          :input-style="{ height: '100%' }"
          placeholder="手动输入关键词，每行一个"
        />
        <el-button class="mt-10px" @click="parseKeywords">添加到列表 >></el-button>
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
      <el-button type="primary" @click="submitKeywords">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Search } from '@element-plus/icons-vue'

const props = defineProps<{
  defaultBid: number
  existingKeywords?: any[]
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const selectedMatchTypes = ref(['EXACT'])
const bulkKeywords = ref('')
const bidStrategy = ref('fixed')
const fixedBid = ref(props.defaultBid || 1.0)
const bidAdjustment = ref(0)
const pendingKeywords = ref<any[]>([])

const searchKeyword = ref('')
const recommendedKeywords = ref<string[]>([])
let recommendTimer: any = null

const open = () => {
  visible.value = true
  bulkKeywords.value = ''
  pendingKeywords.value = []
  searchKeyword.value = ''
  recommendedKeywords.value = []
  fixedBid.value = props.defaultBid || 1.0
  bidStrategy.value = 'fixed'
  bidAdjustment.value = 0
}

const handleSearchInput = (val: string) => {
  if (recommendTimer) clearTimeout(recommendTimer)
  if (!val) {
    recommendedKeywords.value = []
    return
  }
  recommendTimer = setTimeout(() => {
    recommendedKeywords.value = [
      val + ' cases',
      val + ' for men',
      val + ' for women',
      val + ' accessories',
      'best ' + val,
      val + ' 2024',
      'cheap ' + val
    ]
  }, 500)
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

const parseKeywords = () => {
  if (!bulkKeywords.value) return
  if (selectedMatchTypes.value.length === 0) {
    ElMessage.warning('请至少选择一种匹配方式')
    return
  }
  const lines = bulkKeywords.value.split('\n').map(l => l.trim()).filter(l => l)
  let skipCount = 0
  
  lines.forEach(text => {
    selectedMatchTypes.value.forEach(matchType => {
      const isInPending = pendingKeywords.value.some(k => k.keywordText?.trim().toLowerCase() === text.trim().toLowerCase() && k.matchType === matchType)
      const isExisted = props.existingKeywords?.some(k => 
        k.state === 'ENABLED' && 
        k.keywordText?.trim().toLowerCase() === text.trim().toLowerCase() && 
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
  })
  
  if (skipCount > 0) {
    ElMessage.info(`已自动过滤 ${skipCount} 个重复的关键词`)
  }
  bulkKeywords.value = ''
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
