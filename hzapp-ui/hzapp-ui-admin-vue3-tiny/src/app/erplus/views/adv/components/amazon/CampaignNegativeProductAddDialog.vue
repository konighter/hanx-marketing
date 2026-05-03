<template>
  <el-dialog v-model="visible" title="添加活动级否定商品" width="800px" append-to-body>
    <div class="flex gap-24px h-450px p-4px">
      <!-- 左侧输入框 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-start gap-8px">
            <span class="text-13px font-medium whitespace-nowrap pt-4px">否定类型:</span>
            <el-radio-group v-model="selectedType" size="small">
              <el-radio label="ASIN_SAME_AS">商品 (ASIN)</el-radio>
              <el-radio label="ASIN_BRAND_SAME_AS">品牌</el-radio>
            </el-radio-group>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="flex-1 flex flex-col overflow-hidden custom-tabs">
          <el-tab-pane label="手动输入" name="manual" class="h-full flex flex-col pt-12px">
            <el-input 
              v-model="bulkValues" 
              type="textarea" 
              style="flex: 1"
              :input-style="{ height: '100%' }"
              :placeholder="selectedType === 'ASIN_SAME_AS' ? '手动输入 ASIN，每行一个' : '手动输入品牌 ID，每行一个'"
            />
            <el-button class="mt-10px" @click="parseItems">添加到列表 >></el-button>
          </el-tab-pane>
          <el-tab-pane v-if="selectedType === 'ASIN_BRAND_SAME_AS'" label="推荐品牌" name="search" class="h-full flex flex-col pt-12px">
            <div class="mb-12px">
              <el-input 
                v-model="brandKeyword" 
                placeholder="搜索品牌名称..." 
                clearable
                @keyup.enter="searchBrands"
              >
                <template #append>
                  <el-button :loading="loadingBrands" @click="searchBrands">搜索</el-button>
                </template>
              </el-input>
            </div>
            <div class="flex-1 overflow-hidden border border-gray-100 rounded">
              <el-table v-loading="loadingBrands" :data="brandRecommendations" size="small" height="100%" :show-header="false">
                <el-table-column prop="name">
                  <template #default="{ row }">
                    <div class="flex flex-col">
                      <span class="font-medium">{{ row.name }}</span>
                      <span class="text-11px text-gray-400">ID: {{ row.id }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column width="60" align="right">
                  <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="addBrand(row)">添加</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 右侧已添加的列表 -->
      <div class="flex-1 border-1 border-gray-200 border-solid rounded-4px p-12px flex flex-col h-full overflow-hidden bg-white shadow-sm">
        <div class="flex justify-between items-center mb-12px">
          <span class="font-bold text-14px text-gray-700">待添加列表</span>
          <el-tag size="small" type="info" effect="dark">{{ pendingItems.length }}</el-tag>
        </div>
        <div class="flex-1 overflow-y-auto">
          <el-table :data="pendingItems" size="small" height="100%">
            <el-table-column label="值" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ row.name ? `${row.name} (${row.value})` : row.value }}</span>
              </template>
            </el-table-column>
            <el-table-column label="类型" prop="type" width="100">
              <template #default="{ row }">
                <span>{{ row.type === 'ASIN_SAME_AS' ? '商品' : '品牌' }}</span>
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
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { AmzAdvCampaignManagerApi, AmzAdvHelpApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  shopId: number
  campaignId: number
  existingItems?: any[]
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const selectedType = ref('ASIN_SAME_AS')
const bulkValues = ref('')
const pendingItems = ref<any[]>([])
const savingItems = ref(false)

const activeTab = ref('manual')
const brandKeyword = ref('')
const brandRecommendations = ref<any[]>([])
const loadingBrands = ref(false)

watch(selectedType, (val) => {
  if (val === 'ASIN_SAME_AS') {
    activeTab.value = 'manual'
  }
})

const searchBrands = async () => {
  loadingBrands.value = true
  try {
    const res = await AmzAdvHelpApi.getNegativeBrandRecommendations({
      shopId: props.shopId,
      keyword: brandKeyword.value
    })
    brandRecommendations.value = res || []
  } catch (e) {
    ElMessage.error('获取品牌推荐失败')
  } finally {
    loadingBrands.value = false
  }
}

const addBrand = (brand: any) => {
  const val = brand.id
  const isInPending = pendingItems.value.some(i => i.value === val && i.type === 'ASIN_BRAND_SAME_AS')
  const isExisted = props.existingItems?.some(i => {
    const expr = i.expression?.[0]
    return i.state === 'ENABLED' && expr?.value === val && expr?.type === 'ASIN_BRAND_SAME_AS'
  })

  if (!isInPending && !isExisted) {
    pendingItems.value.push({
      value: val,
      type: 'ASIN_BRAND_SAME_AS',
      name: brand.name // 保存名称用于显示
    })
  } else {
    ElMessage.info('该品牌已在列表中')
  }
}

const open = () => {
  visible.value = true
  bulkValues.value = ''
  pendingItems.value = []
}

const parseItems = () => {
  if (!bulkValues.value) return
  const lines = bulkValues.value.split('\n').map(l => l.trim()).filter(l => l)
  let skipCount = 0
  
  lines.forEach(val => {
    const isInPending = pendingItems.value.some(i => i.value?.trim().toLowerCase() === val.trim().toLowerCase() && i.type === selectedType.value)
    const isExisted = props.existingItems?.some(i => {
      const expr = i.expression?.[0]
      return i.state === 'ENABLED' && expr?.value?.trim().toLowerCase() === val.trim().toLowerCase() && expr?.type === selectedType.value
    })
    
    if (!isInPending && !isExisted) {
      pendingItems.value.push({
        value: val,
        type: selectedType.value
      })
    } else {
      skipCount++
    }
  })
  
  if (skipCount > 0) {
    ElMessage.info(`已自动过滤 ${skipCount} 个重复的否定商品`)
  }
  bulkValues.value = ''
}

const submitItems = async () => {
  if (pendingItems.value.length === 0) {
    ElMessage.warning('没有要添加的否定商品')
    return
  }
  savingItems.value = true
  try {
    await AmzAdvCampaignManagerApi.batchCreateNegativeTargeting({
      shopId: props.shopId,
      campaignId: props.campaignId,
      clauses: pendingItems.value.map((i: any) => ({
        expression: [{ type: i.type, value: i.value }],
        state: 'ENABLED',
        campaignId: props.campaignId
      }))
    })
    ElMessage.success('否定商品添加成功')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error('否定商品添加失败')
  } finally {
    savingItems.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.custom-tabs :deep(.el-tabs__content) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.custom-tabs :deep(.el-tab-pane) {
  height: 100%;
}
</style>
