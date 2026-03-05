<template>
  <div class="targeting-manager">
    <div class="flex justify-between items-center mb-10px">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">定向类型:</span>
        <el-radio-group 
          v-model="localConfig.targetingType" 
          size="small"
          :disabled="disabled || (!!localConfig.targetingType && (localConfig.keywordTargetings?.length > 0 || localConfig.productTargetings?.length > 0))"
          @change="handleTargetingTypeChange"
        >
          <el-radio-button label="KEYWORD">关键词定向</el-radio-button>
          <el-radio-button label="PRODUCT">商品/品类定向</el-radio-button>
        </el-radio-group>
        <el-tooltip content="定向类型设定后不可修改" placement="top">
          <el-icon class="ml-5px text-gray-400"><QuestionFilled /></el-icon>
        </el-tooltip>
      </div>
      <el-button 
        v-if="localConfig.targetingType"
        type="primary" 
        size="small" 
        link 
        :disabled="disabled"
        @click="addRow"
      >
        + 添加{{ localConfig.targetingType === 'KEYWORD' ? '关键词' : '商品/类目' }}
      </el-button>
    </div>

    <!-- 关键词定向 -->
    <template v-if="localConfig.targetingType === 'KEYWORD'">
      <el-table :data="localConfig.keywordTargetings || []" border size="small" style="width: 100%">
        <el-table-column label="关键词" prop="keywordText">
          <template #default="{ row }">
            <el-input v-model="row.keywordText" size="small" placeholder="输入关键词" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="匹配类型" prop="matchType" width="150">
          <template #default="{ row }">
            <el-select v-model="row.matchType" size="small" :disabled="disabled" @change="emitUpdate">
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
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button type="danger" :icon="Delete" circle size="small" :disabled="disabled" @click="removeRow($index)" />
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 商品定向 -->
    <template v-else-if="localConfig.targetingType === 'PRODUCT'">
      <el-table :data="localConfig.productTargetings || []" border size="small" style="width: 100%">
        <el-table-column label="操作/类型" prop="expressionType" width="180">
          <template #default="{ row }">
            <el-select v-model="row.expressionType" size="small" placeholder="类型" :disabled="disabled" @change="emitUpdate">
              <el-option label="ASIN (asinSameAs)" value="asinSameAs" />
              <el-option label="类目 (category)" value="category" />
              <el-option label="品牌 (brand)" value="brand" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="值 (ASIN / ID)" prop="expressionValue">
          <template #default="{ row }">
            <el-input v-model="row.expressionValue" size="small" placeholder="输入 ASIN 或 ID" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="出价" prop="bid" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.bid" :precision="2" :step="0.01" size="small" style="width: 100%" :disabled="disabled" @change="emitUpdate" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button type="danger" :icon="Delete" circle size="small" :disabled="disabled" @click="removeRow($index)" />
          </template>
        </el-table-column>
      </el-table>
    </template>

    <el-empty v-else description="请先选择定向类型" :image-size="40" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { QuestionFilled, Delete } from '@element-plus/icons-vue'

const props = defineProps<{
  config: any
  defaultBid: number
  disabled?: boolean
}>()

const emit = defineEmits(['update'])

const localConfig = ref<any>({})

watch(() => props.config, (val) => {
  localConfig.value = JSON.parse(JSON.stringify(val || {}))
}, { immediate: true, deep: true })

const handleTargetingTypeChange = (val: any) => {
  if (val === 'KEYWORD') {
    localConfig.value.productTargetings = []
  } else if (val === 'PRODUCT') {
    localConfig.value.keywordTargetings = []
  }
  emitUpdate()
}

const addRow = () => {
  if (localConfig.value.targetingType === 'KEYWORD') {
    if (!localConfig.value.keywordTargetings) localConfig.value.keywordTargetings = []
    localConfig.value.keywordTargetings.push({ keywordText: '', matchType: 'EXACT', bid: props.defaultBid || 1.0 })
  } else if (localConfig.value.targetingType === 'PRODUCT') {
    if (!localConfig.value.productTargetings) localConfig.value.productTargetings = []
    localConfig.value.productTargetings.push({ expressionType: 'asinSameAs', expressionValue: '', bid: props.defaultBid || 1.0 })
  }
  emitUpdate()
}

const removeRow = (index: number) => {
  if (localConfig.value.targetingType === 'KEYWORD') {
    localConfig.value.keywordTargetings.splice(index, 1)
  } else if (localConfig.value.targetingType === 'PRODUCT') {
    localConfig.value.productTargetings.splice(index, 1)
  }
  emitUpdate()
}

const emitUpdate = () => {
  emit('update', localConfig.value)
}
</script>
