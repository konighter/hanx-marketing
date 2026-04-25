<template>
  <div class="targeting-manager">
    <div class="flex justify-between items-center mb-10px">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">定向类型:</span>
        <el-radio-group 
          v-model="localConfig.amz_targeting_type" 
          size="small"
          :disabled="disabled"
          @change="handleTargetingTypeChange"
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
        <el-tooltip content="定向类型设定后不可修改" placement="top">
          <el-icon class="ml-5px text-gray-400"><QuestionFilled /></el-icon>
        </el-tooltip>
      </div>
      <el-button 
        v-if="localConfig.amz_targeting_type"
        size="small" 
        :disabled="disabled"
        @click="addRow"
        class="add-btn"
      >
        <el-icon><Plus /></el-icon>
        <span class="ml-4px">添加{{ localConfig.amz_targeting_type === 'KEYWORD' ? '关键词' : '商品/类目' }}</span>
      </el-button>
    </div>

    <!-- 关键词定向 -->
    <template v-if="localConfig.amz_targeting_type === 'KEYWORD'">
      <el-table :data="localConfig.amz_keyword || []" border size="small" style="width: 100%">
        <el-table-column label="关键词" prop="keywordText">
          <template #default="{ row }">
            <div v-if="row.keywordId" class="text-13px py-4px">{{ row.keywordText }}</div>
            <el-input v-else v-model="row.keywordText" size="small" placeholder="输入关键词" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="匹配类型" prop="matchType" width="150">
          <template #default="{ row }">
            <div v-if="row.keywordId" class="text-13px py-4px">{{ row.matchType }}</div>
            <el-select v-else v-model="row.matchType" size="small" :disabled="disabled" @change="emitUpdate">
              <el-option label="EXACT" value="EXACT" />
              <el-option label="PHRASE" value="PHRASE" />
              <el-option label="BROAD" value="BROAD" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.bid" :precision="2" :step="0.01" size="small" style="width: 100%" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" :disabled="disabled" @click="removeRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 商品定向 -->
    <template v-else-if="localConfig.amz_targeting_type === 'PRODUCT'">
      <el-table :data="localConfig.amz_target_clause || []" border size="small" style="width: 100%">
        <el-table-column label="操作/类型" prop="expressionType" width="180">
          <template #default="{ row }">
            <div v-if="row.targetId" class="text-13px py-4px">{{ row.expressionType }}</div>
            <el-select v-else v-model="row.expressionType" size="small" placeholder="类型" :disabled="disabled" @change="emitUpdate">
              <el-option label="ASIN (asinSameAs)" value="asinSameAs" />
              <el-option label="类目 (category)" value="category" />
              <el-option label="品牌 (brand)" value="brand" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="值 (ASIN / ID)" prop="expressionValue">
          <template #default="{ row }">
            <div v-if="row.targetId" class="text-13px py-4px font-medium">{{ row.expressionValue }}</div>
            <el-input v-else v-model="row.expressionValue" size="small" placeholder="输入 ASIN 或 ID" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.bid" :precision="2" :step="0.01" size="small" style="width: 100%" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" :disabled="disabled" @click="removeRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <el-empty v-else description="请先选择定向类型" :image-size="40" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { QuestionFilled, Plus } from '@element-plus/icons-vue'

const props = defineProps<{
  config: any
  defaultBid: number
  disabled?: boolean
}>()

const emit = defineEmits(['update'])

const localConfig = ref<any>({})

watch(() => props.config, (val) => {
  // 只有当本地配置为空或者明确没有定向类型时才初始化，避免编辑中途被覆盖
  if (!localConfig.value.amz_targeting_type || (localConfig.value.amz_keyword?.length === 0 && localConfig.value.amz_target_clause?.length === 0)) {
    const config = JSON.parse(JSON.stringify(val || {}))
    // 自动识别定向类型
    if (!config.amz_targeting_type) {
      if (config.amz_keyword?.length > 0) config.amz_targeting_type = 'KEYWORD'
      else if (config.amz_target_clause?.length > 0) config.amz_targeting_type = 'PRODUCT'
    }
    localConfig.value = config
  }
}, { immediate: true, deep: true })

const handleTargetingTypeChange = (val: any) => {
  if (val === 'KEYWORD') {
    localConfig.value.amz_target_clause = []
  } else if (val === 'PRODUCT') {
    localConfig.value.amz_keyword = []
  }
  emitUpdate()
}

const addRow = () => {
  if (localConfig.value.amz_targeting_type === 'KEYWORD') {
    if (!localConfig.value.amz_keyword) localConfig.value.amz_keyword = []
    localConfig.value.amz_keyword.push({ keywordText: '', matchType: 'EXACT', bid: props.defaultBid || 1.0 })
  } else if (localConfig.value.amz_targeting_type === 'PRODUCT') {
    if (!localConfig.value.amz_target_clause) localConfig.value.amz_target_clause = []
    localConfig.value.amz_target_clause.push({ expressionType: 'asinSameAs', expressionValue: '', bid: props.defaultBid || 1.0 })
  }
  emitUpdate()
}

const removeRow = (index: number) => {
  if (localConfig.value.amz_targeting_type === 'KEYWORD') {
    localConfig.value.amz_keyword.splice(index, 1)
  } else if (localConfig.value.amz_targeting_type === 'PRODUCT') {
    localConfig.value.amz_target_clause.splice(index, 1)
  }
  emitUpdate()
}

const emitUpdate = () => {
  emit('update', localConfig.value)
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
