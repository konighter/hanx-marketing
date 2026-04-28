<template>
  <div class="ad-group-negative-manager">
    <div class="flex justify-between items-center mb-10px">
      <el-radio-group v-model="activeNegativeType" size="small" class="compact-radio">
        <el-radio-button label="keyword" :disabled="config.amz_negative_target_clause?.length > 0">
          否定关键词 ({{ config.amz_negative_keyword?.length || 0 }})
        </el-radio-button>
        <el-radio-button label="product" :disabled="config.amz_negative_keyword?.length > 0">
          否定商品 ({{ config.amz_negative_target_clause?.length || 0 }})
        </el-radio-button>
      </el-radio-group>

      <div class="flex gap-8px">
        <el-button 
          v-if="selectedRows.length > 0"
          size="small" 
          type="danger" 
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedRows.length }})
        </el-button>
        <el-button 
          size="small" 
          @click="openAddDialog" 
          class="add-btn"
        >
          <el-icon><Plus /></el-icon>
          <span class="ml-4px">选择{{ activeNegativeType === 'keyword' ? '否定关键词' : '否定商品' }}</span>
        </el-button>
      </div>
    </div>

    <div class="p-10px border-1 border-gray-200 border-solid rounded-4px bg-gray-50 min-h-80px">
      <!-- 否定关键词表格 -->
      <div v-if="activeNegativeType === 'keyword'">
        <el-table 
          :data="config.amz_negative_keyword || []" 
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
              <el-button type="danger" link size="small" @click="removeNegativeKeyword(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 否定商品表格 -->
      <div v-else-if="activeNegativeType === 'product'">
        <el-table 
          :data="config.amz_negative_target_clause || []" 
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
                {{ row.expression[0].value }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ row }">
              <el-button type="danger" link size="small" @click="removeNegativeProduct(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 批量添加对话框 -->
    <NegativeKeywordAddCreateDialog
      ref="keywordAddDialogRef"
      :existing-keywords="config.amz_negative_keyword || []"
      @success="handleKeywordsAdded"
    />
    <NegativeProductAddCreateDialog
      ref="productAddDialogRef"
      :existing-items="config.amz_negative_target_clause || []"
      @success="handleProductsAdded"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import NegativeKeywordAddCreateDialog from './NegativeKeywordAddCreateDialog.vue'
import NegativeProductAddCreateDialog from './NegativeProductAddCreateDialog.vue'

const config = defineModel<any>('config', { required: true })

const activeNegativeType = ref('keyword')
const selectedRows = ref<any[]>([])

const keywordAddDialogRef = ref()
const productAddDialogRef = ref()

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

const handleKeywordsAdded = (items: any[]) => {
  config.value = {
    ...config.value,
    amz_negative_keyword: [...(config.value.amz_negative_keyword || []), ...items]
  }
}

const handleProductsAdded = (items: any[]) => {
  config.value = {
    ...config.value,
    amz_negative_target_clause: [...(config.value.amz_negative_target_clause || []), ...items]
  }
}

const handleBatchDelete = () => {
  if (activeNegativeType.value === 'keyword') {
    config.value = {
      ...config.value,
      amz_negative_keyword: (config.value.amz_negative_keyword || []).filter(
        (k: any) => !selectedRows.value.includes(k)
      )
    }
  } else {
    config.value = {
      ...config.value,
      amz_negative_target_clause: (config.value.amz_negative_target_clause || []).filter(
        (p: any) => !selectedRows.value.includes(p)
      )
    }
  }
  selectedRows.value = []
}

const removeNegativeKeyword = (row: any) => {
  config.value = {
    ...config.value,
    amz_negative_keyword: (config.value.amz_negative_keyword || []).filter((k: any) => k !== row)
  }
}

const removeNegativeProduct = (row: any) => {
  config.value = {
    ...config.value,
    amz_negative_target_clause: (config.value.amz_negative_target_clause || []).filter((p: any) => p !== row)
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
