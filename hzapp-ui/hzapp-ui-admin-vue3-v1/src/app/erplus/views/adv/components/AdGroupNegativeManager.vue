<template>
  <div class="negative-manager">
    <div class="flex justify-between items-center mb-10px">
      <el-radio-group v-model="negativeType" size="small">
        <el-radio-button label="keyword">否定关键词 ({{ localConfig.negativeKeywords?.length || 0 }})</el-radio-button>
        <el-radio-button label="product">否定商品 ({{ localConfig.negativeTargetings?.length || 0 }})</el-radio-button>
      </el-radio-group>
      <el-button type="primary" size="small" link :disabled="disabled" @click="addRow">
        + 添加{{ negativeType === 'keyword' ? '否定词' : '否定商品' }}
      </el-button>
    </div>

    <!-- 否定关键词表格 -->
    <el-table v-if="negativeType === 'keyword'" :data="localConfig.negativeKeywords || []" border size="small">
      <el-table-column label="否定关键词" prop="keywordText">
        <template #default="{ row }">
          <el-input v-model="row.keywordText" size="small" placeholder="输入否定词" :disabled="disabled" @change="emitUpdate" />
        </template>
      </el-table-column>
      <el-table-column label="匹配类型" prop="matchType" width="180">
        <template #default="{ row }">
          <el-select v-model="row.matchType" size="small" :disabled="disabled" @change="emitUpdate">
            <el-option label="NEGATIVE_EXACT" value="NEGATIVE_EXACT" />
            <el-option label="NEGATIVE_PHRASE" value="NEGATIVE_PHRASE" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="60" align="center">
        <template #default="{ $index }">
          <el-button type="danger" :icon="Delete" circle size="small" :disabled="disabled" @click="removeRow($index)" />
        </template>
      </el-table-column>
    </el-table>

    <!-- 否定商品表格 -->
    <el-table v-else :data="localConfig.negativeTargetings || []" border size="small">
      <el-table-column label="否定商品 (ASIN/SKU)" prop="asin">
        <template #default="{ row }">
          <el-input v-model="row.asin" size="small" placeholder="输入 ASIN" :disabled="disabled" @change="emitUpdate" />
        </template>
      </el-table-column>
      <el-table-column label="品牌ID (可选)" prop="brandId" width="150">
        <template #default="{ row }">
          <el-input v-model="row.brandId" size="small" placeholder="品牌 ID" :disabled="disabled" @change="emitUpdate" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="60" align="center">
        <template #default="{ $index }">
          <el-button type="danger" :icon="Delete" circle size="small" :disabled="disabled" @click="removeRow($index)" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Delete } from '@element-plus/icons-vue'

const props = defineProps<{
  config: any
  disabled?: boolean
}>()

const emit = defineEmits(['update'])

const negativeType = ref('keyword')
const localConfig = ref<any>({})

watch(() => props.config, (val) => {
  localConfig.value = JSON.parse(JSON.stringify(val || {}))
}, { immediate: true, deep: true })

const addRow = () => {
  if (negativeType.value === 'keyword') {
    if (!localConfig.value.negativeKeywords) localConfig.value.negativeKeywords = []
    localConfig.value.negativeKeywords.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' })
  } else {
    if (!localConfig.value.negativeTargetings) localConfig.value.negativeTargetings = []
    localConfig.value.negativeTargetings.push({ asin: '', brandId: '' })
  }
  emitUpdate()
}

const removeRow = (index: number) => {
  if (negativeType.value === 'keyword') {
    localConfig.value.negativeKeywords.splice(index, 1)
  } else {
    localConfig.value.negativeTargetings.splice(index, 1)
  }
  emitUpdate()
}

const emitUpdate = () => {
  emit('update', localConfig.value)
}
</script>
