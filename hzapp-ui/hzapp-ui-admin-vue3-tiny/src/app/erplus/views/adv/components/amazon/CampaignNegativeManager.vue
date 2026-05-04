<template>
  <div class="campaign-negative-manager">
    <div class="flex justify-between items-center mb-12px">
      <el-radio-group v-model="activeNegativeType" size="small" class="compact-radio">
        <el-radio-button label="keyword">否定关键词 ({{ visibleKeywords.length }})</el-radio-button>
        <el-radio-button label="product">否定商品 ({{ visibleProducts.length }})</el-radio-button>
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

    <!-- 否定内容区域 -->
    <div class="negative-content">
      <!-- 否定关键词表格 -->
      <div v-if="activeNegativeType === 'keyword'">
        <el-table 
          :data="visibleKeywords" 
          border 
          size="small" 
          empty-text="暂无否定关键词"
          class="!rounded-4px overflow-hidden"
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
      <div v-if="activeNegativeType === 'product'">
        <el-table 
          :data="visibleProducts" 
          border 
          size="small" 
          empty-text="暂无否定商品"
          class="!rounded-4px overflow-hidden"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <div class="text-13px py-4px">
                {{ row.expression?.[0]?.type === 'ASIN_SAME_AS' ? '商品 (ASIN)' : '品牌' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="值 (ASIN/品牌)" prop="expression">
            <template #default="{ row }">
              <div class="text-13px py-4px font-medium">
                {{ row.resolvedExpression?.[0]?.value || row.expression?.[0]?.value }}
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

    <!-- 弹窗组件 -->
    <CampaignNegativeKeywordAddDialog
      ref="keywordAddDialogRef"
      :shop-id="shopId"
      :campaign-id="campaignId"
      :existing-keywords="negativeKeywords"
      @success="emit('refresh')"
    />
    <CampaignNegativeProductAddDialog
      ref="productAddDialogRef"
      :shop-id="shopId"
      :campaign-id="campaignId"
      :existing-items="negativeTargets"
      @success="emit('refresh')"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { AmzAdvCampaignManagerApi } from '@/app/erplus/api/adv/ads'
import CampaignNegativeKeywordAddDialog from './CampaignNegativeKeywordAddDialog.vue'
import CampaignNegativeProductAddDialog from './CampaignNegativeProductAddDialog.vue'

const props = defineProps<{
  shopId: number
  campaignId: number
  externalId?: string
  negativeKeywords: any[]
  negativeTargets: any[]
  disabled?: boolean
}>()

const emit = defineEmits(['refresh'])

const activeNegativeType = ref('keyword')
const selectedRows = ref<any[]>([])
const deleting = ref(false)

const keywordAddDialogRef = ref()
const productAddDialogRef = ref()

const visibleKeywords = computed(() => (props.negativeKeywords || []).filter(k => k.state !== 'ARCHIVED'))
const visibleProducts = computed(() => (props.negativeTargets || []).filter(p => p.state !== 'ARCHIVED'))

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

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认批量删除选中的 ${selectedRows.value.length} 项吗？`, '提示', { type: 'warning' })
    
    deleting.value = true
    const ids = selectedRows.value.map(r => r.keywordId || r.targetId).filter(id => !!id)
    
    if (ids.length > 0) {
      if (activeNegativeType.value === 'keyword') {
        await AmzAdvCampaignManagerApi.batchDeleteNegativeKeyword({
          shopId: props.shopId,
          campaignId: props.campaignId,
          keywords: ids
        })
      } else {
        await AmzAdvCampaignManagerApi.batchDeleteNegativeTargeting({
          shopId: props.shopId,
          campaignId: props.campaignId,
          ids: ids
        })
      }
      ElMessage.success('批量删除成功')
      emit('refresh')
    }
    selectedRows.value = []
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('批量删除失败')
  } finally {
    deleting.value = false
  }
}

const removeNegativeKeyword = async (row: any) => {
  try {
    if (row.keywordId) {
      await AmzAdvCampaignManagerApi.batchDeleteNegativeKeyword({
        shopId: props.shopId,
        keywords: [row.keywordId]
      })
      ElMessage.success('删除成功')
      emit('refresh')
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const removeNegativeProduct = async (row: any) => {
  try {
    if (row.targetId) {
      await AmzAdvCampaignManagerApi.batchDeleteNegativeTargeting({
        shopId: props.shopId,
        ids: [row.targetId]
      })
      ElMessage.success('删除成功')
      emit('refresh')
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.campaign-negative-manager :deep(.el-radio-button__inner) {
  padding: 5px 12px;
  font-size: 12px;
}
.campaign-negative-manager :deep(.el-table--small .el-table__cell) {
  padding: 4px 0;
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
.negative-content {
  background: #fff;
}
</style>
