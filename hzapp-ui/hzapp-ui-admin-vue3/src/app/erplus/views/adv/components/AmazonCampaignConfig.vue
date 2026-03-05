<template>
  <div class="amazon-campaign-config">
    <el-form :model="modelValue" label-width="140px" :disabled="disabled">
      <!-- 1. 竞价策略 -->
      <el-divider content-position="left">竞价策略</el-divider>
      <el-form-item label="竞价策略">
        <el-select 
          :model-value="modelValue.dynamicBidding?.strategy" 
          placeholder="请选择竞价策略"
          class="w-300px"
          @update:model-value="val => updateBidding('strategy', val)"
        >
          <el-option label="基于规则 (ROAS)" value="RULE_BASED_BIDDING" />
          <el-option label="动态竞价 - 只降低" value="LEGACY_FOR_SALES" />
          <el-option label="动态竞价 - 提高和降低" value="AUTO_FOR_SALES" />
          <el-option label="固定竞价" value="MANUAL" />
        </el-select>
      </el-form-item>
      
      <el-form-item v-if="modelValue.dynamicBidding?.strategy === 'RULE_BASED_BIDDING'" label="目标 ROAS">
        <el-input-number 
          :model-value="modelValue.dynamicBidding?.rules?.[0]?.value" 
          :precision="2" 
          :step="0.1" 
          :min="0.1"
          @change="val => updateBiddingRule(val)"
        />
      </el-form-item>

      <el-form-item label="优化规则">
        <AdsOptimizationRuleSelect 
          :model-value="modelValue.optimizationRuleId"
          :account-id="modelValue.accountId"
          :profile-id="modelValue.profileId"
          placeholder="请选择或创建优化规则"
          @update:model-value="val => updateModelValue('optimizationRuleId', val)"
        />
      </el-form-item>

      <!-- 2. 分位置竞价调整 -->
      <el-divider content-position="left">分位置竞价调整</el-divider>
      <div class="mb-20px">
        <el-table :data="placementData" border style="width: 100%">
          <el-table-column label="位置" prop="label" width="180" />
          <el-table-column label="竞价调整 (%)">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.percentage" 
                :min="0" 
                :max="900" 
                :step="10" 
                size="small"
                @change="handlePercentageChange(row)"
              /> %
            </template>
          </el-table-column>
          <el-table-column label="目标出价">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.targetBid"
                :precision="2" 
                :step="0.01" 
                :min="0" 
                size="small"
                @change="handleTargetBidChange"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 3. 否定投放规则 (活动级) -->
      <el-divider content-position="left">否定投放 (活动级)</el-divider>
      <div class="mb-20px p-15px border-1 border-gray-200 border-solid rounded-4px bg-gray-50">
        <div class="flex items-center mb-10px">
          <span class="mr-10px">否定关键词</span>
          <el-button type="primary" link @click="addNegativeKeyword">添加</el-button>
        </div>
        <el-table :data="localNegativeKeywords" border size="small">
          <el-table-column label="关键词" prop="keywordText">
            <template #default="{ row }">
              <el-input v-model="row.keywordText" size="small" @change="updateNegativeKeywords" />
            </template>
          </el-table-column>
          <el-table-column label="匹配类型" width="150">
            <template #default="{ row }">
              <el-select v-model="row.matchType" size="small" @change="updateNegativeKeywords">
                <el-option label="否定精准" value="NEGATIVE_EXACT" />
                <el-option label="否定短语" value="NEGATIVE_PHRASE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link @click="removeNegativeKeyword($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 4. 广告组配置 -->
      <el-divider content-position="left">广告组设置</el-divider>
      <div v-loading="loadingGroups">
        <el-tabs v-if="adGroups.length > 0" v-model="activeAdGroupTab" type="card">
          <el-tab-pane 
            v-for="group in adGroups" 
            :key="group.id" 
            :label="group.name" 
            :name="group.id.toString()"
          >
            <div class="p-15px border-1 border-gray-100 border-solid rounded-4px">
              <div class="flex items-center mb-15px">
                <span class="mr-20px">状态: 
                  <el-tag :type="group.status === 'ENABLED' ? 'success' : 'info'" size="small">
                    {{ group.status === 'ENABLED' ? '启用' : '暂停' }}
                  </el-tag>
                </span>
                <span class="mr-10px">默认竞价:</span>
                <el-input-number 
                  :model-value="groupBids[group.id]" 
                  :precision="2" 
                  :step="0.01" 
                  :min="0.02" 
                  size="small"
                  @change="val => handleGroupBidChange(group.id, val)"
                />
              </div>

              <el-tabs v-model="activeTargetingTab" class="mt-10px">
                <el-tab-pane label="投放数据" name="targeting">
                  <div class="flex justify-between items-center mb-10px">
                    <div class="flex items-center">
                      <span class="text-14px font-bold mr-10px">定向类型:</span>
                      <el-radio-group 
                        v-model="activeGroupExtConfig.targetingType" 
                        size="small"
                        :disabled="!!activeGroupExtConfig.targetingType || activeGroupKeywords.length > 0 || activeGroupProducts.length > 0"
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
                      v-if="activeGroupExtConfig.targetingType"
                      type="primary" 
                      size="small" 
                      link 
                      @click="addActiveTargeting"
                    >
                      + 添加{{ activeGroupExtConfig.targetingType === 'KEYWORD' ? '关键词' : '商品/类目' }}
                    </el-button>
                  </div>

                  <!-- 互斥显示 -->
                  <template v-if="activeGroupExtConfig.targetingType === 'KEYWORD'">
                    <el-table :data="activeGroupKeywords" border size="small" style="width: 100%">
                      <el-table-column label="关键词" prop="keywordText">
                        <template #default="{ row }">
                          <el-input v-model="row.keywordText" size="small" placeholder="输入关键词" @change="syncActiveGroupToModel" />
                        </template>
                      </el-table-column>
                      <el-table-column label="匹配类型" prop="matchType" width="150">
                        <template #default="{ row }">
                          <el-select v-model="row.matchType" size="small" @change="syncActiveGroupToModel">
                            <el-option label="EXACT" value="EXACT" />
                            <el-option label="PHRASE" value="PHRASE" />
                            <el-option label="BROAD" value="BROAD" />
                          </el-select>
                        </template>
                      </el-table-column>
                      <el-table-column label="出价" prop="bid" width="120">
                        <template #default="{ row }">
                          <el-input-number v-model="row.bid" :precision="2" :step="0.01" size="small" style="width: 100%" @change="syncActiveGroupToModel" />
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="60" align="center">
                        <template #default="{ $index }">
                          <el-button type="danger" :icon="Delete" circle size="small" @click="removeActiveTargeting($index)" />
                        </template>
                      </el-table-column>
                    </el-table>
                  </template>

                  <template v-else-if="activeGroupExtConfig.targetingType === 'PRODUCT'">
                    <el-table :data="activeGroupProducts" border size="small" style="width: 100%">
                      <el-table-column label="操作/类型" prop="expressionType" width="180">
                        <template #default="{ row }">
                          <el-select v-model="row.expressionType" size="small" placeholder="类型" @change="syncActiveGroupToModel">
                            <el-option label="ASIN (asinSameAs)" value="asinSameAs" />
                            <el-option label="类目 (category)" value="category" />
                            <el-option label="品牌 (brand)" value="brand" />
                          </el-select>
                        </template>
                      </el-table-column>
                      <el-table-column label="值 (ASIN / ID)" prop="expressionValue">
                        <template #default="{ row }">
                          <el-input v-model="row.expressionValue" size="small" placeholder="输入 ASIN 或 ID" @change="syncActiveGroupToModel" />
                        </template>
                      </el-table-column>
                      <el-table-column label="出价" prop="bid" width="120">
                        <template #default="{ row }">
                          <el-input-number v-model="row.bid" :precision="2" :step="0.01" size="small" style="width: 100%" @change="syncActiveGroupToModel" />
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="60" align="center">
                        <template #default="{ $index }">
                          <el-button type="danger" :icon="Delete" circle size="small" @click="removeActiveTargeting($index)" />
                        </template>
                      </el-table-column>
                    </el-table>
                  </template>

                  <el-empty v-else description="请先选择定向类型" :image-size="40" />
                </el-tab-pane>

                <el-tab-pane label="否定数据" name="negative">
                  <div class="flex justify-between items-center mb-10px">
                    <el-radio-group v-model="activeNegativeType" size="small">
                      <el-radio-button label="keyword">否定关键词 ({{ activeGroupNegativeKeywords.length }})</el-radio-button>
                      <el-radio-button label="product">否定商品 ({{ activeGroupNegativeProducts.length }})</el-radio-button>
                    </el-radio-group>
                    <el-button type="primary" size="small" link @click="addActiveNegative">
                      + 添加{{ activeNegativeType === 'keyword' ? '否定词' : '否定商品' }}
                    </el-button>
                  </div>

                  <!-- 否定关键词表格 -->
                  <el-table v-if="activeNegativeType === 'keyword'" :data="activeGroupNegativeKeywords" border size="small">
                    <el-table-column label="否定关键词" prop="keywordText">
                      <template #default="{ row }">
                        <el-input v-model="row.keywordText" size="small" placeholder="输入否定词" @change="syncActiveGroupToModel" />
                      </template>
                    </el-table-column>
                    <el-table-column label="匹配类型" prop="matchType" width="180">
                      <template #default="{ row }">
                        <el-select v-model="row.matchType" size="small" @change="syncActiveGroupToModel">
                          <el-option label="NEGATIVE_EXACT" value="NEGATIVE_EXACT" />
                          <el-option label="NEGATIVE_PHRASE" value="NEGATIVE_PHRASE" />
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="60" align="center">
                      <template #default="{ $index }">
                        <el-button type="danger" :icon="Delete" circle size="small" @click="removeActiveNegative($index)" />
                      </template>
                    </el-table-column>
                  </el-table>

                  <!-- 否定商品表格 -->
                  <el-table v-else :data="activeGroupNegativeProducts" border size="small">
                    <el-table-column label="否定商品 (ASIN/SKU)" prop="asin">
                      <template #default="{ row }">
                        <el-input v-model="row.asin" size="small" placeholder="输入 ASIN" @change="syncActiveGroupToModel" />
                      </template>
                    </el-table-column>
                    <el-table-column label="品牌ID (可选)" prop="brandId" width="150">
                      <template #default="{ row }">
                        <el-input v-model="row.brandId" size="small" placeholder="品牌 ID" @change="syncActiveGroupToModel" />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="60" align="center">
                      <template #default="{ $index }">
                        <el-button type="danger" :icon="Delete" circle size="small" @click="removeActiveNegative($index)" />
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-tab-pane>
        </el-tabs>
        <el-empty v-if="adGroups.length === 0" description="暂无广告组" />
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { AdsAdGroupApi } from '@/app/erplus/api/adv/ads'
import { AdsAdGroup } from '../types/ads'
import AdsOptimizationRuleSelect from './AdsOptimizationRuleSelect.vue'
import { QuestionFilled, Delete } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue: any
  campaignId: number
  disabled?: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const loadingGroups = ref(false)
const adGroups = ref<AdsAdGroup[]>([])
const groupBids = ref<Record<number, number>>({})
const activeAdGroupTab = ref('')
const activeTargetingTab = ref('targeting')
const activeNegativeType = ref('keyword')
const localNegativeKeywords = ref<any[]>([])

const activeAdGroup = computed(() => {
  return Array.isArray(adGroups.value) ? adGroups.value.find(g => g.id.toString() === activeAdGroupTab.value) : null
})

const activeGroupExtConfig = computed(() => activeAdGroup.value?.extData?.platformConfig || {})
const activeGroupKeywords = computed(() => activeGroupExtConfig.value.keywordTargetings || [])
const activeGroupProducts = computed(() => activeGroupExtConfig.value.productTargetings || [])
const activeGroupNegativeKeywords = computed(() => activeGroupExtConfig.value.negativeKeywords || [])
const activeGroupNegativeProducts = computed(() => activeGroupExtConfig.value.negativeTargetings || [])

const ensureActiveGroupConfig = () => {
  const group = adGroups.value.find(g => g.id.toString() === activeAdGroupTab.value)
  if (!group) return null
  if (!group.extData) {
    group.extData = { platformConfig: {} }
  } else if (!group.extData.platformConfig) {
    group.extData.platformConfig = {}
  }
  return group.extData.platformConfig
}

const placementData = ref([
  { type: 'PLACEMENT_TOP', label: '首页 (搜索顶部)', percentage: 0, targetBid: 0 },
  { type: 'PLACEMENT_PRODUCT_PAGE', label: '商品详情页', percentage: 0, targetBid: 0 },
  { type: 'PLACEMENT_REST_OF_SEARCH', label: '搜索其他位置', percentage: 0, targetBid: 0 }
])

const handleTargetingTypeChange = (val: any) => {
  const config = ensureActiveGroupConfig()
  if (!config) return
  config.targetingType = val
  if (val === 'KEYWORD') {
    config.productTargetings = []
  } else if (val === 'PRODUCT') {
    config.keywordTargetings = []
  }
  syncActiveGroupToModel()
}

const addActiveTargeting = () => {
  const config = ensureActiveGroupConfig()
  if (!config) return
  
  const type = config.targetingType
  if (type === 'KEYWORD') {
    if (!config.keywordTargetings) config.keywordTargetings = []
    config.keywordTargetings.push({ keywordText: '', matchType: 'EXACT', bid: activeAdGroup.value?.defaultBid || 1.0 })
  } else if (type === 'PRODUCT') {
    if (!config.productTargetings) config.productTargetings = []
    config.productTargetings.push({ expressionType: 'asinSameAs', expressionValue: '', bid: activeAdGroup.value?.defaultBid || 1.0 })
  }
  syncActiveGroupToModel()
}

const removeActiveTargeting = (index: number) => {
  const type = activeGroupExtConfig.value.targetingType
  if (type === 'KEYWORD') {
    activeGroupKeywords.value.splice(index, 1)
  } else if (type === 'PRODUCT') {
    activeGroupProducts.value.splice(index, 1)
  }
  syncActiveGroupToModel()
}

const addActiveNegative = () => {
  const config = ensureActiveGroupConfig()
  if (!config) return

  if (activeNegativeType.value === 'keyword') {
    if (!config.negativeKeywords) config.negativeKeywords = []
    config.negativeKeywords.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' })
  } else {
    if (!config.negativeTargetings) config.negativeTargetings = []
    config.negativeTargetings.push({ asin: '', brandId: '' })
  }
  syncActiveGroupToModel()
}

const removeActiveNegative = (index: number) => {
  if (activeNegativeType.value === 'keyword') {
    activeGroupNegativeKeywords.value.splice(index, 1)
  } else {
    activeGroupNegativeProducts.value.splice(index, 1)
  }
  syncActiveGroupToModel()
}

const syncActiveGroupToModel = () => {
  if (!activeAdGroup.value) return
  handleGroupConfigUpdate(activeAdGroup.value.id, activeGroupExtConfig.value)
}

const handleGroupConfigUpdate = (groupId: number, config: any) => {
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === groupId)
  
  if (index > -1) {
    currentGroups[index] = { ...currentGroups[index], extData: { platformConfig: config } }
  } else {
    currentGroups.push({ id: groupId, extData: { platformConfig: config } })
  }
  
  updateModelValue('adGroups', currentGroups)
}

// 初始化分位置竞价数据
watch(() => props.modelValue.dynamicBidding?.placementBidding, (adjustments) => {
  if (adjustments) {
    placementData.value.forEach(item => {
      const adj = adjustments.find(a => a.placement === item.type)
      item.percentage = adj ? adj.percentage : 0
      // 目标出价不随竞价调整比例而变化，仅初始化为 0 或保留手动输入值
      if (item.targetBid === undefined) {
        item.targetBid = 0
      }
    })
  }
}, { immediate: true })

// Sync negative keywords from props to local ref
watch(() => props.modelValue.negativeKeywords, (val) => {
  localNegativeKeywords.value = JSON.parse(JSON.stringify(val || []))
}, { immediate: true })

const handlePercentageChange = (row: any) => {
  updatePlacement(row.type, row.percentage)
}

const handleTargetBidChange = () => {
  // Manual reference, no sync back
}

const updateBidding = (key: string, value: any) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}) }
  dynamicBidding[key] = value
  updateModelValue('dynamicBidding', dynamicBidding)
}

const updateBiddingRule = (val: any) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}) }
  const rules = [...(dynamicBidding.rules || [])]
  if (rules.length === 0) {
    rules.push({ ruleType: 'BID', value: val })
  } else {
    rules[0] = { ...rules[0], value: val }
  }
  dynamicBidding.rules = rules
  updateModelValue('dynamicBidding', dynamicBidding)
}

const updatePlacement = (type: string, val: number) => {
  const dynamicBidding = { ...(props.modelValue.dynamicBidding || {}) }
  const adjustments = [...(dynamicBidding.placementBidding || [])]
  const index = adjustments.findIndex(a => a.placement === type)
  if (index > -1) {
    adjustments[index] = { placement: type, percentage: val }
  } else {
    adjustments.push({ placement: type, percentage: val })
  }
  dynamicBidding.placementBidding = adjustments
  updateModelValue('dynamicBidding', dynamicBidding)
}

const addNegativeKeyword = () => {
  localNegativeKeywords.value.push({ keywordText: '', matchType: 'NEGATIVE_EXACT' })
  updateNegativeKeywords()
}

const removeNegativeKeyword = (index: number) => {
  localNegativeKeywords.value.splice(index, 1)
  updateNegativeKeywords()
}

const updateNegativeKeywords = () => {
  updateModelValue('negativeKeywords', [...localNegativeKeywords.value])
}

const fetchAdGroups = async () => {
  if (!props.campaignId) return
  loadingGroups.value = true
  try {
    const res = await AdsAdGroupApi.getAdGroupPage({
      campaignIds: [props.campaignId],
      pageSize: 100
    })
    adGroups.value = res.list
    if (adGroups.value.length > 0 && !activeAdGroupTab.value) {
      activeAdGroupTab.value = adGroups.value[0].id.toString()
    }
    adGroups.value.forEach(group => {
      groupBids.value[group.id] = group.defaultBid || 0
    })
    
    if (!props.modelValue.adGroups) {
      updateModelValue('adGroups', [])
    }
  } finally {
    loadingGroups.value = false
  }
}

const handleGroupBidChange = (groupId: number, val: number | undefined) => {
  if (val === undefined) return
  groupBids.value[groupId] = val
  const currentGroups = [...(props.modelValue.adGroups || [])]
  const index = currentGroups.findIndex(g => g.id === groupId)
  
  if (index > -1) {
    currentGroups[index] = { ...currentGroups[index], defaultBid: val }
  } else {
    currentGroups.push({ id: groupId, defaultBid: val })
  }
  
  updateModelValue('adGroups', currentGroups)
}

const updateModelValue = (key: string, value: any) => {
  emit('update:modelValue', {
    ...props.modelValue,
    [key]: value
  })
}

watch(() => props.campaignId, () => {
  fetchAdGroups()
}, { immediate: true })

onMounted(() => {
  fetchAdGroups()
})
</script>

<style scoped>
.amazon-campaign-config {
  width: 100%;
}
</style>
