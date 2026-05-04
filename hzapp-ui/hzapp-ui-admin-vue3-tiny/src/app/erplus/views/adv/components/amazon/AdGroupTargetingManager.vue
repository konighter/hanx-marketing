<template>
  <div class="targeting-manager">
    <div class="flex justify-between items-center mb-10px w-full">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">定向类型:</span>
        <el-radio-group 
          v-if="targetingType?.toUpperCase()?.includes('MANUAL')"
          v-model="manualTargetingMode" 
          size="small"
          :disabled="disabled"
          @change="handleManualModeChange"
        >
          <el-radio-button 
            v-if="!(localConfig.amz_target_clause?.length > 0)"
            label="KEYWORD" 
          >关键词定向</el-radio-button>
          <el-radio-button 
            v-if="!(localConfig.amz_keyword?.length > 0)"
            label="PRODUCT" 
          >商品/品类定向</el-radio-button>
        </el-radio-group>
        <el-tag v-else-if="targetingType?.toUpperCase().includes('AUTO')" type="success" size="small">自动定向</el-tag>
        <el-tooltip content="定向类型设定后不可修改" placement="top">
          <el-icon class="ml-5px text-gray-400"><QuestionFilled /></el-icon>
        </el-tooltip>
      </div>
      <div class="flex gap-10px">
        <template v-if="selectedRows.length > 0">
          <el-button 
            size="small" 
            type="danger" 
            :loading="deleting"
            @click="handleBatchDelete"
          >
            批量删除 ({{ selectedRows.length }})
          </el-button>
          <el-button size="small" type="primary" @click="handleBatchUpdateBid">
            批量调价
          </el-button>
        </template>
        <el-button 
          v-if="targetingType?.toUpperCase()?.includes('MANUAL')"
          size="small" 
          :disabled="disabled"
          @click="openAddDialog"
          class="add-btn"
        >
          <el-icon><Plus /></el-icon>
          <span class="ml-4px">添加{{ manualTargetingMode === 'KEYWORD' ? '关键词' : '商品' }}</span>
        </el-button>
      </div>
    </div>

    <!-- 关键词定向 -->
    <template v-if="targetingType?.toUpperCase()?.includes('AUTO') ? false : manualTargetingMode === 'KEYWORD'">
      <el-table :data="visibleKeywords" border size="small" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column label="关键词" prop="keywordText">
          <template #default="{ row }">
            <div class="text-13px py-4px">{{ row.keywordText }}</div>
          </template>
        </el-table-column>
        <el-table-column label="匹配类型" prop="matchType" width="150">
          <template #default="{ row }">
            <div class="text-13px py-4px">{{ row.matchType }}</div>
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="140">
          <template #default="{ row }">
            <div v-if="!isEditing(row)" class="flex items-center group cursor-pointer" @click="startEdit(row)">
              <span class="text-13px font-medium mr-5px">{{ row.bid?.toFixed(2) }}</span>
              <el-icon class="text-gray-400 group-hover:text-primary transition-colors"><Edit /></el-icon>
            </div>
            <div v-else class="flex items-center gap-4px">
              <el-input-number 
                v-model="row.bid" 
                :precision="2" 
                :step="0.01" 
                :controls="false"
                size="small" 
                style="width: 70px" 
                :disabled="disabled" 
              />
              <el-button type="primary" link size="small" @click="saveRowBid(row)">
                <el-icon><Check /></el-icon>
              </el-button>
              <el-button link size="small" @click="cancelEdit(row)">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ row }">
            <el-button type="danger" link size="small" :disabled="disabled" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 商品定向 -->
    <template v-else-if="targetingType?.toUpperCase()?.includes('AUTO') ? false : manualTargetingMode === 'PRODUCT'">
      <el-table :data="visibleTargetingClauses" border size="small" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column label="操作/类型" prop="expressionType" width="180">
          <template #default="{ row }">
            <div class="text-13px py-4px">{{ row.expressionType }}</div>
          </template>
        </el-table-column>
        <el-table-column label="值 (ASIN / ID)" prop="expressionValue">
          <template #default="{ row }">
            <div class="text-13px py-4px font-medium">{{ row.expressionValue }}</div>
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="140">
          <template #default="{ row }">
            <div v-if="!isEditing(row)" class="flex items-center group cursor-pointer" @click="startEdit(row)">
              <span class="text-13px font-medium mr-5px">{{ row.bid?.toFixed(2) }}</span>
              <el-icon class="text-gray-400 group-hover:text-primary transition-colors"><Edit /></el-icon>
            </div>
            <div v-else class="flex items-center gap-4px">
              <el-input-number 
                v-model="row.bid" 
                :precision="2" 
                :step="0.01" 
                :controls="false"
                size="small" 
                style="width: 70px" 
                :disabled="disabled" 
              />
              <el-button type="primary" link size="small" @click="saveRowBid(row)">
                <el-icon><Check /></el-icon>
              </el-button>
              <el-button link size="small" @click="cancelEdit(row)">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ row }">
            <el-button type="danger" link size="small" :disabled="disabled" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 自动定向 (4种匹配方式) -->
    <template v-else-if="targetingType?.toUpperCase()?.includes('AUTO')">
      <el-table :data="autoTargetingRows" border size="small" style="width: 100%">
        <el-table-column label="匹配类型" prop="_expressionType" width="180">
          <template #default="{ row }">
            <div class="text-13px font-medium py-4px">
              {{ AUTO_TARGETING_MAP[row._expressionType] || row._expressionType }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="说明" prop="description" min-width="200">
          <template #default="{ row }">
            <div class="text-12px text-gray-400">
              {{ AUTO_TARGETING_DESC[row._expressionType] }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.state" 
              active-value="ENABLED" 
              inactive-value="PAUSED"
              size="small"
              :disabled="disabled || !row.targetId"
              @change="val => handleAutoTargetingStateChange(row, val as string)"
            />
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="140">
          <template #default="{ row }">
            <div v-if="!isEditing(row)" class="flex items-center group cursor-pointer" @click="startEdit(row)">
              <span class="text-13px font-medium mr-5px">{{ row.bid?.toFixed(2) }}</span>
              <el-icon class="text-gray-400 group-hover:text-primary transition-colors"><Edit /></el-icon>
            </div>
            <div v-else class="flex items-center gap-4px">
              <el-input-number 
                v-model="row.bid" 
                :precision="2" 
                :step="0.01" 
                :controls="false"
                size="small" 
                style="width: 70px" 
                :disabled="disabled" 
              />
              <el-button type="primary" link size="small" @click="saveAutoRowBid(row)">
                <el-icon><Check /></el-icon>
              </el-button>
              <el-button link size="small" @click="cancelEdit(row)">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <el-empty v-else description="请先选择定向类型" :image-size="40" />

    <!-- 添加关键词对话框 -->
    <KeywordAddDialog 
      ref="keywordAddDialogRef" 
      :shop-id="shopId" 
      :ad-group-id="adGroupId" 
      :external-id="externalId"
      :default-bid="defaultBid"
      :existing-keywords="localConfig.amz_keyword || []"
      @success="handleSuccess"
    />

    <!-- 添加商品/品类定向对话框 -->
    <TargetingProductAddDialog
      ref="targetingProductAddDialogRef"
      :shop-id="shopId"
      :ad-group-id="adGroupId"
      :default-bid="defaultBid"
      :existing-items="localConfig.amz_target_clause || []"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Plus, Edit, Check, Close } from '@element-plus/icons-vue'
import { AmzAdvAdGroupManagerApi, AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import KeywordAddDialog from './KeywordAddDialog.vue'
import TargetingProductAddDialog from './TargetingProductAddDialog.vue'

const props = defineProps<{
  defaultBid: number
  disabled?: boolean
  adGroupId: number
  externalId?: string
  shopId: number
  targetingType?: string
  config: any
}>()

const emit = defineEmits(['update', 'refresh'])

const localConfig = ref<any>({})

// 只展示非归档状态的关键词/定向
const visibleKeywords = computed(() => {
  return (localConfig.value.amz_keyword || []).filter((k: any) => k.state !== 'ARCHIVED')
})

const visibleTargetingClauses = computed(() => {
  return (localConfig.value.amz_target_clause || []).filter((c: any) => c.state !== 'ARCHIVED')
})

const selectedRows = ref<any[]>([])
const editingRows = ref(new Set<string>())

const manualTargetingMode = ref('KEYWORD')

const emitUpdate = () => {
  emit('update', localConfig.value)
}

watch(() => [props.config, props.adGroupId], (newVals, oldVals) => {
  const [newConfig, newId] = newVals
  const [_, oldId] = oldVals || []
  
  // 如果广告组 ID 变了，必须强制重置本地配置
  const forceReset = oldId !== undefined && newId !== oldId
  
  if (forceReset || !localConfig.value.amz_keyword) {
    const config = JSON.parse(JSON.stringify(newConfig || {}))
    localConfig.value = config
    
    // 初始化手动定向模式
    if (config.amz_target_clause?.length > 0) {
      manualTargetingMode.value = 'PRODUCT'
    } else {
      manualTargetingMode.value = 'KEYWORD'
    }
  }
}, { immediate: true, deep: true })

const AUTO_TARGETING_MAP: Record<string, string> = {
  'QUERY_HIGH_REL_MATCHES': '紧密匹配',
  'QUERY_BROAD_REL_MATCHES': '宽泛匹配',
  'ASIN_SUBSTITUTE_RELATED': '替代商品',
  'ASIN_ACCESSORY_RELATED': '关联商品'
}

const AUTO_TARGETING_DESC: Record<string, string> = {
  'QUERY_HIGH_REL_MATCHES': '我们会向与您的商品紧密相关的搜索词展示您的广告。',
  'QUERY_BROAD_REL_MATCHES': '我们会向与您的商品宽泛相关的搜索词展示您的广告。',
  'ASIN_SUBSTITUTE_RELATED': '我们会向查看与您的商品类似的商品的详情页面的购物者展示您的广告。',
  'ASIN_ACCESSORY_RELATED': '我们会向查看与您的商品互补的商品的详情页面的购物者展示您的广告。'
}

/** 从 targeting clause 对象中提取 expression type (如 QUERY_HIGH_REL_MATCHES) */
const getExpressionType = (clause: any): string | undefined => {
  if (clause.expression && Array.isArray(clause.expression) && clause.expression.length > 0) {
    return clause.expression[0].type
  }
  return clause.expressionValue // 兼容旧数据
}

const autoTargetingRows = computed(() => {
  const existing = localConfig.value.amz_target_clause || []
  return Object.keys(AUTO_TARGETING_MAP).map(type => {
    const item = existing.find((c: any) => getExpressionType(c) === type)
    return item ? { ...item, _expressionType: type } : { 
      expression: [{ type, value: null }],
      expressionType: 'AUTO', 
      _expressionType: type, 
      bid: props.defaultBid, 
      state: 'ENABLED',
      isNew: true 
    }
  })
})

const handleManualModeChange = (val: any) => {
  if (val === 'KEYWORD') {
    localConfig.value.amz_target_clause = []
  } else if (val === 'PRODUCT') {
    localConfig.value.amz_keyword = []
  }
  manualTargetingMode.value = val
  emitUpdate()
}

// 弹框引用
const keywordAddDialogRef = ref()
const targetingProductAddDialogRef = ref()

const openAddDialog = () => {
  if (manualTargetingMode.value === 'KEYWORD') {
    keywordAddDialogRef.value?.open()
  } else {
    targetingProductAddDialogRef.value?.open()
  }
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const isEditing = (row: any) => {
  const id = row.keywordId || row.targetId || row.tempId
  return editingRows.value.has(id)
}

const startEdit = (row: any) => {
  if (props.disabled) return
  if (!row.tempId && !row.keywordId && !row.targetId) {
    row.tempId = Math.random().toString(36).substring(7)
  }
  const id = row.keywordId || row.targetId || row.tempId
  editingRows.value.add(id)
}

const cancelEdit = (row: any) => {
  const id = row.keywordId || row.targetId || row.tempId
  editingRows.value.delete(id)
}

const saveRowBid = async (row: any) => {
  if (manualTargetingMode.value === 'KEYWORD') {
    if (row.keywordId) {
      await handleKeywordBidChange(row)
    }
  } else {
    // 商品定向的出价保存逻辑 (如果已经持久化)
    if (row.targetId) {
      try {
        await AmzAdvAdGroupManagerApi.batchUpdateTargetingBid({
          shopId: props.shopId,
          groupId: props.adGroupId,
          items: [{ targetId: row.targetId, bid: row.bid }]
        })
        ElMessage.success('出价已更新')
      } catch (e) {
        ElMessage.error('出价更新失败')
      }
    }
  }
  cancelEdit(row)
  emitUpdate()
}

const saveAutoRowBid = async (row: any) => {
  try {
    if (row.targetId) {
      await AmzAdvAdGroupManagerApi.batchUpdateTargetingBid({
        shopId: props.shopId,
        groupId: props.adGroupId,
        items: [{ targetId: row.targetId, bid: row.bid }]
      })
    } else {
      // 如果没有 targetId，需要先创建
      await AmzAdvAdGroupManagerApi.batchCreateTargeting({
        shopId: props.shopId,
        groupId: props.adGroupId,
        items: [{
          expression: row.expression || [{ type: row._expressionType, value: null }],
          expressionType: 'AUTO',
          bid: row.bid,
          state: row.state
        }]
      })
      emit('refresh')
    }
    ElMessage.success('出价已更新')
  } catch (e) {
    ElMessage.error('操作失败')
  }
  cancelEdit(row)
  emitUpdate()
}

const handleAutoTargetingStateChange = async (row: any, val: string) => {
  if (!row.targetId) return
  try {
    // 这里复用调价接口来更新状态 (如果是 V3 接口通常支持同时更新)
    // 或者如果有专门的状态更新接口则使用之。目前参考已有逻辑。
    await AmzAdvAdGroupManagerApi.batchUpdateTargetingBid({
      shopId: props.shopId,
      groupId: props.adGroupId,
      items: [{ targetId: row.targetId, state: val }]
    })
    ElMessage.success('状态已更新')
    emitUpdate()
  } catch (e) {
    row.state = val === 'ENABLED' ? 'PAUSED' : 'ENABLED'
    ElMessage.error('更新状态失败')
  }
}

const deleting = ref(false)

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认批量删除选中的 ${selectedRows.value.length} 项吗？`, '提示', { type: 'warning' })
    
    deleting.value = true
    const persistIds = selectedRows.value
      .map(r => r.keywordId || r.targetId)
      .filter(id => !!id)
      
    if (persistIds.length > 0) {
      if (manualTargetingMode.value === 'KEYWORD') {
        await AmzAdvAdGroupManagerApi.batchDeleteKeyword({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: persistIds
        })
      } else {
        await AmzAdvAdGroupManagerApi.batchDeleteTargeting({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: persistIds
        })
      }
    }
    
    // 更新本地列表
    if (manualTargetingMode.value === 'KEYWORD') {
      localConfig.value.amz_keyword = localConfig.value.amz_keyword.filter(
        (k: any) => !selectedRows.value.includes(k)
      )
    } else {
      localConfig.value.amz_target_clause = localConfig.value.amz_target_clause.filter(
        (c: any) => !selectedRows.value.includes(c)
      )
    }
    
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    emitUpdate()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('批量删除失败')
  } finally {
    deleting.value = false
  }
}

const handleBatchUpdateBid = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新的出价', '批量调价', {
      inputPattern: /^\d+(\.\d{1,2})?$/,
      inputErrorMessage: '请输入有效的价格',
      inputValue: props.defaultBid?.toString()
    })
    
    const newBid = parseFloat(value)
    const persistItems = selectedRows.value
      .filter(r => r.keywordId || r.targetId)
      
    if (persistItems.length > 0) {
      if (manualTargetingMode.value === 'KEYWORD') {
        await AmzAdvAdGroupManagerApi.batchUpdateKeywordBid({
          shopId: props.shopId,
          groupId: props.adGroupId,
          items: persistItems.map(r => ({ keywordId: r.keywordId, bid: newBid }))
        })
      } else {
        await AmzAdvAdGroupManagerApi.batchUpdateTargetingBid({
          shopId: props.shopId,
          groupId: props.adGroupId,
          items: persistItems.map(r => ({ targetId: r.targetId, bid: newBid }))
        })
      }
    }
    
    // 更新本地列表
    selectedRows.value.forEach(r => {
      r.bid = newBid
    })
    
    ElMessage.success('批量调价成功')
    selectedRows.value = []
    emitUpdate()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('批量调价失败')
  }
}

const handleSuccess = async () => {
  const res = await AdsAdGroupApi.getAttributes(props.adGroupId)
  if (res) {
    localConfig.value = { ...localConfig.value, ...res }
    emit('update', localConfig.value)
  }
  emit('refresh')
}

const handleKeywordBidChange = async (row: any) => {
  if (!row.keywordId) return
  try {
    await AmzAdvAdGroupManagerApi.batchUpdateKeywordBid({
      shopId: props.shopId,
      groupId: props.adGroupId,
      items: [{ keywordId: row.keywordId, bid: row.bid }]
    })
    ElMessage.success('出价已更新')
  } catch (e) {
    ElMessage.error('出价更新失败')
  }
}

const removeRow = async (row: any) => {
  try {
    if (localConfig.value.amz_targeting_type === 'KEYWORD') {
      if (row.keywordId) {
        await AmzAdvAdGroupManagerApi.batchDeleteKeyword({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: [row.keywordId]
        })
      }
      const index = localConfig.value.amz_keyword.indexOf(row)
      if (index > -1) localConfig.value.amz_keyword.splice(index, 1)
    } else if (localConfig.value.amz_targeting_type === 'PRODUCT') {
      if (row.targetId) {
        await AmzAdvAdGroupManagerApi.batchDeleteTargeting({
          shopId: props.shopId,
          groupId: props.adGroupId,
          ids: [row.targetId]
        })
      }
      const index = localConfig.value.amz_target_clause.indexOf(row)
      if (index > -1) localConfig.value.amz_target_clause.splice(index, 1)
    }
    emitUpdate()
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}


</script>

<style scoped>
.add-btn {
  border: 1px solid var(--el-border-color);
  background: transparent;
  color: var(--el-text-color-regular);
  height: 24px;
  padding: 0 8px;
}
.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
