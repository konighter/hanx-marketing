<template>
  <el-dialog v-model="visible" title="添加商品/品类定向" width="900px" append-to-body>
    <div class="flex gap-24px h-550px p-4px">
      <!-- 左侧输入框 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-center gap-8px">
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

        <el-input 
          v-model="bulkValues" 
          type="textarea" 
          style="flex: 1"
          :input-style="{ height: '100%' }"
          :placeholder="getPlaceholder()"
        />
        <el-button class="mt-10px" @click="parseValues">添加到列表 >></el-button>
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
      <el-button type="primary" :loading="savingItems" @click="submitItems">确认添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { AmzAdvAdGroupManagerApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  shopId: number
  adGroupId: number
  defaultBid: number
  existingItems?: any[]
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const selectedType = ref('asinSameAs')
const bulkValues = ref('')
const bidStrategy = ref('fixed')
const fixedBid = ref(props.defaultBid || 1.0)
const bidAdjustment = ref(0)
const pendingItems = ref<any[]>([])
const savingItems = ref(false)

const open = () => {
  visible.value = true
  bulkValues.value = ''
  pendingItems.value = []
  fixedBid.value = props.defaultBid || 1.0
  bidStrategy.value = 'fixed'
  bidAdjustment.value = 0
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
    let typeEnum = 'ASIN_SAME_AS'
    if (selectedType.value === 'category') typeEnum = 'ASIN_CATEGORY_SAME_AS'
    if (selectedType.value === 'brand') typeEnum = 'ASIN_BRAND_SAME_AS'

    const isExisted = props.existingItems?.some(p => 
      p.state === 'ENABLED' && 
      p.expression?.[0]?.value?.trim().toLowerCase() === text.trim().toLowerCase() && 
      p.expression?.[0]?.type === typeEnum
    )
    
    if (!isInPending && !isExisted) {
      pendingItems.value.push({
        value: text,
        type: selectedType.value,
        bid: getCalculatedBid()
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

const submitItems = async () => {
  if (pendingItems.value.length === 0) {
    ElMessage.warning('没有要添加的定向项')
    return
  }
  savingItems.value = true
  try {
    await AmzAdvAdGroupManagerApi.batchCreateTargeting({
      shopId: props.shopId,
      groupId: props.adGroupId,
      clauses: pendingItems.value.map((k: any) => {
        let typeEnum = 'ASIN_SAME_AS'
        if (k.type === 'category') typeEnum = 'ASIN_CATEGORY_SAME_AS'
        if (k.type === 'brand') typeEnum = 'ASIN_BRAND_SAME_AS'

        return {
          state: 'ENABLED',
          expressionType: 'MANUAL',
          expression: [
            {
              type: typeEnum,
              value: k.value
            }
          ],
          bid: k.bid
        }
      })
    })
    ElMessage.success('定向项添加成功')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error('定向项添加失败')
  } finally {
    savingItems.value = false
  }
}

defineExpose({ open })
</script>
