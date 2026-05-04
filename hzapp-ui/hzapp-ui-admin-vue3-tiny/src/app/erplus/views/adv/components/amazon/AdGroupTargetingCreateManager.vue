<template>
  <div class="targeting-manager">
    <div class="flex justify-between items-center mb-10px w-full">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">定向类型:</span>
        <el-radio-group 
          v-if="targetingType?.toUpperCase().includes('MANUAL')"
          v-model="manualTargetingMode" 
          size="small"
        >
          <el-radio-button 
            v-if="!(config.amz_target_clause?.length > 0)"
            label="KEYWORD" 
          >关键词定向</el-radio-button>
          <el-radio-button 
            v-if="!(config.amz_keyword?.length > 0)"
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
            @click="handleBatchDelete"
          >
            批量删除 ({{ selectedRows.length }})
          </el-button>
          <el-button size="small" type="primary" @click="handleBatchUpdateBid">
            批量调价
          </el-button>
        </template>
        <el-button 
          v-if="targetingType?.toUpperCase().includes('AUTO') ? false : true"
          size="small" 
          @click="openAddDialog"
          class="add-btn"
        >
          <el-icon><Plus /></el-icon>
          <span class="ml-4px">添加{{ manualTargetingMode === 'KEYWORD' ? '关键词' : '商品' }}</span>
        </el-button>
      </div>
    </div>

    <!-- 关键词定向 -->
    <template v-if="targetingType?.toUpperCase().includes('AUTO') ? false : manualTargetingMode === 'KEYWORD'">
      <el-table :data="config.amz_keyword || []" border size="small" style="width: 100%" @selection-change="handleSelectionChange">
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
            <el-input-number 
              v-model="row.bid" 
              :precision="2" 
              :step="0.01" 
              :controls="false"
              size="small" 
              style="width: 70px"
              @change="syncConfig"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 商品定向 -->
    <template v-else-if="targetingType?.toUpperCase().includes('AUTO') ? false : manualTargetingMode === 'PRODUCT'">
      <el-table :data="config.amz_target_clause || []" border size="small" style="width: 100%" @selection-change="handleSelectionChange">
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
            <el-input-number 
              v-model="row.bid" 
              :precision="2" 
              :step="0.01" 
              :controls="false"
              size="small" 
              style="width: 70px"
              @change="syncConfig"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 自动定向 (4种匹配方式) -->
    <template v-else-if="targetingType?.toUpperCase().includes('AUTO')">
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
        <el-table-column label="出价" prop="bid" width="140">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.bid" 
              :precision="2" 
              :step="0.01" 
              :controls="false"
              size="small" 
              style="width: 70px"
              @change="syncConfig"
            />
          </template>
        </el-table-column>
      </el-table>
    </template>

    <el-empty v-else description="请先选择定向类型" :image-size="40" />

    <!-- 添加关键词对话框 -->
    <KeywordAddCreateDialog 
      ref="keywordAddDialogRef" 
      :default-bid="defaultBid"
      :existing-keywords="config.amz_keyword || []"
      :ad-asins="adAsins"
      @success="handleKeywordsAdded"
    />

    <!-- 添加商品/品类定向对话框 -->
    <TargetingProductAddCreateDialog
      ref="targetingProductAddDialogRef"
      :default-bid="defaultBid"
      :existing-items="config.amz_target_clause || []"
      :ad-asins="adAsins"
      @success="handleTargetingAdded"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, watch } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { QuestionFilled, Plus } from '@element-plus/icons-vue'
import KeywordAddCreateDialog from './KeywordAddCreateDialog.vue'
import TargetingProductAddCreateDialog from './TargetingProductAddCreateDialog.vue'

const props = defineProps<{
  defaultBid: number
  adAsins?: string[]
  targetingType?: string
}>()

const shopId = inject<number>('shopId')
const config = defineModel<any>('config', { required: true })

const manualTargetingMode = ref('KEYWORD')

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

// 初始化自动定向行
const autoTargetingRows = computed(() => {
  if (!props.targetingType?.toUpperCase().includes('AUTO')) return []
  
  // 如果 amz_target_clause 为空，则根据 4 种类型初始化
  if (!config.value.amz_target_clause || config.value.amz_target_clause.length === 0) {
    const rows = Object.keys(AUTO_TARGETING_MAP).map(type => ({
      expression: [{ type, value: null }],
      expressionType: 'AUTO',
      _expressionType: type,
      bid: props.defaultBid,
      state: 'ENABLED'
    }))
    // 注意：在 computed 中直接修改并不同步，我们在 watch 中处理初始化
    return rows
  }
  
  return config.value.amz_target_clause.map((c: any) => ({
    ...c,
    _expressionType: c.expression?.[0]?.type || c.expressionValue
  }))
})

const syncConfig = () => {
  config.value = { ...config.value }
}

// 监听并同步手动定向模式到 amz_targeting_type
watch(manualTargetingMode, (mode) => {
  if (props.targetingType?.toUpperCase().includes('MANUAL') && config.value) {
    config.value.amz_targeting_type = mode
    syncConfig()
  }
}, { immediate: true })

// 监听并初始化自动定向数据
watch(() => props.targetingType, (type) => {
  if (type?.toUpperCase().includes('AUTO') && (!config.value.amz_target_clause || config.value.amz_target_clause.length === 0)) {
    config.value.amz_target_clause = Object.keys(AUTO_TARGETING_MAP).map(type => ({
      expression: [{ type, value: null }],
      expressionType: 'AUTO',
      _expressionType: type,
      bid: props.defaultBid,
      state: 'ENABLED'
    }))
    syncConfig()
  }
}, { immediate: true })

const selectedRows = ref<any[]>([])

// 弹框引用
const keywordAddDialogRef = ref()
const targetingProductAddDialogRef = ref()

const openAddDialog = () => {
  if (!props.adAsins || props.adAsins.length === 0) {
    ElMessage.warning('请先添加至少一个商品广告，再添加关键词')
    return
  }
  if (manualTargetingMode.value === 'KEYWORD') {
    keywordAddDialogRef.value?.open()
  } else {
    targetingProductAddDialogRef.value?.open()
  }
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const handleBatchDelete = () => {
  if (manualTargetingMode.value === 'KEYWORD') {
    config.value = {
      ...config.value,
      amz_keyword: (config.value.amz_keyword || []).filter((k: any) => !selectedRows.value.includes(k))
    }
  } else {
    config.value = {
      ...config.value,
      amz_target_clause: (config.value.amz_target_clause || []).filter((c: any) => !selectedRows.value.includes(c))
    }
  }
  selectedRows.value = []
}

const handleBatchUpdateBid = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新的出价', '批量调价', {
      inputPattern: /^\d+(\.\d{1,2})?$/,
      inputErrorMessage: '请输入有效的价格',
      inputValue: props.defaultBid?.toString()
    })
    
    const newBid = parseFloat(value)
    selectedRows.value.forEach(r => {
      r.bid = newBid
    })
    syncConfig()
    selectedRows.value = []
  } catch (e) {}
}

const handleKeywordsAdded = (items: any[]) => {
  config.value = {
    ...config.value,
    amz_keyword: [...(config.value.amz_keyword || []), ...items]
  }
}

const handleTargetingAdded = (items: any[]) => {
  config.value = {
    ...config.value,
    amz_target_clause: [...(config.value.amz_target_clause || []), ...items]
  }
}

const removeRow = (row: any) => {
  if (manualTargetingMode.value === 'KEYWORD') {
    config.value = {
      ...config.value,
      amz_keyword: (config.value.amz_keyword || []).filter((k: any) => k !== row)
    }
  } else {
    config.value = {
      ...config.value,
      amz_target_clause: (config.value.amz_target_clause || []).filter((c: any) => c !== row)
    }
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
