<template>
  <div class="ad-group-negative-manager">
    <div class="flex justify-between items-center mb-10px">
      <el-radio-group v-model="activeNegativeType" size="small" class="compact-radio">
        <el-radio-button label="keyword" :disabled="visibleProducts.length > 0">
          否定关键词 ({{ visibleKeywords.length }})
        </el-radio-button>
        <el-radio-button label="product" :disabled="visibleKeywords.length > 0">
          否定商品 ({{ visibleProducts.length }})
        </el-radio-button>
      </el-radio-group>

      <div class="flex gap-8px">
        <el-button 
          v-if="selectedRows.length > 0"
          size="small" 
          type="danger" 
          :loading="deleting"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedRows.length }})
        </el-button>
        <el-button 
          size="small" 
          :disabled="disabled" 
          @click="openAddDialog" 
          class="add-btn"
        >
          <el-icon><Plus /></el-icon>
          <span class="ml-4px">添加否定{{ activeNegativeType === 'keyword' ? '关键词' : '商品' }}</span>
        </el-button>
      </div>
    </div>

    <div class="p-10px border-1 border-gray-200 border-solid rounded-4px bg-gray-50 min-h-80px">
      <!-- 否定关键词表格 -->
      <div v-if="activeNegativeType === 'keyword'">
        <el-table 
          :data="visibleKeywords" 
          border 
          size="small" 
          empty-text="暂无否定关键词"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column label="关键词" prop="keywordText">
            <template #default="{ row }">
              <div class="text-12px py-2px font-medium">{{ row.keywordText }}</div>
            </template>
          </el-table-column>
          <el-table-column label="匹配类型" width="150">
            <template #default="{ row }">
              <div class="text-12px py-2px">{{ row.matchType === 'NEGATIVE_EXACT' ? '否定精准' : '否定词组' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ row }">
              <el-button type="danger" link size="small" :disabled="disabled" @click="removeNegativeKeyword(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 否定商品表格 -->
      <div v-else-if="activeNegativeType === 'product'">
        <el-table 
          :data="visibleProducts" 
          border 
          size="small" 
          empty-text="暂无否定商品"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <div class="text-13px py-4px">
                {{ row.expression[0].type === 'ASIN_SAME_AS' ? '商品 (ASIN)' : '品牌' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="值 (ASIN/品牌)" prop="expression">
            <template #default="{ row }">
              <div class="text-13px py-4px font-medium">
                {{ row.resolvedExpression?.[0]?.value || row.expression[0].value }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ row }">
              <el-button type="danger" link size="small" :disabled="disabled" @click="removeNegativeProduct(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 批量添加对话框 -->
    <NegativeKeywordAddDialog
      ref="keywordAddDialogRef"
      :shop-id="shopId"
      :ad-group-id="adGroupId"
      :existing-keywords="localKeywords"
      @success="handleRefresh"
    />
    <NegativeProductAddDialog
      ref="productAddDialogRef"
      :shop-id="shopId"
      :ad-group-id="adGroupId"
      :existing-items="localProducts"
      @success="handleRefresh"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { AmzAdvAdGroupManagerApi, AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import NegativeKeywordAddDialog from './NegativeKeywordAddDialog.vue'
import NegativeProductAddDialog from './NegativeProductAddDialog.vue'

const props = defineProps<{
  config: any
  disabled?: boolean
  adGroupId: number
  shopId: number
}>()

const emit = defineEmits(['update', 'refresh'])

const activeNegativeType = ref('keyword')
const localKeywords = ref<any[]>([])
const localProducts = ref<any[]>([])
const selectedRows = ref<any[]>([])
const deleting = ref(false)

const keywordAddDialogRef = ref()
const productAddDialogRef = ref()

// 只展示非归档状态的否定关键词/商品
const visibleKeywords = computed(() => {
  return (localKeywords.value || []).filter((k: any) => k.state !== 'ARCHIVED')
})

const visibleProducts = computed(() => {
  return (localProducts.value || []).filter((p: any) => p.state !== 'ARCHIVED')
})

const syncToConfig = () => {
  const newConfig = { ...props.config }
  newConfig.amz_negative_keyword = localKeywords.value
  newConfig.amz_negative_target_clause = localProducts.value
  emit('update', newConfig)
}

watch(() => [props.config, props.adGroupId], (newVals, oldVals) => {
  const [newConfig, newId] = newVals
  const [_, oldId] = oldVals || []
  
  if (!newConfig) return
  const forceReset = oldId !== undefined && newId !== oldId

  // 只有当本地列表为空时，或者广告组 ID 发生变化时才初始化，避免编辑过程中的覆盖
  if (forceReset || (localKeywords.value.length === 0 && localProducts.value.length === 0)) {
    localKeywords.value = JSON.parse(JSON.stringify(newConfig.amz_negative_keyword || []))
    localProducts.value = JSON.parse(JSON.stringify(newConfig.amz_negative_target_clause || []))
  }
}, { immediate: true, deep: true })



const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const openAddDialog = () => {
  if (activeNegativeType.value === 'keyword') {
    keywordAddDialogRef.value?.open()
  } else {
    productAddDialogRef.value?.open()
  }
}

const handleRefresh = async () => {
  const res = await AdsAdGroupApi.getAttributes(props.adGroupId)
  if (res) {
    localKeywords.value = res.amz_negative_keyword || []
    localProducts.value = res.amz_negative_target_clause || []
    syncToConfig()
  }
  emit('refresh')
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认批量删除选中的 ${selectedRows.value.length} 项吗？`, '提示', { type: 'warning' })
    
    deleting.value = true
    const persistIds = selectedRows.value
      .map(r => r.keywordId || r.targetId)
      .filter(id => !!id)
      
    if (persistIds.length > 0) {
      if (activeNegativeType.value === 'keyword') {
        await AmzAdvAdGroupManagerApi.batchDeleteNegativeKeyword({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: persistIds
        })
      } else {
        await AmzAdvAdGroupManagerApi.batchDeleteNegativeTargeting({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: persistIds
        })
      }
    }
    
    // 更新本地列表
    if (activeNegativeType.value === 'keyword') {
      localKeywords.value = localKeywords.value.filter(
        (k: any) => !selectedRows.value.includes(k)
      )
    } else {
      localProducts.value = localProducts.value.filter(
        (p: any) => !selectedRows.value.includes(p)
      )
    }
    
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    syncToConfig()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('批量删除失败')
  } finally {
    deleting.value = false
  }
}

const removeNegativeKeyword = async (row: any) => {
  try {
    if (row.keywordId) {
      await AmzAdvAdGroupManagerApi.batchDeleteNegativeKeyword({
        shopId: props.shopId,
        groupId: props.adGroupId,
        ids: [row.keywordId]
      })
    }
    const index = localKeywords.value.indexOf(row)
    if (index > -1) {
      localKeywords.value.splice(index, 1)
    }
    syncToConfig()
    ElMessage.success('删除成功')
  } catch(e) {
    ElMessage.error('删除失败')
  }
}

const removeNegativeProduct = async (row: any) => {
  try {
    if (row.targetId) {
      await AmzAdvAdGroupManagerApi.batchDeleteNegativeTargeting({
        shopId: props.shopId,
        groupId: props.adGroupId,
        ids: [row.targetId]
      })
    }
    const index = localProducts.value.indexOf(row)
    if (index > -1) {
      localProducts.value.splice(index, 1)
    }
    syncToConfig()
    ElMessage.success('删除成功')
  } catch(e) {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.ad-group-negative-manager :deep(.el-radio-button__inner) {
  padding: 5px 12px;
  font-size: 12px;
}

.ad-group-negative-manager :deep(.el-table .cell) {
  padding-left: 8px;
  padding-right: 8px;
}

.ad-group-negative-manager :deep(.el-table--small .el-table__cell) {
  padding: 4px 0;
}

.compact-input :deep(.el-input__inner) {
  height: 24px;
}

.add-btn {
  border: 1px solid var(--el-border-color);
  background: #fff;
  color: var(--el-text-color-regular);
  height: 24px;
  padding: 0 8px;
  font-size: 12px;
}
.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
