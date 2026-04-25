<template>
  <div class="ad-group-negative-manager">
    <div class="mb-8px">
      <el-radio-group v-model="activeNegativeType" size="small" class="compact-radio">
        <el-radio-button label="keyword" :disabled="localProducts.length > 0">
          否定关键词 ({{ localKeywords.length }})
        </el-radio-button>
        <el-radio-button label="product" :disabled="localKeywords.length > 0">
          否定商品 ({{ localProducts.length }})
        </el-radio-button>
      </el-radio-group>
    </div>

    <div class="p-10px border-1 border-gray-200 border-solid rounded-4px bg-gray-50 min-h-80px">
      <!-- 否定关键词表格 -->
      <div v-if="activeNegativeType === 'keyword'">
        <div class="flex items-center mb-6px">
          <span class="mr-8px text-12px font-bold">否定关键词</span>
          <el-button size="small" :disabled="disabled" @click="addNegativeKeyword" class="add-btn ml-10px">
            <el-icon><Plus /></el-icon>
            <span class="ml-4px">添加</span>
          </el-button>
        </div>
        <el-table :data="localKeywords" border size="small" empty-text="暂无否定关键词">
          <el-table-column label="关键词" prop="keywordText">
            <template #default="{ row }">
              <div v-if="row.keywordId" class="text-12px py-2px font-medium">{{ row.keywordText }}</div>
              <el-input 
                v-else 
                v-model="row.keywordText" 
                size="small" 
                placeholder="关键词" 
                :disabled="disabled" 
                @change="syncToConfig" 
                class="compact-input"
              />
            </template>
          </el-table-column>
          <el-table-column label="匹配类型" width="150">
            <template #default="{ row }">
              <div v-if="row.keywordId" class="text-12px py-2px">{{ row.matchType }}</div>
              <el-select v-else v-model="row.matchType" size="small" :disabled="disabled" @change="syncToConfig">
                <el-option label="精准" value="NEGATIVE_EXACT" />
                <el-option label="短语" value="NEGATIVE_PHRASE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" :disabled="disabled" @click="removeNegativeKeyword($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 否定商品表格 -->
      <div v-else-if="activeNegativeType === 'product'">
        <div class="flex items-center mb-10px">
          <span class="mr-10px text-13px font-bold">否定商品</span>
          <el-button size="small" :disabled="disabled" @click="addNegativeProduct" class="add-btn ml-10px">
            <el-icon><Plus /></el-icon>
            <span class="ml-4px">添加</span>
          </el-button>
        </div>
        <el-table :data="localProducts" border size="small" empty-text="暂无否定商品">
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <div v-if="row.targetId" class="text-13px py-4px">
                {{ row.expression[0].type === 'ASIN_SAME_AS' ? '商品 (ASIN)' : '品牌' }}
              </div>
              <el-select v-else v-model="row.expression[0].type" size="small" :disabled="disabled" @change="syncToConfig">
                <el-option label="商品 (ASIN)" value="ASIN_SAME_AS" />
                <el-option label="品牌" value="ASIN_BRAND_SAME_AS" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="值 (ASIN/品牌)" prop="expression">
            <template #default="{ row }">
              <div v-if="row.targetId" class="text-13px py-4px font-medium">
                {{ row.resolvedExpression?.[0]?.value || row.expression[0].value }}
              </div>
              <el-input 
                v-else
                v-model="row.expression[0].value" 
                size="small" 
                :placeholder="row.expression[0].type === 'ASIN_SAME_AS' ? '请输入 ASIN' : '请输入品牌 ID'"
                :disabled="disabled" 
                @change="syncToConfig" 
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" :disabled="disabled" @click="removeNegativeProduct($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps<{
  config: any
  disabled?: boolean
}>()

const emit = defineEmits(['update'])

const activeNegativeType = ref('keyword')
const localKeywords = ref<any[]>([])
const localProducts = ref<any[]>([])

const isKeywordTabDisabled = computed(() => props.disabled || localProducts.value.length > 0)
const isProductTabDisabled = computed(() => props.disabled || localKeywords.value.length > 0)

watch(() => props.config, (val) => {
  if (!val) return
  // 只有当本地列表为空时，才从 props 初始化，避免编辑过程中的覆盖
  if (localKeywords.value.length === 0) {
    localKeywords.value = JSON.parse(JSON.stringify(val.amz_negative_keyword || []))
  }
  if (localProducts.value.length === 0) {
    localProducts.value = JSON.parse(JSON.stringify(val.amz_negative_target_clause || []))
  }
}, { immediate: true, deep: true })

const syncToConfig = () => {
  const newConfig = { ...props.config }
  // 不要在同步时过滤空行，否则会导致刚添加的空行被 watch 刷掉
  // 过滤逻辑应该放在最终提交前，或者让后端处理
  newConfig.amz_negative_keyword = localKeywords.value
  newConfig.amz_negative_target_clause = localProducts.value
  emit('update', newConfig)
}

const addNegativeKeyword = () => {
  localKeywords.value.push({ keywordText: '', matchType: 'NEGATIVE_EXACT', state: 'ENABLED' })
}

const removeNegativeKeyword = (index: number) => {
  localKeywords.value.splice(index, 1)
  syncToConfig()
}

const addNegativeProduct = () => {
  localProducts.value.push({
    state: 'ENABLED',
    expression: [{ type: 'ASIN_SAME_AS', value: '' }]
  })
}

const removeNegativeProduct = (index: number) => {
  localProducts.value.splice(index, 1)
  syncToConfig()
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
